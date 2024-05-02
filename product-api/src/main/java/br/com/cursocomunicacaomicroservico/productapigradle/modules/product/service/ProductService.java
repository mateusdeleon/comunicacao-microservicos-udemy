package br.com.cursocomunicacaomicroservico.productapigradle.modules.product.service;

import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.SuccessResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.ValidationException;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.service.CategoryService;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.dto.*;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.model.Product;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.repository.ProductRepository;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.sales.client.SalesClient;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.sales.dto.SalesConfirmationDTO;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.sales.dto.SalesProductResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.sales.enums.SalesStatus;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.sales.rabbitmq.SalesConfirmationSender;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.service.SupplierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static br.com.cursocomunicacaomicroservico.productapigradle.config.RequestUtil.getCurrentRequest;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class ProductService {

    private static final Integer ZERO = 0;
    private static final String AUTHORIZATION = "Authorization";
    private static final String TRANSACTION_ID = "transactionid";
    private static final String SERVICE_ID = "serviceid";

    private final ProductRepository productRepository;
    private final SupplierService supplierService;
    private final CategoryService categoryService;
    private final SalesConfirmationSender salesConfirmationSender;
    private final SalesClient salesClient;
    private final ObjectMapper objectMapper;

    public ProductResponse findByIdProduct(Integer id) {
        return ProductResponse.of(findById(id));
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::of)
                .toList();
    }

    public List<ProductResponse> findBySupplierId(Integer supplierId) {
        if (isEmpty(supplierId)) {
            throw new ValidationException("the product' supplier was not informed.");
        }

        return productRepository.findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::of)
                .toList();
    }

    public List<ProductResponse> findByCategoryId(Integer categoryId) {
        if (isEmpty(categoryId)) {
            throw new ValidationException("the product' category was not informed.");
        }

        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(ProductResponse::of)
                .toList();
    }

    public List<ProductResponse> findByName(String name) {
        if (isEmpty(name)) {
            throw new ValidationException("the product name was not informed.");
        }

        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductResponse::of)
                .toList();
    }

    public Product findById(Integer id) {
        validateInformedId(id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ValidationException("There's no product for the given id."));
    }

    public Boolean existsByCategoryId(Integer categoryId) {
        return productRepository.existsByCategoryId(categoryId);
    }

    public Boolean existsBySupplierId(Integer supplierId) {
        return productRepository.existsBySupplierId(supplierId);
    }

    public ProductResponse save(ProductRequest productRequest) {
        validateProductData(productRequest);
        validateCategoryAndSupplierId(productRequest);
        var category = categoryService.findById(productRequest.getCategoryId());
        var supplier = supplierService.findById(productRequest.getSupplierId());

        var product = productRepository.save(Product.of(productRequest, category, supplier));
        return ProductResponse.of(product);
    }

    public ProductResponse update(ProductRequest productRequest, Integer id) {
        validateProductData(productRequest);
        validateInformedId(id);
        validateCategoryAndSupplierId(productRequest);
        var category = categoryService.findById(productRequest.getCategoryId());
        var supplier = supplierService.findById(productRequest.getSupplierId());
        var product = Product.of(productRequest, category, supplier);
        product.setId(id);
        productRepository.save(product);
        return ProductResponse.of(product);
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);
        if (!productRepository.existsById(id)) {
            throw new ValidationException("Product does not exists.");
        }
        var sales = getSalesByProductId(id);
        if (!isEmpty(sales.getSalesIds())) {
            throw new ValidationException("Product cannot be deleted, there are sales for it.");
        }
        productRepository.deleteById(id);
        return SuccessResponse.create("the product was deleted.");
    }

    private void validateInformedId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("the product id must be informed.");
        }
    }

    private void validateProductData(ProductRequest productRequest) {
        if (isEmpty(productRequest.getName())) {
            throw new ValidationException("The product name was not informed.");
        }

        if (isEmpty(productRequest.getQuantityAvailable())) {
            throw new ValidationException("The product quantity was not informed.");
        }

        if (productRequest.getQuantityAvailable() <= ZERO) {
            throw new ValidationException("The quantity should not be less or equal to zero.");
        }
    }

    private void validateCategoryAndSupplierId(ProductRequest productRequest) {
        if (isEmpty(productRequest.getCategoryId())) {
            throw new ValidationException("The category id was not informed.");
        }

        if (isEmpty(productRequest.getSupplierId())) {
            throw new ValidationException("The supplier id was not informed.");
        }
    }

    public void updateProductStock(ProductStockDTO productStockDTO) {
        try {
            validateStockUpdateData(productStockDTO);
            updateStock(productStockDTO);
        } catch (Exception ex) {
            log.error("Error while trying to update stock for message with error: {}", ex.getMessage(), ex);
            var rejectedMessage = new SalesConfirmationDTO(
                    productStockDTO.getSalesId(), SalesStatus.RJECTED, productStockDTO.getTransactionid());
            salesConfirmationSender.sendSalesConfirmationMessage(rejectedMessage);
        }
    }

    private void validateStockUpdateData(ProductStockDTO productStockDTO) {
        if (isEmpty(productStockDTO) || isEmpty(productStockDTO.getSalesId())) {
            throw new ValidationException("Product data and sales id must be informed.");
        }
        if (isEmpty(productStockDTO.getProducts())) {
            throw new ValidationException("sales' product must be informed.");
        }
        productStockDTO
                .getProducts()
                .forEach(salesProduct -> {
                    if (isEmpty(salesProduct.getQuantity()) || isEmpty(salesProduct.getProductId())) {
                        throw new ValidationException("Product id and quantity must be informed.");
                    }
                });
    }

    @Transactional
    protected void updateStock(ProductStockDTO productStockDTO) {
        var productsForUpdate = new ArrayList<Product>();
        productStockDTO.getProducts()
                .forEach(salesProduct -> {
                    var existingProduct = findById(salesProduct.getProductId());
                    validateQuantityInStock(salesProduct, existingProduct);
                    existingProduct.updateStock(salesProduct.getQuantity());
                    productsForUpdate.add(existingProduct);
                });
        if (!isEmpty(productsForUpdate)) {
            productRepository.saveAll(productsForUpdate);
            var approvedMessage = new SalesConfirmationDTO(
                    productStockDTO.getSalesId(), SalesStatus.APPROVED, productStockDTO.getTransactionid());
            salesConfirmationSender.sendSalesConfirmationMessage(approvedMessage);
        }
    }

    private void validateQuantityInStock(ProductQuantityDTO salesProduct, Product existingProduct) {
        if (salesProduct.getQuantity() > existingProduct.getQuantityAvailable()) {
            throw new ValidationException(
                    String.format("Product %s is out of stock.", existingProduct.getId()));
        }
    }

    public ProductSalesResponse findProductSales(Integer id) {
        var product = findById(id);
        var sales = getSalesByProductId(product.getId());
        return ProductSalesResponse.of(product, sales.getSalesIds());
    }

    private SalesProductResponse getSalesByProductId(Integer productId) {
        try {
            var currentRequest = getCurrentRequest();
            var token = currentRequest.getHeader(AUTHORIZATION);
            var transactionid = currentRequest.getHeader(TRANSACTION_ID);
            var serviceid = currentRequest.getAttribute(SERVICE_ID);
            log.info("Request to GET orders by productId with data {} | [transactionId: {} | serviceId: {}]",
                    productId, transactionid, serviceid);
            var response = salesClient
                    .findSalesByProductId(productId, token, transactionid)
                    .orElseThrow(() -> new ValidationException("Sales not found by this product."));
            log.info("Response to GET orders by productId with data {} | [transactionId: {} | serviceId: {}]",
                    objectMapper.writeValueAsString(response), transactionid, serviceid);
            return response;
        } catch (Exception ex) {
            log.error("Error trying to call Sales-API: {}", ex.getMessage());
            throw new ValidationException("There was an error trying to get the product's sales.");
        }
    }

    public SuccessResponse checkProductsStock(ProductCheckStockRequest productCheckStockRequest) {
        try {
            var currentRequest = getCurrentRequest();
            var transactionid = currentRequest.getHeader(TRANSACTION_ID);
            var serviceid = currentRequest.getAttribute(SERVICE_ID);
            log.info("Request to POST product stock with data {} | [transactionId: {} | serviceId: {}]",
                    objectMapper.writeValueAsString(productCheckStockRequest),
                    transactionid,
                    serviceid);
            if (isEmpty(productCheckStockRequest) || isEmpty(productCheckStockRequest.getProducts())) {
                throw new ValidationException("The request data must be informed.");
            }
            productCheckStockRequest
                    .getProducts()
                    .forEach(this::validateStock);
            var response = SuccessResponse.create("The stock is ok.");
            log.info("Response to POST product stock with data {} | [transactionId: {} | serviceId: {}]",
                    objectMapper.writeValueAsString(response),
                    transactionid,
                    serviceid);
            return response;
        } catch (Exception ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    private void validateStock(ProductQuantityDTO productQuantityDTO) {
        if (isEmpty(productQuantityDTO.getProductId()) || isEmpty(productQuantityDTO.getQuantity())) {
            throw new ValidationException("Product id and quantity must be informed.");
        }
        var product = findById(productQuantityDTO.getProductId());
        if (productQuantityDTO.getQuantity() > product.getQuantityAvailable()) {
            throw new ValidationException(String.format("The product %s is out of stock.", product.getId()));
        }
    }

}

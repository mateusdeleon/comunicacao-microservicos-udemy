package br.com.cursocomunicacaomicroservico.productapigradle.modules.product.service;

import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.SuccessResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.ValidationException;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.service.CategoryService;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.dto.ProductRequest;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.dto.ProductResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.model.Product;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.repository.ProductRepository;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CategoryService categoryService;

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

}

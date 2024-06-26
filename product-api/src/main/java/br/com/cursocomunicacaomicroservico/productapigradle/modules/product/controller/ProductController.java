package br.com.cursocomunicacaomicroservico.productapigradle.modules.product.controller;

import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.SuccessResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.dto.ProductCheckStockRequest;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.dto.ProductRequest;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.dto.ProductResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.dto.ProductSalesResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ProductResponse save(@RequestBody ProductRequest productRequest) {
        return productService.save(productRequest);
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @GetMapping("{id}")
    public ProductResponse findById(@PathVariable Integer id) {
        return productService.findByIdProduct(id);
    }

    @GetMapping("name/{name}")
    public List<ProductResponse> findByName(@PathVariable String name) {
        return productService.findByName(name);
    }

    @GetMapping("category/{categoryId}")
    public List<ProductResponse> findByCategory(@PathVariable Integer categoryId) {
        return productService.findByCategoryId(categoryId);
    }

    @GetMapping("supplier/{supplierId}")
    public List<ProductResponse> findBySupplier(@PathVariable Integer supplierId) {
        return productService.findBySupplierId(supplierId);
    }

    @PutMapping("{id}")
    public ProductResponse update(@RequestBody ProductRequest productRequest, @PathVariable Integer id) {
        return productService.update(productRequest, id);
    }

    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return productService.delete(id);
    }

    @PostMapping("check-stock")
    public SuccessResponse checkProductsStock(@RequestBody ProductCheckStockRequest productCheckStockRequest) {
        return productService.checkProductsStock(productCheckStockRequest);
    }

    @GetMapping("{productId}/sales")
    public ProductSalesResponse findProductSales(@PathVariable Integer productId) {
        return productService.findProductSales(productId);
    }

}

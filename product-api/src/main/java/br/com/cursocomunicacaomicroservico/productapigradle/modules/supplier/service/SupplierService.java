package br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.service;

import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.SuccessResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.ValidationException;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.service.ProductService;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.dto.SupplierRequest;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.dto.SupplierResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.model.Supplier;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@AllArgsConstructor(onConstructor_ = {@Lazy})
public class SupplierService {

    private final SupplierRepository supplierRepository;
    @Lazy
    private final ProductService productService;

    public SupplierResponse findByIdSupplier(Integer id) {
        return SupplierResponse.of(findById(id));
    }

    public List<SupplierResponse> findAll() {
        return supplierRepository.findAll().stream()
                .map(SupplierResponse::of)
                .toList();
    }

    public List<SupplierResponse> findByName(String name) {
        if (isEmpty(name)) {
            throw new ValidationException("the supplier name was not informed.");
        }

        return supplierRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(SupplierResponse::of)
                .toList();
    }

    public Supplier findById(Integer id) {
        validateInformedId(id);
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ValidationException("There's no supplier for the given id."));
    }

    public SupplierResponse save(SupplierRequest supplierRequest) {
        validateSupplierName(supplierRequest);
        var supplier = supplierRepository.save(Supplier.of(supplierRequest));
        return SupplierResponse.of(supplier);
    }

    public SupplierResponse update(SupplierRequest supplierRequest, Integer id) {
        validateSupplierName(supplierRequest);
        validateInformedId(id);
        var supplier = Supplier.of(supplierRequest);
        supplier.setId(id);
        supplierRepository.save(supplier);
        return SupplierResponse.of(supplier);
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);
        if (productService.existsBySupplierId(id)) {
            throw new ValidationException("you cannot delete this supplier because it's already defined by a product.");
        }
        supplierRepository.deleteById(id);
        return SuccessResponse.create("the supplier was deleted.");
    }

    private void validateSupplierName(SupplierRequest supplierRequest) {
        if (isEmpty(supplierRequest.getName())) {
            throw new ValidationException("the supplier name was not informed.");
        }
    }

    private void validateInformedId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("the supplier id must be informed.");
        }
    }

}

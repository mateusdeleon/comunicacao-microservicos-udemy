package br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.controller;

import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.SuccessResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.dto.SupplierRequest;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.dto.SupplierResponse;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/supplier")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public SupplierResponse save(@RequestBody SupplierRequest supplierRequest) {
        return supplierService.save(supplierRequest);
    }

    @GetMapping
    public List<SupplierResponse> findAll() {
        return supplierService.findAll();
    }

    @GetMapping("{id}")
    public SupplierResponse findById(@PathVariable Integer id) {
        return supplierService.findByIdSupplier(id);
    }

    @GetMapping("name/{name}")
    public List<SupplierResponse> findByName(@PathVariable String name) {
        return supplierService.findByName(name);
    }

    @PutMapping("{id}")
    public SupplierResponse update(@RequestBody SupplierRequest supplierRequest, @PathVariable Integer id) {
        return supplierService.update(supplierRequest, id);
    }

    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return supplierService.delete(id);
    }

}

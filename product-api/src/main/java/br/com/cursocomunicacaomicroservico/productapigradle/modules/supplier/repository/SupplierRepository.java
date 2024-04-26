package br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.repository;

import br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    List<Supplier> findByNameContainingIgnoreCase(String name);

}

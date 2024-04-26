package br.com.cursocomunicacaomicroservico.productapigradle.modules.product.repository;

import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryId(Integer idCategory);
    List<Product> findBySupplierId(Integer idSupplier);
    Boolean existsByCategoryId(Integer idCategory);
    Boolean existsBySupplierId(Integer idSupplier);

}

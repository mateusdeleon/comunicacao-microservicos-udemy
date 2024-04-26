package br.com.cursocomunicacaomicroservico.productapigradle.modules.category.repository;

import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByDescriptionContainingIgnoreCase(String description);
}

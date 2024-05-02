package br.com.cursocomunicacaomicroservico.productapigradle.modules.category.model;

import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.dto.CategoryRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CATEGORY")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    public static Category of(CategoryRequest categoryRequest) {
        var category = new Category();
        BeanUtils.copyProperties(categoryRequest, category);
        return category;
    }

}

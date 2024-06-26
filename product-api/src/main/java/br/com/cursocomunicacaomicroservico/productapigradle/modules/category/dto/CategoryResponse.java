package br.com.cursocomunicacaomicroservico.productapigradle.modules.category.dto;

import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.model.Category;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class CategoryResponse {

    private Integer id;
    private String description;

    public static CategoryResponse of(Category category) {
        var response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);
        return response;
    }

}

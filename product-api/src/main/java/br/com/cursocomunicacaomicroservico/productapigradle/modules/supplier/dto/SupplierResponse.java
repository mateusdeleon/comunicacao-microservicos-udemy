package br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.dto;

import br.com.cursocomunicacaomicroservico.productapigradle.modules.category.model.Category;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.supplier.model.Supplier;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SupplierResponse {

    private Integer id;
    private String name;

    public static SupplierResponse of(Supplier supplier) {
        var response = new SupplierResponse();
        BeanUtils.copyProperties(supplier, response);
        return response;
    }

}

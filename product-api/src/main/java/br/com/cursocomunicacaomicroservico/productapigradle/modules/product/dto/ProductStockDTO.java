package br.com.cursocomunicacaomicroservico.productapigradle.modules.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockDTO {

    private String salesId;
    private List<ProductQuantityDTO> products;

}

package br.com.cursocomunicacaomicroservico.productapigradle.modules.product.rabbitmq;

import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.dto.ProductStockDTO;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.product.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductStockListener {

    @Autowired
    private ProductService productService;

    @RabbitListener(queues = "${app-config.rabbit.queue.product-stock}")
    public void receiveProductStockMessage(ProductStockDTO productStockDTO) throws JsonProcessingException {
        log.info("Recieving message with data: {}  and TransactionID: {}",
                new ObjectMapper().writeValueAsString(productStockDTO),
                productStockDTO.getTransactionid());
        productService.updateProductStock(productStockDTO);
    }

}

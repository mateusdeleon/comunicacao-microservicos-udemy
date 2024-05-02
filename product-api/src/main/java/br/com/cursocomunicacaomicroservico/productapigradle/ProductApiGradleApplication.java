package br.com.cursocomunicacaomicroservico.productapigradle;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class ProductApiGradleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApiGradleApplication.class, args);
	}

}

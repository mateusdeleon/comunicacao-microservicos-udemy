package br.com.cursocomunicacaomicroservico.productapigradle.config.exception;

import lombok.Data;

@Data
public class ExceptionDetails {

    private int status;
    private String message;
}

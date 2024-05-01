package br.com.cursocomunicacaomicroservico.productapigradle.config.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

import static br.com.cursocomunicacaomicroservico.productapigradle.config.RequestUtil.getCurrentRequest;


public class FeignClientAuthInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String TRANSACTION_ID = "transactionid";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        var currentRequest = getCurrentRequest();
        requestTemplate
                .header(AUTHORIZATION, currentRequest.getHeader(AUTHORIZATION))
                .header(TRANSACTION_ID, currentRequest.getHeader(TRANSACTION_ID));
    }
}

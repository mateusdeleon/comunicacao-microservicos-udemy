package br.com.cursocomunicacaomicroservico.productapigradle.config.interceptor;

import br.com.cursocomunicacaomicroservico.productapigradle.config.exception.ValidationException;
import br.com.cursocomunicacaomicroservico.productapigradle.modules.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String TRANSACTION_ID = "transactionid";

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (isOptions(request) || isPublic(request.getRequestURI())) {
            return true;
        }
        if (isEmpty(request.getHeader(TRANSACTION_ID))) {
            throw new ValidationException("the transactionid header is required.");
        }
        var authorization = request.getHeader(AUTHORIZATION);
        jwtService.validateAuthorization(authorization);
        request.setAttribute("serviceid", UUID.randomUUID().toString());
        return true;
    }

    private boolean isPublic(String url) {
        return Urls.PROTECTED_URLS
                .stream()
                .noneMatch(url::contains);
    }

    private boolean isOptions(HttpServletRequest request) {
        return HttpMethod.OPTIONS.name().equals(request.getMethod());
    }
}

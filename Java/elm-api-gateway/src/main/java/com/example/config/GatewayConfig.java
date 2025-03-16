package com.example.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;



//The GatewayConfig.java file is responsible for dynamically adding authentication headers to every request that passes through the Spring Cloud Gateway before forwarding it to other microservices.
//GlobalFilter is a Spring Cloud Gateway filter that applies to all requests passing through the API Gateway.






@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayConfig implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest().mutate()
            .headers(httpHeaders -> {
                httpHeaders.remove(HttpHeaders.COOKIE);
                httpHeaders.set(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("admin:admin123".getBytes()));
            })
            .build();

        return chain.filter(exchange.mutate().request(request).build());
    }
}

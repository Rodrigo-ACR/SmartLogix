package com.smartlogix.bff.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate con @LoadBalanced: resuelve nombres logicos de servicio
     * (ej: http://inventario/productos) consultando el registro de Eureka,
     * y balancea entre instancias si hay mas de una (escalabilidad horizontal).
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

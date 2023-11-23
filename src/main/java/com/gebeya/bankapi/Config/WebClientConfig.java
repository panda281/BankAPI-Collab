package com.gebeya.bankAPI.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }

    @Bean
    public WebClient webClientForOtp(WebClient.Builder webClientBuilder)
    {
        return webClientBuilder.baseUrl("https://sms.yegara.com/api2").build();
    }

    @Bean
    public WebClient webClientForTopUp(WebClient.Builder webClientBuilder)
    {
        return webClientBuilder.baseUrl("http://192.168.1.43:9090/mtelecom").build();
    }
}
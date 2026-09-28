package org.invest.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import static org.invest.constants.TInvestProperties.TINVEST_URL;

@Configuration
public class TInvestRestConfiguration {

    @Bean
    public RestClient tInvestRestClient(
            String token
    ) {
        return RestClient.builder()
                .baseUrl(
                        TINVEST_URL
                )
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + token
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .defaultHeader(
                        HttpHeaders.ACCEPT,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }
}

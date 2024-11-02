package com.stock.v1.service;

import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Service
public class AutomationService {
	
    public void startAutomationJobs() throws IOException {
        
    	String url = "https://www.barchart.com/stocks/top-100-stocks";

        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .build();

        webClient.get()
                .exchangeToMono(response -> {
                    HttpHeaders headers = response.headers().asHttpHeaders();

                    // Extract cookies
                    List<String> cookies = headers.get(HttpHeaders.SET_COOKIE);
                    if (cookies != null) {
                        for (String cookie : cookies) {
                            System.out.println("Cookie: " + cookie);
                        }
                    }

                    // Extract XSRF token
                    String xsrfToken = headers.getFirst("XSRF-TOKEN");
                    if (xsrfToken != null) {
                        System.out.println("XSRF Token: " + xsrfToken);
                    }

                    return Mono.just(response);
                })
                .block();
    }
}
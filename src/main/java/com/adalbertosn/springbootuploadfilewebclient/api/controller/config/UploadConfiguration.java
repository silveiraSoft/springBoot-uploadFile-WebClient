package com.adalbertosn.springbootuploadfilewebclient.api.controller.config;

import com.adalbertosn.springbootuploadfilewebclient.core.ReactiveUpload;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import javax.net.ssl.SSLException;

@Configuration
@Qualifier("UploadConfiguration")
public class UploadConfiguration {

    //@Bean(name = "webclientUpload")
    @Bean
    public WebClient webClientUpload(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8081")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient webClientLocalUpload(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8080")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


    @Bean
    public ReactiveUpload reactiveUpload(){
        ReactiveUpload ru = new ReactiveUpload();
        ru.atrib1 = "test bean upload";
        return ru;
        //return new ReactiveUpload();
    }
}

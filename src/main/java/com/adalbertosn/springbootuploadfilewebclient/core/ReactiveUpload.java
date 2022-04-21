package com.adalbertosn.springbootuploadfilewebclient.core;

import com.adalbertosn.springbootuploadfilewebclient.exception.UploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReactiveUpload {

    @Autowired
    private WebClient webClientUpload;
    @Autowired
    private WebClient webClientLocalUpload;
    private static final String EXTERNAL_UPLOAD_URL = "http://localhost:8080/upload";
    public String atrib1;
    private final Path basePath = Paths.get("D:\\documentos-disco");

    public Mono<HttpStatus> uploadPdf(final Resource resource) {
        //final URI url = UriComponentsBuilder.fromHttpUrl(EXTERNAL_UPLOAD_URL).build().toUri();
        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", resource);

        return webClientUpload.post()
                .uri("/upload")
                .contentType(MediaType.APPLICATION_PDF)
                .body(BodyInserters.fromResource(resource))
                //.contentType(MediaType.MULTIPART_FORM_DATA)
                //.body(BodyInserters.fromMultipartData(builder.build()))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(HttpStatus.class);//.thenReturn(response.statusCode());
                    } else {
                        throw new UploadException("Error uploading file");
                    }
                });
    }

    public Mono<HttpStatus> uploadMultipart(final MultipartFile multipartFile) {
        //final URI url = UriComponentsBuilder.fromHttpUrl(EXTERNAL_UPLOAD_URL).build().toUri();
        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", multipartFile.getResource());

        return webClientUpload.post()
                //.uri(url)
                .uri("/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(HttpStatus.class).thenReturn(response.statusCode());
                    } else {
                        throw new UploadException("Error uploading file");
                    }
                });
    }


    public Mono<String> testReactivo(final Flux<FilePart> fileParts) {
        return webClientUpload.post()
                .uri("/test")
                //.contentType(MediaType.APPLICATION_JSON)
                //.accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("Testando"))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new RuntimeException("Error")))
                .bodyToMono(String.class);
    }

    public Mono<String> uploadReactivoOK(final Flux<Part> fileParts) {
        return webClientLocalUpload.post()
                .uri("/saveFileDisco")
                //.contentType(MediaType.APPLICATION_JSON)
                //.accept(MediaType.APPLICATION_JSON)
                //.contentType(MediaType.MULTIPART_FORM_DATA)
                .contentType(MediaType.APPLICATION_PDF)
                .body(BodyInserters.fromValue(fileParts))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new RuntimeException("Error")))
                .bodyToMono(String.class);
    }

    public Mono<String> uploadReactivo(final FilePart filePart) {
                 return  webClientLocalUpload.post()
                        .uri("/saveFileDisco")
                         //.contentType(MediaType.)
                         //.accept(MediaType.APPLICATION_JSON)
                         //.contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(BodyInserters.fromValue(filePart))
                         //.body(BodyInserters.fromPublisher(filePart.content(), DataBuffer.class))
                        .retrieve()
                        .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new RuntimeException("Error")))
                        .bodyToMono(String.class);

    }

    //public Mono<ResponseEntity<Void>> uploadReactivo0(final Flux<Part> fileParts) {
    public Mono<String> uploadReactivo0(final Flux<FilePart> fileParts) {

        //final URI url = UriComponentsBuilder.fromHttpUrl(EXTERNAL_UPLOAD_URL).build().toUri();
        return fileParts
                .flatMap(filePart -> {
                    return webClientLocalUpload.post()
                            .uri("/saveFileDisco")
                            //.contentType(MediaType.APPLICATION_PDF)
                            //.contentType(MediaType.MULTIPART_FORM_DATA)
                            //.accept(MediaType.APPLICATION_PDF)
                            //.body(BodyInserters.fromValue(filePart))
                            .body(BodyInserters.fromPublisher(filePart.content(), DataBuffer.class))
                            .retrieve()
                            .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new RuntimeException("Error")))
                            .bodyToMono(String.class).thenReturn(Mono.just(ResponseEntity.accepted().build()))
                            ;


                            //.body(BodyInserters.fromPublisher(filePart.content(), DataBuffer.class))

//                            .exchange()
//                            .flatMap(clientResponse -> {
//                                //some logging
//                                return Mono.empty();
//                            });


//                            .exchangeToMono(response -> {
//                                if (response.statusCode().equals(HttpStatus.OK)) {
//                                    //return response.bodyToMono(HttpStatus.class).thenReturn(response.statusCode());
//                                    return Mono.empty();
//                                } else {
//                                    throw new UploadException("Error uploading file");
//                                }
//                            });
//
//
//                            .exchangeToMono(response -> {
//                                if (response.statusCode().equals(HttpStatus.OK)) {
//                                    return response.bodyToMono(HttpStatus.class).thenReturn(response.statusCode());
//                                } else {
//                                    throw new UploadException("Error uploading file");
//                                }
//
//
//                            .flatMap(clientResponse -> {
//                                //some logging
//                                return Mono.empty();
//                            })
//
//
//                            //;

                })
                .collectList()
                //.flatMap(response -> Mono.just(ResponseEntity.accepted().build()));
                .flatMap(response -> Mono.just("Sucess"));
    }

    public Mono<ResponseEntity<Void>> uploadReactivo1(final Flux<FilePart> fileParts) {
        final URI url = UriComponentsBuilder.fromHttpUrl(EXTERNAL_UPLOAD_URL).build().toUri();
        return fileParts
                .flatMap(filePart -> {
                    return webClientUpload.post()
                            //.uri("/someOtherService")
                            .uri(url)
                            .body(BodyInserters.fromPublisher(filePart.content(), DataBuffer.class))
                            .exchange()
                            .flatMap(clientResponse -> {
                                //some logging
                                return Mono.empty();
                            });
                })
                .collectList()
                .flatMap(response -> Mono.just(ResponseEntity.accepted().build()));
    }

//    public String getTypeOfBean(){
//        return webClient;
//    }
}

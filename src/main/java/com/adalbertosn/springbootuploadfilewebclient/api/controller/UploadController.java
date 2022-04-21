package com.adalbertosn.springbootuploadfilewebclient.api.controller;

import com.adalbertosn.springbootuploadfilewebclient.core.ReactiveUpload;
import com.adalbertosn.springbootuploadfilewebclient.core.UploadDisco;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class UploadController {

    Logger log = LoggerFactory.getLogger(UploadController.class);
    @Autowired
    ReactiveUpload uploadService;
    @Autowired
    UploadDisco uploadDisco;
    //private final Path basePath = Paths.get("./src/main/resources/upload/");
    private final Path basePath = Paths.get("D:\\documentos-disco");

//    @Autowired
//    public UploadController(ReactiveUpload uploadService) {
//        this.uploadService = uploadService;
//    }
//
    @PostMapping(path = "/uploadPdf")
    //@ResponseStatus(HttpStatus.OK)
    public Mono<HttpStatus> uploadPdf(@RequestParam("file") final MultipartFile multipartFile) {
        return uploadService.uploadPdf(multipartFile.getResource());
    }

    @PostMapping(value = "/upload/multipart")
    public Mono<HttpStatus> uploadMultipart(@RequestParam("file") final MultipartFile multipartFile) {
        return uploadService.uploadMultipart(multipartFile);
    }

//    @RequestMapping(value = "/upload1", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public Flux<String> uploadPdf(@RequestPart("file") Flux<Part> multipartFile) {
//
//        log.info("Request contains, File: ");
//        return multipartFile
//                .filter(part -> part instanceof FilePart) // only retain file parts
//                .ofType(FilePart.class) // convert the flux to FilePart
//                .flatMap(this::saveFile); // save each file and flatmap it to a flux of results
//        // Add your processing logic here
//        //return ResponseEntity.ok("Success");
//    }


    //@RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    //public ResponseEntity<String> uploadPdf(@RequestPart("file") MultipartFile multipartFile) {
    //public Mono<ResponseEntity<Void>> uploadPdf2(@RequestPart("file") Flux<Part> fileParts) {

    //public Mono<ResponseEntity<Void>> uploadReactivo(@RequestPart("file") Flux<FilePart> fileParts) {
    //public Mono<String> uploadReactivo(@RequestPart("file") Flux<FilePart> fileParts) {
    @PostMapping(value = "/uploadReactivo" /*, consumes = MediaType.MULTIPART_FORM_DATA_VALUE , produces = MediaType.APPLICATION_JSON_VALUE*/)
    public Mono<String> uploadReactivo(@RequestPart("file") Flux<FilePart> fileParts) {
             return   fileParts
                .doOnNext(
                        fp -> {
                            System.out.println(fp.filename());
                        }).flatMap( fp ->{
                              return uploadService.uploadReactivo(fp);
                             //return fp.transferTo(basePath.resolve(fp.filename()));
                        }).onErrorMap(e-> new RuntimeException(e.getMessage()))
                     .then(Mono.just("Success"));

    }

//    public Mono<Void> uploadReactivo(@RequestPart("file") Flux<FilePart> fileParts) {
//        return fileParts
//                .doOnNext(fp->System.out.println(fp.filename()))
//                .flatMap(fp->fp.transferTo(basePath.resolve(fp.filename())))
//                .then();
//    }

    @PostMapping(value = "/testReactivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    //@RequestMapping(value = "/upload1", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    //public ResponseEntity<String> uploadPdf(@RequestPart("file") MultipartFile multipartFile) {
    //public Mono<ResponseEntity<Void>> uploadPdf2(@RequestPart("file") Flux<Part> fileParts) {
    public Mono<String> testReactivo(@RequestPart("file") Flux<FilePart> fileParts) {
        return uploadService.testReactivo(fileParts);
    }


    private Mono<String> saveFile1(FilePart filePart) {
        log.info("handling file upload {}", filePart.filename());

        // if a file with the same name already exists in a repository, delete and recreate it
        final String filename = filePart.filename();
        File file = new File(filename);
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            return Mono.error(e); // if creating a new file fails return an error
        }
        return Mono.just(filename);
    }

    @PostMapping(value = "/saveFileDisco"/*, consumes = MediaType.MULTIPART_FORM_DATA_VALUE , produces = MediaType.APPLICATION_JSON_VALUE*/)
    private Mono<String> saveFileDisco(@RequestBody FilePart filePart) throws Exception {
        log.info("handling file upload {}", filePart.filename());

        // if a file with the same name already exists in a repository, delete and recreate it
        final String filename = filePart.filename();
        File file = new File(filename);
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            //return Mono.error(e); // if creating a new file fails return an error
            throw new Exception(e.getMessage());
        }

        return filePart.transferTo(basePath.resolve(filePart.filename())).then(Mono.just("Success"));
        //uploadDisco.salvarFileReactivo(filePart);
        //return filename;
    }

//    private Mono<String> saveFileDisco(FilePart filePart) {
//        log.info("handling file upload {}", filePart.filename());
//
//        // if a file with the same name already exists in a repository, delete and recreate it
//        final String filename = filePart.filename();
//        File file = new File(filename);
//        if (file.exists())
//            file.delete();
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            return Mono.error(e); // if creating a new file fails return an error
//        }
//
//        filePart.transferTo(basePath.resolve(filePart.filename()));
//        //uploadDisco.salvarFileReactivo(filePart);
//        return Mono.just(filename);
//    }

//    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
//            produces = {MediaType.APPLICATION_JSON_VALUE})
//    @ResponseBody
//    public Mono<HttpStatus> uploadPdf(@RequestParam MultipartFile file) {
//        return uploadService.uploadPdf(file.getResource());
//    }

//    @PostMapping(path = "/upload")
//    public String uploadPdf() {
//        return uploadService.atrib1;
//    }

    @GetMapping("/hello")
    public String hello(){
        return "Ola";
    }
}

package com.alkemy.java2.clase15.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/test")
@Slf4j

public class TestController {

  @GetMapping("/hola")
  public String mostrarHola(){
    log.info("en el controlador TestController, metodo mostrarHola");
    return  "Hola desplegado desde controller test";

  }
}

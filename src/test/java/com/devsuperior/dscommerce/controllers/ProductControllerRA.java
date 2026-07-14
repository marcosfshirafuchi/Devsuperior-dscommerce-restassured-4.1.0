package com.devsuperior.dscommerce.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class ProductControllerRA {

    private Long existingProductId, nonExistingProductId;

    @BeforeEach
    public void sertUp() {
        //Endereço que vai estar hospedado o serviço
        baseURI = "http://localhost:8080";
    }

    //Exercícios de fixação: Testes de API com MockMvc

    /*Problema 1: Consultar produto por id

    Implemente o teste de API usando o REST Assured para consultar produto com id existente. Para o teste, você deve fazer uma requisição do tipo GET no endpoint /products/{id} onde id = 2, conforme ilustrado na Figura 1a (abaixo). Em seguida, você deverá verificar se o status da requisição corresponde a 200 (Ok), obter o corpo da resposta e verificar se os campos id, name, imgUrl, price, categories.id e categories.name correspondem aos valores apresentados na Figura 1b (abaixo).


    Figura 1a: Exemplo consulta de produtos por id

    {
        "id": 2,
        "name": "Smart TV",
        "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore",
        "price": 2190.0,
        "imgUrl": "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg",
        "categories": [
            {
                "id": 3,
                "name": "Computadores"
            },
            {
                "id": 2,
                "name": "Eletrônicos"
            }
          ]
        }
    Figura 1b:  Dados do produto com id = 2


    * */
    @Test
    public void findByIdShouldReturnProductWhenIdExists() {
        existingProductId = 2L;
        given()
                //Está passando o endpoint para testar
                .get("/products/{id}", existingProductId)
        .then()
             //Verifica a resposta do serviço
            .statusCode(200)
            .body("id", is(2))
            .body("name", equalTo("Smart TV"))
            .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
            .body("price", is(2190.0F))
            .body("categories.id", hasItems(2, 3))
            .body("categories.name", hasItems("Eletrônicos", "Computadores"));
    }
}

package com.devsuperior.dscommerce.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class ProductControllerRA {

    private Long existingProductId, nonExistingProductId;
    private String productName;

    @BeforeEach
    public void sertUp() {
        //Endereço que vai estar hospedado o serviço
        baseURI = "http://localhost:8080";
        productName = "Macbook";
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

        /*Problema 2: Consultar produtos

    Implemente os testes de API usando Rest Assured para consultar produtos (método GET do ProductController), considerando os seguintes cenários.
    1.	Busca paginada exibe listagem paginada quando campo nome não preenchido e checa se os produtos Macbook Pro e PC Gamer Tera estão contidos
    2.	Busca paginada filtra produtos por nome e exibe listagem paginada quando campo nome preenchidos
    3.	Busca paginada filtra produtos de forma paginada e filtra produtos com preço maior que 2000.0

    * */

    //1.	Busca paginada exibe listagem paginada quando campo nome não preenchido e checa se os produtos Macbook Pro e PC Gamer Tera estão contidos
    @Test
    public void findAllShouldReturnPageProductsWhenProductNameIsEmpty(){
        given()
           //Está passando o endpoint para testar
           .get("/products?page=0")
        .then()
           .statusCode(200)
           //Verifica os nomes dos produtos
           .body("content.name", hasItems("Macbook Pro", "PC Gamer Tera"));
    }

    //2.	Busca paginada filtra produtos por nome e exibe listagem paginada quando campo nome preenchidos
    @Test
    public void findAllShouldReturnPageProductsWhenProductNameIsNotEmpty(){
        given()
           //Está passando o endpoint para testar
           .get("/products?name={productName}", productName)
        .then()
           //Verifica o status code
           .statusCode(200)
           //Verifica o id
           .body("content.id[0]", is(3))
           //Verifica o nome
           .body("content.name[0]", equalTo("Macbook Pro"))
           //Verifica o preço
           .body("content.price[0]", is(1250.0F))
           //Verifica a imagem
           .body("content.imgUrl[0]", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
    }

    //3.	Busca paginada filtra produtos de forma paginada e filtra produtos com preço maior que 2000.0
    @Test
    public void findAllShouldReturnPagedProductsWithPriceGreaterThan2000(){
        given()
           //Está passando o endpoint para testar
           .get("/products?size=25")
        .then()
           //Verifica o status code
           .statusCode(200)
           //Verifica os nomes dos produtos com o preço maior que 2000.0
           .body("content.findAll {it.price > 2000}.name", hasItems("Smart TV", "PC Gamer Weed"));
    }
}

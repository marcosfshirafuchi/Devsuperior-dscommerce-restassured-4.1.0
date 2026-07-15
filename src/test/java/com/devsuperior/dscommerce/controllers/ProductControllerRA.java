package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class ProductControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    private Long existingProductId, nonExistingProductId, dependentProductId;
    private String productName;
    //Chave é String e valor é Object
    private Map<String, Object> postProductInstance;

    @BeforeEach
    public void sertUp() {
        //Endereço que vai estar hospedado o serviço
        baseURI = "http://localhost:8080";

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto"; // Invalid Token

        productName = "Macbook";
        postProductInstance = new HashMap<>();
        postProductInstance.put("name", "Meu produto");
        postProductInstance.put("description", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim");
        postProductInstance.put("imgUrl", "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
        postProductInstance.put("price", 50.0);

        //Criar uma lista de categorias
        List<Map<String, Object>> categories = new ArrayList<>();

        Map<String, Object> category1 = new HashMap<>();
        category1.put("id",2);

        Map<String, Object> category2 = new HashMap<>();
        category2.put("id",3);

        categories.add(category1);
        categories.add(category2);

        postProductInstance.put("categories", categories);
    }

    //Exercícios de fixação: Testes de API com MockMvc

    /*Problema 1: Consultar produto por id

    Implemente o teste de API usando o REST Assured para consultar produto com id existente. Para o teste, você deve fazer uma requisição do tipo GET no endpoint /products/{id} onde id = 2, conforme ilustrado na Figura 1a (abaixo). Em seguida, você deverá verificar se o status da requisição corresponde a 200 (Ok), obter o corpo da resposta e verificar se os campos id, name, imgUrl, price, categories.id e categories.name correspondem aos valores apresentados na Figura 1b (abaixo).


    Figura 1a: Exemplo consulta de produtos por id

    {
        "id": 2,
        "name": "Smart TV",
        "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore e dolore",
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

        /*Problema 3: Inserir produto

    Implemente os testes de API usando Rest Assured para inserção de produto (método POST do ProductController), considerando os seguintes cenários. Lembre-se de inserir o token no cabeçalho da requisição.
    1.	Inserção de produto insere produto com dados válidos quando logado como admin
    2.	Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e campo name for inválido
    3.	Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e campo description for inválido
    4.	Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e campo price for negativo
    5.	Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e campo price for zero
    6.	Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e não tiver categoria associada
    7.	Inserção de produto retorna 403 quando logado como cliente
    8.	Inserção de produto retorna 401 quando não logado como admin ou cliente

    * */

    //1.	Inserção de produto insere produto com dados válidos quando logado como admin
    @Test
    public void insertShouldReturnProductCreatedWhenAdminLogged(){
        //Criar o objeto JSON
        // JSONObject newProduct = new JSONObject(postProductInstance); // Removido

        given()
           //Definindo o cabeçalho da requisição
           //Tipo da informação
           .header("Content-type","application/json")
           .header("Authorization","Bearer " + adminToken)
           .body(postProductInstance) // Passando o Map diretamente
           .contentType(ContentType.JSON)
           .accept(ContentType.JSON)
        .when()
           //Está passando o endpoint para testar
           .post("/products")
        .then()
           .log().all() // Log all request and response details for debugging
           //Verificando a resposta da requisição
           .statusCode(201)
           .body("name", equalTo("Meu produto"))
           .body("price", is(50.0F))
           .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"))
           .body("categories.id", hasItems(2,3));
    }

    //2.	Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e campo name for inválido
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName(){
        postProductInstance.put("name","ab");
        //Criar o objeto JSON
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                //Definindo o cabeçalho da requisição
                //Tipo da informação
                .header("Content-type","application/json")
                .header("Authorization","Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                //Está passando o endpoint para testar
                .post("/products")
                .then()
                //Verificando a resposta da requisição
                .statusCode(422)
                //Mensagem de erro no Postman
                .body("errors.message[0]", equalTo("Nome precisar ter de 3 a 80 caracteres"));
    }

    //3.	Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e campo description for inválido
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidDescription(){
        postProductInstance.put("description","ab");
        //Criar o objeto JSON
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                //Definindo o cabeçalho da requisição
                //Tipo da informação
                .header("Content-type","application/json")
                .header("Authorization","Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                //Está passando o endpoint para testar
                .post("/products")
                .then()
                //Verificando a resposta da requisição
                .statusCode(422)
                //Mensagem de erro no Postman
                .body("errors.message[0]", equalTo("Descrição precisa ter no mínimo 10 caracteres"));
    }

    //4.	Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e campo price for negativo
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsNegative(){
        postProductInstance.put("price", -50.0);
        //Criar o objeto JSON
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                //Definindo o cabeçalho da requisição
                //Tipo da informação
                .header("Content-type","application/json")
                .header("Authorization","Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                //Está passando o endpoint para testar
                .post("/products")
                .then()
                //Verificando a resposta da requisição
                .statusCode(422)
                //Mensagem de erro no Postman
                .body("errors.message[0]", equalTo("O preço deve ser positivo"));
    }

    //5.	Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e campo price for zero
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsZero(){
        postProductInstance.put("price", 0);
        //Criar o objeto JSON
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                //Definindo o cabeçalho da requisição
                //Tipo da informação
                .header("Content-type","application/json")
                .header("Authorization","Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                //Está passando o endpoint para testar
                .post("/products")
                .then()
                //Verificando a resposta da requisição
                .statusCode(422)
                //Mensagem de erro no Postman
                .body("errors.message[0]", equalTo("O preço deve ser positivo"));
    }

    //6.	Inserção de produto retorna 422 e mensagens customizadas com dados inválidos quando logado como admin e não tiver categoria associada
    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndProductHasNotCategory(){
        postProductInstance.put("categories", null);
        //Criar o objeto JSON
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                //Definindo o cabeçalho da requisição
                //Tipo da informação
                .header("Content-type","application/json")
                .header("Authorization","Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                //Está passando o endpoint para testar
                .post("/products")
                .then()
                //Verificando a resposta da requisição
                .statusCode(422)
                //Mensagem de erro no Postman
                .body("errors.message[0]", equalTo("Deve ter pelo menos uma categoria"));
    }

    //7.	Inserção de produto retorna 403 quando logado como cliente
    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() {
        //Criar o objeto JSON
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                //Definindo o cabeçalho da requisição
                //Tipo da informação
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                //Está passando o endpoint para testar
                .post("/products")
                .then()
                //Verificando a resposta da requisição
                .statusCode(403);
    }

    //8.	Inserção de produto retorna 401 quando não logado como admin ou cliente
    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() {
        //Criar o objeto JSON
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                //Definindo o cabeçalho da requisição
                //Tipo da informação
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                //Está passando o endpoint para testar
                .post("/products")
                .then()
                //Verificando a resposta da requisição
                .statusCode(401);
    }

    /*Problema 4: Deletar produto

    Implemente os testes de API usando Rest Assured para deleção de produto (método DELETE do ProductController), considerando os seguintes cenários. Lembre-se de inserir o token no cabeçalho da requisição.
    1.	Deleção de produto deleta produto existente quando logado como admin
    2.	Deleção de produto retorna 404 para produto inexistente quando logado como admin
    3.	Deleção de produto retorna 400 para produto dependente quando logado como admin
    4.	Deleção de produto retorna 403 quando logado como cliente
    5.	Deleção de produto retorna 401 quando não logado como admin ou cliente

    * */

    //1.	Deleção de produto deleta produto existente quando logado como admin
    @Test
    public void deleteShouldReturnNoContentWhenIdExistsAndAdminLogged() {
        existingProductId = 25L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/products/{id}", existingProductId)
                .then()
                .statusCode(204);
    }

    //2.	Deleção de produto retorna 404 para produto inexistente quando logado como admin
    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() {
        nonExistingProductId = 100L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/products/{id}", nonExistingProductId)
                .then()
                .statusCode(404)
                .body("error", equalTo("Recurso não encontrado"))
                .body("status", equalTo(404));
    }

    //3.	Deleção de produto retorna 400 para produto dependente quando logado como admin
    @Test
    public void deleteShouldReturnBadRequestWhenDepedentIdAndAdminLogged() {
        dependentProductId = 3L;

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/products/{id}", dependentProductId)
                .then()
                .statusCode(400);
    }

    //4.	Deleção de produto retorna 403 quando logado como cliente
    @Test
    public void deleteShouldReturnForbiddenWhenClientLogged() {
        existingProductId = 25L;

        given()
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .delete("/products/{id}", existingProductId)
                .then()
                .statusCode(403);
    }

    //5.	Deleção de produto retorna 401 quando não logado como admin ou cliente
    @Test
    public void deleteShouldReturnUnauthorizedWhenInvalidToken() {
        existingProductId = 25L;

        given()
                .header("Authorization", "Bearer " + invalidToken)
                .when()
                .delete("/products/{id}", existingProductId)
                .then()
                .statusCode(401);
    }

}
/*
 * This source file was generated by the Gradle 'init' task
 */
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import utils.Orders;
import utils.User;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.Random;

class AppTest {

    private String token;

    @BeforeClass
    @Test
    public void setup(){
        String emailRandom = generateRandomEmail();
        RestAssured.baseURI = "https://simple-books-api.glitch.me";
        Response response = given()
                                .contentType("application/json")
                                .body("{\"clientName\": \"Kevin\", \"clientEmail\": \"" + emailRandom + "\"}")
                                .post("/api-clients/")
                            .then()
                                .statusCode(201)
                                .extract()
                                .response();
        token = response.jsonPath().getString("accessToken");   
        System.out.println(token);                     
    }

    @Test
    public void validateBooksSchemasTheBooks(){
        given()
            .header("Content-Type","application/json")
        .when()
            .get("/books")
        .then()
            .assertThat()
            .statusCode(200)
            .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(
                "schemas/body-schemas.json"));
    }

    @Test
    public void validateStatusCode200Books(){
        given()
            .header("Content-Type","application/json")
        .when()
            .get("/books")
        .then()
            .assertThat()
            .statusCode(200);
    }

    @Test
    public void validateStatusCode404Books(){
        int id = 999;
        given()
            .header("Content-Type","application/json")
        .when()
            .get("/books/" + id)
        .then()
            .assertThat()
            .statusCode(404);
    }

    @Test
    public void validarStatusCode401CuandoElTokenEsInvalidoOrders(){

        Orders ordersBody = new Orders(1, "Kevin");

        given()
            .header("Content-Type","application/json")
            .header("Authorization","Bearer TOKEN_INVALIDO")
            .body(ordersBody)
        .when()
            .post("/orders")
        .then()
            .assertThat()
            .statusCode(401)
            .log().all();                                                   
    }

    @Test
    public void validarStatusCode400CuandoElCuerpoEsInvalidoOrders(){

        Orders ordersBody = new Orders(0, "");

        given()
            .header("Content-Type","application/json")
            .header("Authorization","Bearer "+token)
            .body(ordersBody)
        .when()
            .post("/orders")
        .then()
            .assertThat()
            .statusCode(400)
            .log().all();                                                   
    }

    @Test
    public void validarObtenerTokenSinEmail(){
        User userBody = new User("Kevin", "");
        given()
            .header("Content-Type", "application/json")
            .body(userBody)
        .when()
            .post("/api-clients")
        .then()
            .assertThat()
            .statusCode(400)
            .body("error", equalTo("Invalid or missing client email."));
    }

    @Test
    public void validarStatusCode201CuandoElTokenEsValidoOrders(){

        Orders ordersBody = new Orders(1, "Kevin");

        given()
            .header("Content-Type","application/json")
            .header("Authorization","Bearer "+token)
            .body(ordersBody)
        .when()
            .post("/orders")
        .then()
            .assertThat()
            .statusCode(201)
            .log().all();                                                   
    }

    @Test
    public void validarTiempoDeRespuesta(){
        given()
            .header("Content-Type","application/json")
        .when()
            .get("/books")
        .then()
            .assertThat()
            .statusCode(200)
            .time(lessThan(2900L));
    }

    

    public String generateRandomEmail(){
        Random random = new Random();
        int randomNumber = random.nextInt(100000);
        return "user" + randomNumber + "@example.com";
    }

}

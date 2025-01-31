/*
 * This source file was generated by the Gradle 'init' task
 */
package api.books.test;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

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

    public String generateRandomEmail(){
        Random random = new Random();
        int randomNumber = random.nextInt(100000);
        return "user" + randomNumber + "@example.com";
    }

}

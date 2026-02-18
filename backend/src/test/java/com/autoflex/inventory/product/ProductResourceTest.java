package com.autoflex.inventory.product;

import com.autoflex.inventory.TestDbCleaner;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class ProductResourceTest {

  @BeforeEach
  void setup() {
    TestDbCleaner.clean();
  }

  @Test
  void shouldDoCrud() {
    Long id = ((Number) given()
        .contentType("application/json")
        .body("""
          {"code":"P-100","name":"Table","price":150.00}
        """)
        .when().post("/products")
        .then().statusCode(200)
        .body("id", notNullValue())
        .extract().path("id")).longValue();

    given().when().get("/products/" + id).then()
        .statusCode(200)
        .body("code", equalTo("P-100"));

    given()
        .contentType("application/json")
        .body("""
          {"code":"P-100","name":"Table Plus","price":180.00}
        """)
        .when().put("/products/" + id)
        .then().statusCode(200)
        .body("name", equalTo("Table Plus"));

    given().when().get("/products").then()
        .statusCode(200)
        .body("$", hasSize(1));

    given().when().delete("/products/" + id).then().statusCode(204);
  }
}

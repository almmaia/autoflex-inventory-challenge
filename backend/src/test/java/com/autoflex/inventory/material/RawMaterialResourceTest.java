package com.autoflex.inventory.material;

import com.autoflex.inventory.TestDbCleaner;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class RawMaterialResourceTest {

  @BeforeEach
  void setup() {
    TestDbCleaner.clean();
  }

  @Test
  void shouldDoCrud() {
    Long id = ((Number) given()
        .contentType("application/json")
        .body("""
          {"code":"RM-100","name":"Steel","stockQty":80}
        """)
        .when().post("/materials")
        .then().statusCode(200)
        .body("id", notNullValue())
        .extract().path("id")).longValue();

    given().when().get("/materials/" + id).then()
        .statusCode(200)
        .body("code", equalTo("RM-100"));

    given()
        .contentType("application/json")
        .body("""
          {"code":"RM-100","name":"Stainless Steel","stockQty":120}
        """)
        .when().put("/materials/" + id)
        .then().statusCode(200)
        .body("name", equalTo("Stainless Steel"));

    given().when().get("/materials").then()
        .statusCode(200)
        .body("$", hasSize(1));

    given().when().delete("/materials/" + id).then().statusCode(204);
  }
}

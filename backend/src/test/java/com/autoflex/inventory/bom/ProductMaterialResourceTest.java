package com.autoflex.inventory.bom;

import com.autoflex.inventory.TestDbCleaner;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class ProductMaterialResourceTest {

  @BeforeEach
  void setup() {
    TestDbCleaner.clean();
  }

  @Test
  void shouldDoCrud() {
    Long productId = ((Number) given()
        .contentType("application/json")
        .body("""
          {"code":"P-200","name":"Chair","price":120}
        """)
        .when().post("/products")
        .then().statusCode(200)
        .extract().path("id")).longValue();

    Long materialId = ((Number) given()
        .contentType("application/json")
        .body("""
          {"code":"RM-200","name":"Wood","stockQty":100}
        """)
        .when().post("/materials")
        .then().statusCode(200)
        .extract().path("id")).longValue();

    given()
        .contentType("application/json")
        .body("""
          {"productId":%d,"materialId":%d,"qtyNeeded":2}
        """.formatted(productId, materialId))
        .when().post("/bom")
        .then().statusCode(200)
        .body("qtyNeeded", equalTo(2));

    given().when().get("/bom/product/" + productId).then()
        .statusCode(200)
        .body("$", hasSize(1));

    given()
        .contentType("application/json")
        .body("""
          {"productId":%d,"materialId":%d,"qtyNeeded":3}
        """.formatted(productId, materialId))
        .when().put("/bom/product/%d/material/%d".formatted(productId, materialId))
        .then().statusCode(200)
        .body("qtyNeeded", equalTo(3));

    given().when().delete("/bom/product/%d/material/%d".formatted(productId, materialId))
        .then().statusCode(204);
  }
}

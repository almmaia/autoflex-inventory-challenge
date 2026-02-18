package com.autoflex.inventory.planning;

import com.autoflex.inventory.TestDbCleaner;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
public class ProductionPlanResourceTest {

  @BeforeEach
  void setup() {
    TestDbCleaner.clean();
  }

  @Test
  void shouldPrioritizeHigherValueProducts() {
    Long premiumId = createProduct("P-HIGH", "Premium Product", 30);
    Long standardId = createProduct("P-LOW", "Standard Product", 10);
    Long materialId = createMaterial("RM-SHARED", "Shared Material", 10);

    createBom(premiumId, materialId, 2);
    createBom(standardId, materialId, 1);

    given().when().get("/planning/suggestion").then()
        .statusCode(200)
        .body("items", hasSize(1))
        .body("items[0].productCode", equalTo("P-HIGH"))
        .body("items[0].plannedQty", equalTo(5))
        .body("items[0].totalValue", equalTo(150.0f))
        .body("totalValue", equalTo(150.0f));
  }

  private Long createProduct(String code, String name, int price) {
    return ((Number) given()
        .contentType("application/json")
        .body("""
          {"code":"%s","name":"%s","price":%d}
        """.formatted(code, name, price))
        .when().post("/products")
        .then().statusCode(200)
        .extract().path("id")).longValue();
  }

  private Long createMaterial(String code, String name, int stockQty) {
    return ((Number) given()
        .contentType("application/json")
        .body("""
          {"code":"%s","name":"%s","stockQty":%d}
        """.formatted(code, name, stockQty))
        .when().post("/materials")
        .then().statusCode(200)
        .extract().path("id")).longValue();
  }

  private void createBom(Long productId, Long materialId, int qtyNeeded) {
    given()
        .contentType("application/json")
        .body("""
          {"productId":%d,"materialId":%d,"qtyNeeded":%d}
        """.formatted(productId, materialId, qtyNeeded))
        .when().post("/bom")
        .then().statusCode(200);
  }
}

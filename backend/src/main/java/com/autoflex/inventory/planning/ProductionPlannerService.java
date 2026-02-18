package com.autoflex.inventory.planning;

import com.autoflex.inventory.bom.ProductMaterial;
import com.autoflex.inventory.material.RawMaterial;
import com.autoflex.inventory.product.Product;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ProductionPlannerService {

  public ProductionPlanResponse computeSuggestedPlan() {
    List<Product> products = Product.listAll();
    products.sort(SortByPriceDesc.INSTANCE);
    List<RawMaterial> materials = RawMaterial.listAll();
    Map<Long, BigDecimal> availableStock = new HashMap<>();
    for (RawMaterial m : materials) {
      availableStock.put(m.id, m.stockQty);
    }

    List<ProductionPlanItem> items = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;
    for (var p : products) {
      List<ProductMaterial> bom = ProductMaterial.list("product.id", p.id);
      if (bom.isEmpty()) continue;

      BigDecimal maxQty = null;
      for (var row : bom) {
        BigDecimal available = availableStock.getOrDefault(row.material.id, BigDecimal.ZERO);
        BigDecimal possible = available.divide(row.qtyNeeded, 0, RoundingMode.FLOOR);
        maxQty = (maxQty == null) ? possible : maxQty.min(possible);
      }

      if (maxQty == null || maxQty.compareTo(BigDecimal.ZERO) <= 0) continue;

      for (var row : bom) {
        BigDecimal current = availableStock.getOrDefault(row.material.id, BigDecimal.ZERO);
        BigDecimal consumed = row.qtyNeeded.multiply(maxQty);
        availableStock.put(row.material.id, current.subtract(consumed));
      }

      var item = new ProductionPlanItem();
      item.productId = p.id;
      item.productCode = p.code;
      item.productName = p.name;
      item.unitPrice = p.price;
      item.plannedQty = maxQty;
      item.totalValue = p.price.multiply(maxQty);
      total = total.add(item.totalValue);
      items.add(item);
    }

    ProductionPlanResponse response = new ProductionPlanResponse();
    response.items = items;
    response.totalValue = total;
    return response;
  }

  private static final class SortByPriceDesc implements Comparator<Product> {
    private static final SortByPriceDesc INSTANCE = new SortByPriceDesc();

    @Override
    public int compare(Product a, Product b) {
      return b.price.compareTo(a.price);
    }
  }
}

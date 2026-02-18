package com.autoflex.inventory.planning;

import java.math.BigDecimal;

public class ProductionPlanItem {
  public Long productId;
  public String productCode;
  public String productName;
  public BigDecimal unitPrice;
  public BigDecimal plannedQty;
  public BigDecimal totalValue;
}

package com.autoflex.inventory.bom;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductMaterialId implements Serializable {
  public Long productId;
  public Long materialId;

  public ProductMaterialId() {}
  public ProductMaterialId(Long productId, Long materialId) {
    this.productId = productId;
    this.materialId = materialId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ProductMaterialId that)) return false;
    return Objects.equals(productId, that.productId) && Objects.equals(materialId, that.materialId);
  }

  @Override
  public int hashCode() { return Objects.hash(productId, materialId); }
}

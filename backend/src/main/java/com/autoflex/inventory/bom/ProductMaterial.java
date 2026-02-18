package com.autoflex.inventory.bom;

import com.autoflex.inventory.material.RawMaterial;
import com.autoflex.inventory.product.Product;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "product_material")
public class ProductMaterial extends PanacheEntityBase {

  @EmbeddedId
  public ProductMaterialId id;

  @ManyToOne(optional = false) @MapsId("productId")
  public Product product;

  @ManyToOne(optional = false) @MapsId("materialId")
  public RawMaterial material;

  @Column(name = "qty_needed", nullable = false, precision = 14, scale = 3)
  @NotNull
  public BigDecimal qtyNeeded;
}

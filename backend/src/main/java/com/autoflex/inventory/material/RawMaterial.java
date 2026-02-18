package com.autoflex.inventory.material;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "raw_material")
public class RawMaterial extends PanacheEntityBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(nullable = false, unique = true, length = 50)
  @NotBlank
  public String code;

  @Column(nullable = false, length = 200)
  @NotBlank
  public String name;

  @Column(name = "stock_qty", nullable = false, precision = 14, scale = 3)
  @NotNull
  public BigDecimal stockQty;
}

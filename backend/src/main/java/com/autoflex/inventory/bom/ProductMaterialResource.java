package com.autoflex.inventory.bom;

import com.autoflex.inventory.material.RawMaterial;
import com.autoflex.inventory.product.Product;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.math.BigDecimal;
import java.util.List;

@Path("/bom")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductMaterialResource {

  @GET
  public List<ProductMaterial> listAll() {
    return ProductMaterial.listAll();
  }

  @GET @Path("/product/{productId}")
  public List<ProductMaterial> listByProduct(@PathParam("productId") Long productId) {
    return ProductMaterial.list("product.id", productId);
  }

  @GET @Path("/product/{productId}/material/{materialId}")
  public ProductMaterial get(@PathParam("productId") Long productId, @PathParam("materialId") Long materialId) {
    ProductMaterial pm = ProductMaterial.findById(new ProductMaterialId(productId, materialId));
    if (pm == null) throw new NotFoundException("Product material not found");
    return pm;
  }

  @POST
  @Transactional
  public ProductMaterial create(@Valid ProductMaterialRequest body) {
    ProductMaterial existing = ProductMaterial.findById(new ProductMaterialId(body.productId, body.materialId));
    if (existing != null) throw new BadRequestException("Association already exists; use PUT to update");
    return upsert(body);
  }

  @PUT @Path("/product/{productId}/material/{materialId}")
  @Transactional
  public ProductMaterial update(
      @PathParam("productId") Long productId,
      @PathParam("materialId") Long materialId,
      @Valid ProductMaterialRequest body
  ) {
    if (!productId.equals(body.productId) || !materialId.equals(body.materialId)) {
      throw new BadRequestException("Path ids must match payload ids");
    }
    return upsert(body);
  }

  @DELETE @Path("/product/{productId}/material/{materialId}")
  @Transactional
  public void delete(@PathParam("productId") Long productId, @PathParam("materialId") Long materialId) {
    ProductMaterial pm = ProductMaterial.findById(new ProductMaterialId(productId, materialId));
    if (pm == null) throw new NotFoundException("Product material not found");
    pm.delete();
  }

  private ProductMaterial upsert(ProductMaterialRequest body) {
    Product p = Product.findById(body.productId);
    if (p == null) throw new NotFoundException("Product not found");

    RawMaterial m = RawMaterial.findById(body.materialId);
    if (m == null) throw new NotFoundException("Material not found");

    ProductMaterialId id = new ProductMaterialId(body.productId, body.materialId);
    ProductMaterial pm = ProductMaterial.findById(id);

    if (pm == null) {
      pm = new ProductMaterial();
      pm.id = id;
      pm.product = p;
      pm.material = m;
    }

    pm.qtyNeeded = body.qtyNeeded;
    if (!pm.isPersistent()) {
      pm.persist();
    }
    return pm;
  }

  public static class ProductMaterialRequest {
    @NotNull
    public Long productId;
    @NotNull
    public Long materialId;
    @NotNull
    @DecimalMin(value = "0.001")
    public BigDecimal qtyNeeded;
  }
}

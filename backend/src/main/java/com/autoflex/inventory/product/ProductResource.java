package com.autoflex.inventory.product;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {
  @GET
  public List<Product> list() { return Product.listAll(); }

  @GET @Path("/{id}")
  public Product get(@PathParam("id") Long id) {
    Product p = Product.findById(id);
    if (p == null) throw new NotFoundException("Product not found");
    return p;
  }

  @POST
  @Transactional
  public Product create(@Valid Product body) {
    body.id = null;
    body.persist();
    return body;
  }

  @PUT @Path("/{id}")
  @Transactional
  public Product update(@PathParam("id") Long id, @Valid Product body) {
    Product p = Product.findById(id);
    if (p == null) throw new NotFoundException("Product not found");
    p.code = body.code;
    p.name = body.name;
    p.price = body.price;
    return p;
  }

  @DELETE @Path("/{id}")
  @Transactional
  public void delete(@PathParam("id") Long id) {
    if (!Product.deleteById(id)) throw new NotFoundException("Product not found");
  }
}

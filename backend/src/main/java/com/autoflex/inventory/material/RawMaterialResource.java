package com.autoflex.inventory.material;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/materials")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RawMaterialResource {
  @GET
  public List<RawMaterial> list() { return RawMaterial.listAll(); }

  @GET @Path("/{id}")
  public RawMaterial get(@PathParam("id") Long id) {
    RawMaterial m = RawMaterial.findById(id);
    if (m == null) throw new NotFoundException("Material not found");
    return m;
  }

  @POST
  @Transactional
  public RawMaterial create(@Valid RawMaterial body) {
    body.id = null;
    body.persist();
    return body;
  }

  @PUT @Path("/{id}")
  @Transactional
  public RawMaterial update(@PathParam("id") Long id, @Valid RawMaterial body) {
    RawMaterial m = RawMaterial.findById(id);
    if (m == null) throw new NotFoundException("Material not found");
    m.code = body.code;
    m.name = body.name;
    m.stockQty = body.stockQty;
    return m;
  }

  @DELETE @Path("/{id}")
  @Transactional
  public void delete(@PathParam("id") Long id) {
    if (!RawMaterial.deleteById(id)) throw new NotFoundException("Material not found");
  }
}

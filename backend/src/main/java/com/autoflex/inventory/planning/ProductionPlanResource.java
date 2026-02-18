package com.autoflex.inventory.planning;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/planning")
@Produces(MediaType.APPLICATION_JSON)
public class ProductionPlanResource {

  @Inject
  ProductionPlannerService planner;

  @GET @Path("/suggestion")
  public ProductionPlanResponse suggestion() {
    return planner.computeSuggestedPlan();
  }

  @GET @Path("/max-qty")
  public List<ProductionPlanItem> maxQtyCompatibility() {
    return planner.computeSuggestedPlan().items;
  }
}

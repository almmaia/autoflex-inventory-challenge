package com.autoflex.inventory.common;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.Instant;
import java.util.Map;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Throwable> {
  @Override
  public Response toResponse(Throwable e) {
    int status = 500;
    if (e instanceof WebApplicationException wae) status = wae.getResponse().getStatus();

    var body = Map.of(
      "timestamp", Instant.now().toString(),
      "status", status,
      "error", e.getClass().getSimpleName(),
      "message", e.getMessage() == null ? "Unexpected error" : e.getMessage()
    );
    return Response.status(status).entity(body).build();
  }
}

package org.acme.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String origin = requestContext.getHeaderString("Origin");
        
        if (origin != null && origin.equals("http://localhost:8000")) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
            responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE");
            responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization,Idempotency-Key");
            responseContext.getHeaders().add("Access-Control-Expose-Headers", "X-Custom-Header");
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        }
    }
}
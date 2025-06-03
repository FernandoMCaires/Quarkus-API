package org.acme.filters;

import jakarta.annotation.Priority;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Singleton
@Priority(Priorities.AUTHENTICATION)
    public class RateLimitingFilter implements ContainerRequestFilter {

    private static final String RATE_LIMIT_HEADER = "X-RateLimit-Limit";
    private static final String REMAINING_HEADER = "X-RateLimit-Remaining";
    @ConfigProperty(name = "rate.limit.requests-per-minute", defaultValue = "2")
    private int maxRequestsPerMinute;
    private final Map<String, ClientRequestInfo> requestsPerClient = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext context) {
        String clientIp = getClientIp(context);

        // Obtem ou inicializa informações do cliente
        ClientRequestInfo clientRequestInfo = requestsPerClient.computeIfAbsent(clientIp, k -> new ClientRequestInfo());

        synchronized (clientRequestInfo) {
            // Verificar se está dentro do limite de requisições
            if (!clientRequestInfo.isAllowed()) {
                throw new WebApplicationException(
                        Response.status(Response.Status.TOO_MANY_REQUESTS)
                                .entity("Limite de requisições excedido. Tente novamente mais tarde.")
                                .build()
                );
            }

            // Adicionar cabeçalhos de limitação
            context.getHeaders().add(RATE_LIMIT_HEADER, String.valueOf(maxRequestsPerMinute));
            context.getHeaders().add(REMAINING_HEADER, String.valueOf(clientRequestInfo.getRemainingRequests()));
        }
    }

    // Obtém o IP do cliente da requisição
    private String getClientIp(ContainerRequestContext context) {
        return context.getHeaders().getFirst("X-Forwarded-For") != null
                ? context.getHeaders().getFirst("X-Forwarded-For")
                : context.getUriInfo().getRequestUri().getHost();
    }

    // Classe para controlar estado de requisições por cliente
    private class ClientRequestInfo {
        private Instant timestamp = Instant.now();
        private AtomicInteger requestCount = new AtomicInteger(0);

        boolean isAllowed() {
            if (timestamp.plusSeconds(60).isBefore(Instant.now())) {
                timestamp = Instant.now();
                requestCount.set(0);
            }

            // Atualizar contagem e verificar limite
            return requestCount.incrementAndGet() <= maxRequestsPerMinute;
        }

        int getRemainingRequests() {
            return Math.max(0, maxRequestsPerMinute - requestCount.get());
        }
    }
}
package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.entitys.Cliente;

@ApplicationScoped
public class ClienteRepository implements PanacheRepository<Cliente> {
    public Cliente findByIdempotencyKey(String idempotencyKey) {
        return find("idempotencyKey", idempotencyKey).firstResult();
    }
}
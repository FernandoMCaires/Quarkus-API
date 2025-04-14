package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.entitys.Pedido;

@ApplicationScoped
public class PedidoRepository implements PanacheRepository<Pedido> {
}

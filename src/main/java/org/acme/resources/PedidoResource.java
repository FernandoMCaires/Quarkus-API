package org.acme.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.entitys.Pedido;
import org.acme.repository.PedidoRepository;

import java.util.List;

@Path("/pedidos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PedidoResource {

    @Inject
    PedidoRepository pedidoRepository;

    @GET
    public List<Pedido> listarTodos() {
        return pedidoRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Pedido buscarPorId(@PathParam("id") Long id) {
        return pedidoRepository.findById(id);
    }

    @POST
    @Transactional
    public Response salvar(Pedido pedido) {
        pedidoRepository.persist(pedido);
        return Response.status(Response.Status.CREATED).entity(pedido).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Long id, Pedido pedidoAtualizado) {
        Pedido pedido = pedidoRepository.findById(id);
        if (pedido == null) {
            throw new NotFoundException("Pedido não encontrado");
        }

        pedido.setDescricao(pedidoAtualizado.getDescricao());
        pedido.setValor(pedidoAtualizado.getValor());

        return Response.ok(pedido).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Long id) {
        boolean deletado = pedidoRepository.deleteById(id);
        if (!deletado) {
            throw new NotFoundException("Pedido não encontrado");
        }

        return Response.noContent().build();
    }
}

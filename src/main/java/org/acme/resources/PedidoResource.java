package org.acme.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.entitys.Cliente;
import org.acme.entitys.Pedido;
import org.acme.entitys.Produto;
import org.acme.repository.ClienteRepository;
import org.acme.repository.PedidoRepository;
import org.acme.repository.ProdutoRepository;

import java.util.ArrayList;
import java.util.List;

@Path("/pedidos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PedidoResource {

    @Inject
    PedidoRepository pedidoRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    ProdutoRepository produtoRepository;


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
        // Buscar cliente completo
        Cliente cliente = clienteRepository.findById(pedido.getCliente().getId());
        if (cliente == null) {
            throw new NotFoundException("Cliente não encontrado");
        }

        // Buscar produtos completos
        List<Produto> produtosCompletos = new ArrayList<>();
        double valorTotal = 0.0;

        for (Produto p : pedido.getProdutos()) {
            Produto produtoCompleto = produtoRepository.findById(p.getId());
            if (produtoCompleto == null) {
                throw new NotFoundException("Produto com ID " + p.getId() + " não encontrado");
            }
            produtosCompletos.add(produtoCompleto);
            valorTotal += produtoCompleto.getPreco();
        }

        // Setar dados no pedido
        pedido.setCliente(cliente);
        pedido.setProdutos(produtosCompletos);
        pedido.setValor(valorTotal); // <-- Aqui está o cálculo

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
        pedido.setCliente(pedidoAtualizado.getCliente());
        pedido.setProdutos(pedidoAtualizado.getProdutos());

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

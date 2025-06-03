package org.acme.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.entitys.Produto;
import org.acme.repository.ProdutoRepository;

import java.util.List;

@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    @Inject
    ProdutoRepository produtoRepository;

    @GET
    public List<Produto> listarTodos() {
        List<Produto> produtos = produtoRepository.listAll();
        for (Produto produto : produtos) {
            int quantidade = produto.getPedidos() != null ? produto.getPedidos().size() : 0;
            produto.setQuantidade(quantidade);
        }
        return produtos;
    }


    @GET
    @Path("/{id}")
    public Produto buscarPorId(@PathParam("id") Long id) {
        Produto produto = produtoRepository.findById(id);
        if (produto == null) {
            throw new NotFoundException("Produto não encontrado");
        }
        produto.setQuantidade(produto.getPedidos() != null ? produto.getPedidos().size() : 0);
        return produto;
    }

    @POST
    @Transactional
    public Response salvar(Produto produto) {
        produtoRepository.persist(produto);
        return Response.status(Response.Status.CREATED).entity(produto).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Long id, Produto produtoAtualizado) {
        Produto produto = produtoRepository.findById(id);
        if (produto == null) {
            throw new NotFoundException("Produto não encontrado");
        }

        produto.setNome(produtoAtualizado.getNome());
        produto.setPreco(produtoAtualizado.getPreco());

        return Response.ok(produto).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Long id) {
        boolean deletado = produtoRepository.deleteById(id);
        if (!deletado) {
            throw new NotFoundException("Produto não encontrado");
        }
        return Response.noContent().build();
    }
}

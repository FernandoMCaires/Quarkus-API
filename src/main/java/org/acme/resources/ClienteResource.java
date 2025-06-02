package org.acme.resources;

import jakarta.inject.Inject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.entitys.Cliente;
import org.acme.entitys.Pedido;
import org.acme.repository.ClienteRepository;

import java.util.ArrayList;
import java.util.List;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos = new ArrayList<>();

    @Inject
    ClienteRepository clienteRepository;

    @GET
    public List<Cliente> listarTodos() {
        return clienteRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Cliente buscarPorId(@PathParam("id") Long id) {
        return clienteRepository.findById(id);
    }

    @POST
    @Transactional
    public Response adicionar(Cliente cliente) {
        clienteRepository.persist(cliente);
        return Response.status(Response.Status.CREATED).entity(cliente).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Long id, Cliente clienteAtualizado) {
        Cliente cliente = clienteRepository.findById(id);
        if (cliente == null) {
            throw new NotFoundException("Cliente não encontrado");
        }

        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());

        return Response.ok(cliente).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Long id) {
        Cliente cliente = clienteRepository.findById(id);
        if (cliente == null) {
            throw new NotFoundException("Cliente não encontrado");
        }

        clienteRepository.delete(cliente);
        return Response.noContent().build();
    }
}

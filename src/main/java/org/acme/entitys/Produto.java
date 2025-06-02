package org.acme.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Double preco;

    @ManyToMany(mappedBy = "produtos", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Pedido> pedidos = new ArrayList<>();

    @Transient
    @JsonIgnore
    private int quantidadePedidos;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public int getQuantidadePedidos() {
        return pedidos != null ? pedidos.size() : 0;
    }

    public void setQuantidadePedidos(int quantidade) {
    }
}

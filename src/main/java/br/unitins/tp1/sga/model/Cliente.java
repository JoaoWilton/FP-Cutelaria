package br.unitins.tp1.sga.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente que solicita facas personalizadas.
 * Herda dados básicos de Usuario.
 */
@Entity
public class Cliente extends Usuario {

    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
    @Column(unique = true)
    private String cpf;

    @PastOrPresent(message = "Data de cadastro não pode ser futura")
    private LocalDate dataCadastro;

    private LocalDate dataNascimento;

    /**
     * Indica se o cliente completou o cadastro com todos os dados obrigatórios
     * (CPF, data de nascimento, telefone, endereço).
     * Clientes com cadastro incompleto não podem realizar checkout.
     */
    @Column(nullable = false)
    private boolean cadastroCompleto = false;

    @OneToMany(mappedBy = "cliente")
    private List<ItemListaDesejos> itensListaDesejos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente")
    private List<Compra> compras = new ArrayList<>();

    @OneToMany(mappedBy = "cliente")
    private List<EnderecoCliente> enderecos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente")
    private List<AvaliacaoModelo> avaliacoes = new ArrayList<>();

    public Cliente() {
        this.dataCadastro = LocalDate.now();
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public boolean isCadastroCompleto() { return cadastroCompleto; }
    public void setCadastroCompleto(boolean cadastroCompleto) { this.cadastroCompleto = cadastroCompleto; }

    public List<ItemListaDesejos> getItensListaDesejos() { return itensListaDesejos; }
    public void setItensListaDesejos(List<ItemListaDesejos> itensListaDesejos) { this.itensListaDesejos = itensListaDesejos; }

    public List<Compra> getCompras() { return compras; }
    public void setCompras(List<Compra> compras) { this.compras = compras; }

    public List<EnderecoCliente> getEnderecos() { return enderecos; }
    public void setEnderecos(List<EnderecoCliente> enderecos) { this.enderecos = enderecos; }

    public List<AvaliacaoModelo> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(List<AvaliacaoModelo> avaliacoes) { this.avaliacoes = avaliacoes; }
}

package br.unitins.tp1.sga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Armazena o código temporário gerado para recuperação de senha.
 * O código expira em 15 minutos e só pode ser usado uma vez.
 * Funciona para qualquer tipo de usuário (Cliente, Cuteleiro, Fornecedor, Administrador).
 */
@Entity
@Table(name = "codigo_recuperacao_senha")
public class CodigoRecuperacaoSenha extends PanacheEntity {

    @ManyToOne
    private Usuario usuario;

    @Column(nullable = false, length = 6)
    private String codigo;

    @Column(nullable = false)
    private LocalDateTime expiracao;

    private boolean usado = false;

    public CodigoRecuperacaoSenha() {}

    public CodigoRecuperacaoSenha(Usuario usuario, String codigo) {
        this.usuario = usuario;
        this.codigo = codigo;
        this.expiracao = LocalDateTime.now().plusMinutes(15);
        this.usado = false;
    }

    public boolean isValido() {
        return !usado && LocalDateTime.now().isBefore(expiracao);
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public LocalDateTime getExpiracao() { return expiracao; }
    public void setExpiracao(LocalDateTime expiracao) { this.expiracao = expiracao; }

    public boolean isUsado() { return usado; }
    public void setUsado(boolean usado) { this.usado = usado; }
}

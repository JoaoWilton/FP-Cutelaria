package br.unitins.tp1.sga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Classe base abstrata para todos os atores do sistema.
 * Representa uma pessoa que interage com o sistema.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario extends PanacheEntity {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(unique = true)
    private String email;

    private String telefone;

    @NotBlank(message = "Login é obrigatório")
    @Column(unique = true)
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 50)
    private TipoUsuario tipoUsuario = TipoUsuario.CLIENTE;

    /**
     * Senha administrativa separada (hash bcrypt).
     * Usada apenas no login do painel administrativo.
     * Apenas usuários do tipo Administrador (entidade Administrador)
     * precisam ter este campo preenchido.
     */
    @Column(name = "senha_administrativa")
    private String senhaAdministrativa;

    // Construtores
    public Usuario() {}

    public Usuario(String nome, String email, String login, String senha) {
        this.nome = nome;
        this.email = email;
        this.login = login;
        this.senha = senha;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getSenhaAdministrativa() { return senhaAdministrativa; }
    public void setSenhaAdministrativa(String senhaAdministrativa) { this.senhaAdministrativa = senhaAdministrativa; }
}

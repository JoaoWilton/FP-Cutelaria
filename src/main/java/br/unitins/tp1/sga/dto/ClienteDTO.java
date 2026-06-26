package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.Cliente;

import java.time.LocalDate;

/**
 * DTO de saída para Cliente — evita expor a senha (hash) e os relacionamentos
 * @OneToMany (pedidos, carrinho, lista de desejos, avaliações), que geram um
 * schema gigante e cíclico no Swagger/OpenAPI.
 */
public class ClienteDTO {
    public Long id;
    public String nome;
    public String email;
    public String telefone;
    public String login;
    public String cpf;
    public LocalDate dataNascimento;
    public LocalDate dataCadastro;
    public boolean cadastroCompleto;

    public static ClienteDTO fromEntity(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.id = cliente.id;
        dto.nome = cliente.getNome();
        dto.email = cliente.getEmail();
        dto.telefone = cliente.getTelefone();
        dto.login = cliente.getLogin();
        dto.cpf = cliente.getCpf();
        dto.dataNascimento = cliente.getDataNascimento();
        dto.dataCadastro = cliente.getDataCadastro();
        dto.cadastroCompleto = cliente.isCadastroCompleto();
        return dto;
    }
}

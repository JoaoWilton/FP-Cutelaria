package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.Fornecedor;

/**
 * DTO de saída para Fornecedor — evita expor senha/senhaAdministrativa
 * e a lista @ManyToMany de Material (schema cíclico no Swagger/OpenAPI).
 */
public class FornecedorDTO {
    public Long id;
    public String nome;
    public String email;
    public String telefone;
    public String login;
    public String cnpj;
    public String contato;

    public static FornecedorDTO fromEntity(Fornecedor f) {
        FornecedorDTO dto = new FornecedorDTO();
        dto.id = f.id;
        dto.nome = f.getNome();
        dto.email = f.getEmail();
        dto.telefone = f.getTelefone();
        dto.login = f.getLogin();
        dto.cnpj = f.getCnpj();
        dto.contato = f.getContato();
        return dto;
    }
}

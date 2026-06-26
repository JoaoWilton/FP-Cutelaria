package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.Administrador;

/** DTO de saída para Administrador — não expõe senha/senhaAdministrativa. */
public class AdministradorDTO {
    public Long id;
    public String nome;
    public String email;
    public String telefone;
    public String login;

    public static AdministradorDTO fromEntity(Administrador a) {
        AdministradorDTO dto = new AdministradorDTO();
        dto.id = a.id;
        dto.nome = a.getNome();
        dto.email = a.getEmail();
        dto.telefone = a.getTelefone();
        dto.login = a.getLogin();
        return dto;
    }
}

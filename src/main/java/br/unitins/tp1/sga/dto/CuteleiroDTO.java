package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.Cuteleiro;

import java.time.LocalDate;

/**
 * DTO de saída para Cuteleiro — evita expor senha/senhaAdministrativa e a
 * lista @OneToMany de ModeloFaca (que referencia de volta o criador, gerando
 * ciclo no Swagger/OpenAPI).
 */
public class CuteleiroDTO {
    public Long id;
    public String nome;
    public String email;
    public String telefone;
    public String login;
    public String especialidade;
    public LocalDate dataContratacao;

    public static CuteleiroDTO fromEntity(Cuteleiro c) {
        CuteleiroDTO dto = new CuteleiroDTO();
        dto.id = c.id;
        dto.nome = c.getNome();
        dto.email = c.getEmail();
        dto.telefone = c.getTelefone();
        dto.login = c.getLogin();
        dto.especialidade = c.getEspecialidade();
        dto.dataContratacao = c.getDataContratacao();
        return dto;
    }
}

package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.EnderecoCliente;

public class EnderecoClienteDTO {
    public Long id;
    public String cep;
    public String rua;
    public String numero;
    public String complemento;
    public String bairro;
    public String cidade;
    public String estado;
    public boolean ativo;
    public boolean principal;

    public static EnderecoClienteDTO fromEntity(EnderecoCliente endereco) {
        EnderecoClienteDTO dto = new EnderecoClienteDTO();
        dto.id = endereco.id;
        dto.cep = endereco.getCep();
        dto.rua = endereco.getRua();
        dto.numero = endereco.getNumero();
        dto.complemento = endereco.getComplemento();
        dto.bairro = endereco.getBairro();
        dto.cidade = endereco.getCidade();
        dto.estado = endereco.getEstado();
        dto.ativo = endereco.isAtivo();
        dto.principal = endereco.isPrincipal();
        return dto;
    }
}

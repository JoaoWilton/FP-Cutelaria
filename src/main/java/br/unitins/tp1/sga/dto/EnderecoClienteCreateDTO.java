package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para criação/edição de endereço do cliente autenticado.
 *
 * Não inclui "cliente", "ativo" nem "principal": esses campos são controlados
 * pelo backend (cliente vem do JWT; ativo/principal têm regras próprias nos
 * endpoints de DELETE e /principal). Usar a entidade EnderecoCliente
 * diretamente com @Valid causaria erro de validação, pois o campo
 * "cliente" tem @NotNull e nunca é enviado pelo cliente da API.
 */
public class EnderecoClienteCreateDTO {

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos numéricos")
    public String cep;

    @NotBlank(message = "Rua é obrigatória")
    public String rua;

    @NotBlank(message = "Número é obrigatório")
    public String numero;

    public String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    public String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    public String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve conter a sigla com 2 caracteres")
    public String estado;
}

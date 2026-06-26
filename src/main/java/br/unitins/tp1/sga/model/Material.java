package br.unitins.tp1.sga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Matéria-prima utilizada na fabricação de componentes.
 */
@Entity
public class Material extends PanacheEntity {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String tipoMaterial;  // Texto livre: "Aço Carbono", "Jacarandá", etc.

    @NotNull(message = "Custo unitário é obrigatório")
    @Positive(message = "Custo unitário deve ser positivo")
    private BigDecimal custoUnitario;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipoMaterial() { return tipoMaterial; }
    public void setTipoMaterial(String tipoMaterial) { this.tipoMaterial = tipoMaterial; }

    public BigDecimal getCustoUnitario() { return custoUnitario; }
    public void setCustoUnitario(BigDecimal custoUnitario) { this.custoUnitario = custoUnitario; }
}
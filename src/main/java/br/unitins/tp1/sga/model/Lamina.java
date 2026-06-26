package br.unitins.tp1.sga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Lâmina da faca — entidade independente (sem herança de Componente).
 * Fase A da remoção de herança JOINED.
 */
@Entity
public class Lamina extends PanacheEntity {

    // Campos herdados de Componente — agora declarados diretamente
    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull(message = "Peso em gramas é obrigatório")
    @Positive(message = "Peso deve ser positivo")
    private Double pesoGramas;

    @ManyToOne
    private Material material;

    // Campos próprios da Lâmina
    @NotBlank(message = "Perfil de desbaste é obrigatório")
    private String perfilDesbaste; // "Plano", "Convexo", "Côncavo"

    @NotNull(message = "Espessura é obrigatória")
    @Positive(message = "Espessura deve ser positiva")
    private Double espessuraMm;

    // Getters e Setters
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getPesoGramas() { return pesoGramas; }
    public void setPesoGramas(Double pesoGramas) { this.pesoGramas = pesoGramas; }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    public String getPerfilDesbaste() { return perfilDesbaste; }
    public void setPerfilDesbaste(String perfilDesbaste) { this.perfilDesbaste = perfilDesbaste; }

    public Double getEspessuraMm() { return espessuraMm; }
    public void setEspessuraMm(Double espessuraMm) { this.espessuraMm = espessuraMm; }
}

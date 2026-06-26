package br.unitins.tp1.sga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Cabo da faca — entidade independente (sem herança de Componente).
 * Fase A da remoção de herança JOINED.
 */
@Entity
public class Cabo extends PanacheEntity {

    // Campos herdados de Componente — agora declarados diretamente
    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull(message = "Peso em gramas é obrigatório")
    @Positive(message = "Peso deve ser positivo")
    private Double pesoGramas;

    @ManyToOne
    private Material material;

    // Campos próprios do Cabo
    private String formatoErgonomico;

    private Boolean pinoOculto;

    // Getters e Setters
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getPesoGramas() { return pesoGramas; }
    public void setPesoGramas(Double pesoGramas) { this.pesoGramas = pesoGramas; }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    public String getFormatoErgonomico() { return formatoErgonomico; }
    public void setFormatoErgonomico(String formatoErgonomico) { this.formatoErgonomico = formatoErgonomico; }

    public Boolean getPinoOculto() { return pinoOculto; }
    public void setPinoOculto(Boolean pinoOculto) { this.pinoOculto = pinoOculto; }
}

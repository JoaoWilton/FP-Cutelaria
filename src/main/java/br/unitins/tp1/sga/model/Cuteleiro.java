package br.unitins.tp1.sga.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cuteleiro (artesão) que produz as facas.
 * Herda dados básicos de Usuario.
 */
@Entity
public class Cuteleiro extends Usuario {

    private String especialidade;  // Ex: Forja, Polimento, Cabos

    @NotNull(message = "Data de contratação é obrigatória")
    @PastOrPresent(message = "Data de contratação não pode ser futura")
    private LocalDate dataContratacao;

    @OneToMany(mappedBy = "criador")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<ModeloFaca> modelosFaca = new ArrayList<>();

    // Construtores
    public Cuteleiro() {
        this.dataContratacao = LocalDate.now();
    }

    // Getters e Setters
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public LocalDate getDataContratacao() { return dataContratacao; }
    public void setDataContratacao(LocalDate dataContratacao) { this.dataContratacao = dataContratacao; }

    public List<ModeloFaca> getModelosFaca() { return modelosFaca; }
    public void setModelosFaca(List<ModeloFaca> modelosFaca) { this.modelosFaca = modelosFaca; }
}

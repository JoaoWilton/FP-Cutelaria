package br.unitins.tp1.sga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class AvaliacaoModelo extends PanacheEntity {

    @NotNull(message = "Nota é obrigatória")
    @Min(value = 1, message = "Nota mínima é 1")
    @Max(value = 5, message = "Nota máxima é 5")
    private Integer nota;

    private String comentario;

    @NotNull(message = "Data de avaliação é obrigatória")
    private LocalDateTime dataAvaliacao;

    private boolean verificado = false;

    @ManyToOne
    @NotNull(message = "Cliente é obrigatório")
    private Cliente cliente;

    @ManyToOne
    @NotNull(message = "Modelo de faca é obrigatório")
    private ModeloFaca modeloFaca;

    public AvaliacaoModelo() {
        this.dataAvaliacao = LocalDateTime.now();
    }

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDateTime dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }

    public boolean isVerificado() { return verificado; }
    public void setVerificado(boolean verificado) { this.verificado = verificado; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public ModeloFaca getModeloFaca() { return modeloFaca; }
    public void setModeloFaca(ModeloFaca modeloFaca) { this.modeloFaca = modeloFaca; }
}

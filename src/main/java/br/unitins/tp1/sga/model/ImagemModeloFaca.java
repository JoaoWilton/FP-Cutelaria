package br.unitins.tp1.sga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class ImagemModeloFaca extends PanacheEntity {

    @NotBlank(message = "URL da imagem é obrigatória")
    private String url;

    private String descricao;

    private Integer ordem;

    @ManyToOne
    @NotNull(message = "Modelo de faca é obrigatório")
    private ModeloFaca modeloFaca;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }

    public ModeloFaca getModeloFaca() { return modeloFaca; }
    public void setModeloFaca(ModeloFaca modeloFaca) { this.modeloFaca = modeloFaca; }
}

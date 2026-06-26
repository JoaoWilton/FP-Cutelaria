package br.unitins.tp1.sga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
public class ModeloFaca extends PanacheEntity {

    @NotBlank(message = "Nome do modelo é obrigatório")
    private String nome;

    private String descricao;

    @NotNull(message = "Preço base é obrigatório")
    @Positive(message = "Preço base deve ser positivo")
    private BigDecimal precoBase;

    private boolean ativo = true;

    @NotNull(message = "Data de criação é obrigatória")
    private LocalDateTime dataCriacao;

    private LocalDateTime dataUltimaAtualizacao;

    @ManyToOne
    private Cuteleiro criador; // nullable: admin pode criar sem cuteleiro específico

    @ManyToOne
    @JoinColumn(name = "lamina_padrao_id")
    @NotNull(message = "Lâmina padrão é obrigatória")
    private Lamina laminaPadrao;

    @ManyToOne
    @JoinColumn(name = "cabo_padrao_id")
    @NotNull(message = "Cabo padrão é obrigatório")
    private Cabo caboPadrao;

    @ManyToMany
    @JoinTable(name = "modelo_faca_guarnicao",
            joinColumns = @JoinColumn(name = "modelo_faca_id"),
            inverseJoinColumns = @JoinColumn(name = "guarnicao_id"))
    private List<Guarnicao> guarnicoesPadrao = new ArrayList<>();

    @OneToMany(mappedBy = "modeloFaca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagemModeloFaca> imagens = new ArrayList<>();

    @OneToMany(mappedBy = "modeloFaca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpcaoPersonalizacao> opcoesPersonalizacao = new ArrayList<>();

    public ModeloFaca() {
        this.dataCriacao = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public BigDecimal calcularPrecoComPersonalizacoes(Map<String, String> opcoes) {
        BigDecimal total = precoBase != null ? precoBase : BigDecimal.ZERO;
        if (opcoes == null || opcoes.isEmpty()) {
            return total;
        }

        for (OpcaoPersonalizacao opcaoPersonalizacao : opcoesPersonalizacao) {
            if (!opcaoPersonalizacao.isAtivo() || opcaoPersonalizacao.getTipo() == null) {
                continue;
            }

            String opcaoSelecionada = opcoes.get(opcaoPersonalizacao.getTipo().name());
            if (opcaoPersonalizacao.getOpcao().equals(opcaoSelecionada)) {
                total = total.add(opcaoPersonalizacao.getCustoAdicional());
            }
        }
        return total;
    }

    public void desativar() {
        this.ativo = false;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPrecoBase() { return precoBase; }
    public void setPrecoBase(BigDecimal precoBase) { this.precoBase = precoBase; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }

    public Cuteleiro getCriador() { return criador; }
    public void setCriador(Cuteleiro criador) { this.criador = criador; }

    public Lamina getLaminaPadrao() { return laminaPadrao; }
    public void setLaminaPadrao(Lamina laminaPadrao) { this.laminaPadrao = laminaPadrao; }

    public Cabo getCaboPadrao() { return caboPadrao; }
    public void setCaboPadrao(Cabo caboPadrao) { this.caboPadrao = caboPadrao; }

    public List<Guarnicao> getGuarnicoesPadrao() { return guarnicoesPadrao; }
    public void setGuarnicoesPadrao(List<Guarnicao> guarnicoesPadrao) { this.guarnicoesPadrao = guarnicoesPadrao; }

    public List<ImagemModeloFaca> getImagens() { return imagens; }
    public void setImagens(List<ImagemModeloFaca> imagens) { this.imagens = imagens; }

    public List<OpcaoPersonalizacao> getOpcoesPersonalizacao() { return opcoesPersonalizacao; }
    public void setOpcoesPersonalizacao(List<OpcaoPersonalizacao> opcoesPersonalizacao) { this.opcoesPersonalizacao = opcoesPersonalizacao; }
}

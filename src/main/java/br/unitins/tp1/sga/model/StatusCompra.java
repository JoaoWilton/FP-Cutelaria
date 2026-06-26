package br.unitins.tp1.sga.model;

/**
 * Status do ciclo de vida de uma {@link Compra} (cabeçalho que agrupa um ou
 * mais {@link ItemCompra}, com PIX e pagamento únicos para a Compra toda).
 *
 * Status do ciclo de vida de uma Compra (sem o valor CARRINHO — ver Fase 1 do PLANO_REESTRUTURACAO.md).
 */
public enum StatusCompra {
    AGUARDANDO_PAGAMENTO,
    PAGAMENTO_CONFIRMADO,
    EM_PRODUCAO,
    ENVIADO,
    ENTREGUE,
    CANCELADO
}

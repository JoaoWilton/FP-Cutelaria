ALTER TABLE usuario ADD COLUMN IF NOT EXISTS tipo_usuario VARCHAR(50) NOT NULL DEFAULT 'CLIENTE';

CREATE TABLE IF NOT EXISTS modelo_faca (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco_base DECIMAL(19, 2) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL,
    data_ultima_atualizacao TIMESTAMP,
    criador_id BIGINT NOT NULL,
    lamina_padrao_id BIGINT NOT NULL,
    cabo_padrao_id BIGINT NOT NULL,
    CONSTRAINT fk_modelo_faca_criador FOREIGN KEY (criador_id) REFERENCES cuteleiro(id),
    CONSTRAINT fk_modelo_faca_lamina FOREIGN KEY (lamina_padrao_id) REFERENCES lamina(id),
    CONSTRAINT fk_modelo_faca_cabo FOREIGN KEY (cabo_padrao_id) REFERENCES cabo(id)
);

CREATE TABLE IF NOT EXISTS modelo_faca_guarnicao (
    modelo_faca_id BIGINT NOT NULL,
    guarnicao_id BIGINT NOT NULL,
    PRIMARY KEY (modelo_faca_id, guarnicao_id),
    CONSTRAINT fk_modelo_guarnicao_modelo FOREIGN KEY (modelo_faca_id) REFERENCES modelo_faca(id) ON DELETE CASCADE,
    CONSTRAINT fk_modelo_guarnicao_guarnicao FOREIGN KEY (guarnicao_id) REFERENCES guarnicao(id)
);

CREATE TABLE IF NOT EXISTS imagem_modelo_faca (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(1024) NOT NULL,
    descricao VARCHAR(255),
    ordem INTEGER,
    modelo_faca_id BIGINT NOT NULL,
    CONSTRAINT fk_imagem_modelo_faca FOREIGN KEY (modelo_faca_id) REFERENCES modelo_faca(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS opcao_personalizacao (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    opcao VARCHAR(255) NOT NULL,
    custo_adicional DECIMAL(19, 2) NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    modelo_faca_id BIGINT NOT NULL,
    CONSTRAINT fk_opcao_modelo_faca FOREIGN KEY (modelo_faca_id) REFERENCES modelo_faca(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS endereco_cliente (
    id BIGSERIAL PRIMARY KEY,
    cep VARCHAR(8) NOT NULL,
    rua VARCHAR(255) NOT NULL,
    numero VARCHAR(50) NOT NULL,
    complemento VARCHAR(255),
    bairro VARCHAR(120) NOT NULL,
    cidade VARCHAR(120) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    cliente_id BIGINT NOT NULL,
    CONSTRAINT fk_endereco_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS item_lista_desejos (
    id BIGSERIAL PRIMARY KEY,
    data_adicionado TIMESTAMP NOT NULL,
    cliente_id BIGINT NOT NULL,
    modelo_faca_id BIGINT NOT NULL,
    CONSTRAINT uk_item_lista_desejos UNIQUE (cliente_id, modelo_faca_id),
    CONSTRAINT fk_lista_desejos_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    CONSTRAINT fk_lista_desejos_modelo FOREIGN KEY (modelo_faca_id) REFERENCES modelo_faca(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS item_carrinho (
    id BIGSERIAL PRIMARY KEY,
    quantidade INTEGER NOT NULL,
    preco_unitario DECIMAL(19, 2) NOT NULL DEFAULT 0,
    preco DECIMAL(19, 2) NOT NULL DEFAULT 0,
    cliente_id BIGINT NOT NULL,
    modelo_faca_id BIGINT NOT NULL,
    CONSTRAINT fk_item_carrinho_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_carrinho_modelo FOREIGN KEY (modelo_faca_id) REFERENCES modelo_faca(id)
);

CREATE TABLE IF NOT EXISTS item_carrinho_personalizacao (
    item_carrinho_id BIGINT NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    opcao VARCHAR(255),
    PRIMARY KEY (item_carrinho_id, tipo),
    CONSTRAINT fk_item_carrinho_personalizacao FOREIGN KEY (item_carrinho_id) REFERENCES item_carrinho(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS pedido_compra (
    id BIGSERIAL PRIMARY KEY,
    numero_sistema VARCHAR(120) UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'CARRINHO',
    preco_base DECIMAL(19, 2) NOT NULL DEFAULT 0,
    custo_personalizacoes DECIMAL(19, 2) NOT NULL DEFAULT 0,
    preco_total DECIMAL(19, 2) NOT NULL DEFAULT 0,
    data_criacao TIMESTAMP NOT NULL,
    data_pagamento TIMESTAMP,
    data_entrega TIMESTAMP,
    motivo_cancelamento TEXT,
    cliente_id BIGINT NOT NULL,
    modelo_faca_id BIGINT NOT NULL,
    cuteleiro_responsavel_id BIGINT,
    endereco_entrega_id BIGINT,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_pedido_modelo FOREIGN KEY (modelo_faca_id) REFERENCES modelo_faca(id),
    CONSTRAINT fk_pedido_cuteleiro FOREIGN KEY (cuteleiro_responsavel_id) REFERENCES cuteleiro(id),
    CONSTRAINT fk_pedido_endereco FOREIGN KEY (endereco_entrega_id) REFERENCES endereco_cliente(id)
);

CREATE TABLE IF NOT EXISTS pedido_compra_personalizacao (
    pedido_compra_id BIGINT NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    opcao VARCHAR(255),
    PRIMARY KEY (pedido_compra_id, tipo),
    CONSTRAINT fk_pedido_personalizacao FOREIGN KEY (pedido_compra_id) REFERENCES pedido_compra(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS avaliacao_modelo (
    id BIGSERIAL PRIMARY KEY,
    nota INTEGER NOT NULL,
    comentario TEXT,
    data_avaliacao TIMESTAMP NOT NULL,
    verificado BOOLEAN NOT NULL DEFAULT FALSE,
    cliente_id BIGINT NOT NULL,
    modelo_faca_id BIGINT NOT NULL,
    CONSTRAINT ck_avaliacao_nota CHECK (nota BETWEEN 1 AND 5),
    CONSTRAINT fk_avaliacao_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliacao_modelo FOREIGN KEY (modelo_faca_id) REFERENCES modelo_faca(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_modelo_faca_criador ON modelo_faca(criador_id);
CREATE INDEX IF NOT EXISTS idx_pedido_compra_cliente ON pedido_compra(cliente_id);
CREATE INDEX IF NOT EXISTS idx_pedido_compra_status ON pedido_compra(status);
CREATE INDEX IF NOT EXISTS idx_item_carrinho_cliente ON item_carrinho(cliente_id);
CREATE INDEX IF NOT EXISTS idx_lista_desejos_cliente ON item_lista_desejos(cliente_id);

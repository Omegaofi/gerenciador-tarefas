-- =====================================================================
-- BANCO DE DADOS: Sistema de Gerenciamento de Projetos e Tarefas
-- Gerado a partir do diagrama ER (USUARIO, MENSAGEM, PROJETO, TAREFA,
-- ARQUIVO, ENTREGATAREFA)
-- Compatível com MySQL 8+ / MariaDB
-- =====================================================================

CREATE DATABASE IF NOT EXISTS gerenciador_projetos
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE gerenciador_projetos;

-- ---------------------------------------------------------------------
-- Tabela: USUARIO
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(150)        NOT NULL,
    email       VARCHAR(150)        NOT NULL,
    senha       VARCHAR(255)        NOT NULL,
    biografia   TEXT,
    tipo        ENUM('admin','gestor','membro') NOT NULL DEFAULT 'membro',
    criado_em   DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_usuario_email (email)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Tabela: PROJETO
-- Relação: USUARIO 1 --- N PROJETO  ("tem")
-- ---------------------------------------------------------------------
CREATE TABLE projeto (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    titulo      VARCHAR(150)        NOT NULL,
    descricao   TEXT,
    status      ENUM('planejado','em_andamento','concluido','cancelado') NOT NULL DEFAULT 'planejado',
    usuario_id  INT                 NOT NULL,   -- dono/criador do projeto
    criado_em   DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_projeto_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Tabela: MENSAGEM
-- Relações: USUARIO 1 --- N MENSAGEM ("envia")
--           PROJETO 1 --- N MENSAGEM ("possui")
-- ---------------------------------------------------------------------
CREATE TABLE mensagem (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    conteudo    TEXT                NOT NULL,
    dataEnvio   DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id  INT                 NOT NULL,   -- quem enviou
    projeto_id  INT                 NOT NULL,   -- a qual projeto pertence
    CONSTRAINT fk_mensagem_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_mensagem_projeto
        FOREIGN KEY (projeto_id) REFERENCES projeto(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Tabela: TAREFA
-- Relações: PROJETO 1 --- N TAREFA ("contem")
--           USUARIO 1 --- N TAREFA ("responsavel por")
-- ---------------------------------------------------------------------
CREATE TABLE tarefa (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    titulo      VARCHAR(150)        NOT NULL,
    prazo       DATE,
    status      ENUM('pendente','em_andamento','concluida','atrasada') NOT NULL DEFAULT 'pendente',
    projeto_id  INT                 NOT NULL,
    usuario_id  INT                 NOT NULL,   -- responsável pela tarefa
    criado_em   DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tarefa_projeto
        FOREIGN KEY (projeto_id) REFERENCES projeto(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_tarefa_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Tabela: ARQUIVO
-- Relações: USUARIO 1 --- N ARQUIVO ("adiciona")
--           TAREFA  1 --- N ARQUIVO ("anexos")
-- ---------------------------------------------------------------------
CREATE TABLE arquivo (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nomeArquivo   VARCHAR(255)      NOT NULL,
    arquivoUrl    VARCHAR(500)      NOT NULL,
    usuario_id    INT               NOT NULL,   -- quem adicionou o arquivo
    tarefa_id     INT               NULL,       -- anexo de qual tarefa (pode ser nulo)
    criado_em     DATETIME          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_arquivo_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_arquivo_tarefa
        FOREIGN KEY (tarefa_id) REFERENCES tarefa(id)
        ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Tabela: ENTREGATAREFA
-- Relações: TAREFA 1 --- N ENTREGATAREFA ("recebe")
--           ARQUIVO 1 --- N ENTREGATAREFA ("entrega")
-- ---------------------------------------------------------------------
CREATE TABLE entregatarefa (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    dataEntrega   DATETIME          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status        ENUM('enviada','em_revisao','aprovada','rejeitada') NOT NULL DEFAULT 'enviada',
    tarefa_id     INT               NOT NULL,
    arquivo_id    INT               NULL,       -- arquivo entregue (opcional)
    CONSTRAINT fk_entrega_tarefa
        FOREIGN KEY (tarefa_id) REFERENCES tarefa(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_entrega_arquivo
        FOREIGN KEY (arquivo_id) REFERENCES arquivo(id)
        ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Índices adicionais para melhorar performance de consultas comuns
-- ---------------------------------------------------------------------
CREATE INDEX idx_mensagem_projeto   ON mensagem(projeto_id);
CREATE INDEX idx_mensagem_usuario   ON mensagem(usuario_id);
CREATE INDEX idx_tarefa_projeto     ON tarefa(projeto_id);
CREATE INDEX idx_tarefa_usuario     ON tarefa(usuario_id);
CREATE INDEX idx_arquivo_tarefa     ON arquivo(tarefa_id);
CREATE INDEX idx_entrega_tarefa     ON entregatarefa(tarefa_id);

-- =====================================================================
-- DADOS DE EXEMPLO (opcional - remova se não quiser popular o banco)
-- =====================================================================
INSERT INTO usuario (nome, email, senha, biografia, tipo) VALUES
('Ana Silva', 'ana.silva@email.com', 'hash_senha_1', 'Gestora de projetos.', 'gestor'),
('Bruno Costa', 'bruno.costa@email.com', 'hash_senha_2', 'Desenvolvedor backend.', 'membro');

INSERT INTO projeto (titulo, descricao, status, usuario_id) VALUES
('Sistema de Vendas', 'Aplicação web para controle de vendas.', 'em_andamento', 1);

INSERT INTO tarefa (titulo, prazo, status, projeto_id, usuario_id) VALUES
('Criar API de produtos', '2026-09-01', 'pendente', 1, 2);

INSERT INTO mensagem (conteudo, usuario_id, projeto_id) VALUES
('Bem-vindos ao projeto! Vamos organizar as entregas por aqui.', 1, 1);

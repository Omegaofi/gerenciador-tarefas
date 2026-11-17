-- Migração do chat cifrado (projeto "Chat Cifrado NexProject", opção A).
-- Não tem USE de propósito: rode no banco do projeto, por exemplo
--   /opt/lampp/bin/mysql -u root gerenciador_projetos < migracao_chat_cifrado.sql
-- Pode ser rodado mais de uma vez (IF NOT EXISTS, sintaxe do MariaDB do XAMPP).

-- 1) Membros do projeto (base do RF008 e da lista de destinatários)
CREATE TABLE IF NOT EXISTS projeto_participante (
    projeto_id     INT      NOT NULL,
    usuario_id     INT      NOT NULL,
    status         ENUM('pendente','aceito','recusado') NOT NULL DEFAULT 'pendente',
    data_inscricao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (projeto_id, usuario_id),
    FOREIGN KEY (projeto_id) REFERENCES projeto(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 2) Chaves do usuário: a pública aberta, a privada trancada com a senha (Base64)
ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS chave_publica         VARCHAR(130) NULL,   -- P-256 X.509, 124 caracteres
    ADD COLUMN IF NOT EXISTS chave_privada_cifrada VARCHAR(255) NULL,   -- PKCS#8 + tag do GCM
    ADD COLUMN IF NOT EXISTS salt_chave            VARCHAR(24)  NULL,
    ADD COLUMN IF NOT EXISTS iv_chave              VARCHAR(24)  NULL;

-- 3) Mensagem cifrada: IV do AES-GCM
ALTER TABLE mensagem
    ADD COLUMN IF NOT EXISTS iv VARCHAR(24) NULL AFTER conteudo;

-- 4) Uma cópia da chave da mensagem para cada membro
CREATE TABLE IF NOT EXISTS mensagem_chave (
    mensagem_id     INT         NOT NULL,
    destinatario_id INT         NOT NULL,
    chave_cifrada   VARCHAR(80) NOT NULL,
    iv              VARCHAR(24) NOT NULL,
    PRIMARY KEY (mensagem_id, destinatario_id),
    FOREIGN KEY (mensagem_id)     REFERENCES mensagem(id) ON DELETE CASCADE,
    FOREIGN KEY (destinatario_id) REFERENCES usuario(id)  ON DELETE CASCADE
) ENGINE=InnoDB;

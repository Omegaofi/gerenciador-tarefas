-- Migração do banco gerenciador_projetos para o modelo NexProject
-- (perfis Criador/Participante, área de atuação do projeto e senhas com SHA-256).
-- Rode uma única vez: mysql -uroot gerenciador_projetos < migracao_nexproject.sql

USE gerenciador_projetos;

-- 1) Perfis do documento: Criador (idealizador) e Participante (colaborador)
ALTER TABLE usuario
    MODIFY tipo ENUM('admin','gestor','membro','criador','participante') NOT NULL DEFAULT 'participante';
UPDATE usuario SET tipo = 'criador' WHERE tipo IN ('admin','gestor');
UPDATE usuario SET tipo = 'participante' WHERE tipo = 'membro';
ALTER TABLE usuario
    MODIFY tipo ENUM('criador','participante') NOT NULL DEFAULT 'participante';

-- 2) Senhas antigas em texto puro passam a ser SHA-256 (64 caracteres hexadecimais)
UPDATE usuario SET senha = SHA2(senha, 256) WHERE LENGTH(senha) <> 64;

-- 3) RF003: área de atuação do projeto
ALTER TABLE projeto
    ADD COLUMN area_atuacao VARCHAR(100) NULL AFTER descricao;

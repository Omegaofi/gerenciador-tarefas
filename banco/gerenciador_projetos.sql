-- NexProject — estrutura completa do banco gerenciador_projetos (MySQL 8+ / MariaDB 10.4+)
-- Já inclui as migrações NexProject e do chat cifrado: use este script para uma instalação nova.
--   mysql -u root < banco/gerenciador_projetos.sql
-- Para atualizar um banco antigo, rode migracao_nexproject.sql e depois migracao_chat_cifrado.sql.

-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: gerenciador_projetos
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `gerenciador_projetos`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `gerenciador_projetos` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `gerenciador_projetos`;

--
-- Table structure for table `arquivo`
--

DROP TABLE IF EXISTS `arquivo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arquivo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nomeArquivo` varchar(255) NOT NULL,
  `arquivoUrl` varchar(500) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `tarefa_id` int(11) DEFAULT NULL,
  `criado_em` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `fk_arquivo_usuario` (`usuario_id`),
  KEY `idx_arquivo_tarefa` (`tarefa_id`),
  CONSTRAINT `fk_arquivo_tarefa` FOREIGN KEY (`tarefa_id`) REFERENCES `tarefa` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_arquivo_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
--

--
-- Table structure for table `entregatarefa`
--

DROP TABLE IF EXISTS `entregatarefa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entregatarefa` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dataEntrega` datetime NOT NULL DEFAULT current_timestamp(),
  `status` enum('enviada','em_revisao','aprovada','rejeitada') NOT NULL DEFAULT 'enviada',
  `tarefa_id` int(11) NOT NULL,
  `arquivo_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_entrega_arquivo` (`arquivo_id`),
  KEY `idx_entrega_tarefa` (`tarefa_id`),
  CONSTRAINT `fk_entrega_arquivo` FOREIGN KEY (`arquivo_id`) REFERENCES `arquivo` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_entrega_tarefa` FOREIGN KEY (`tarefa_id`) REFERENCES `tarefa` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
--

--
-- Table structure for table `mensagem`
--

DROP TABLE IF EXISTS `mensagem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mensagem` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `conteudo` text NOT NULL,
  `iv` varchar(24) DEFAULT NULL,
  `dataEnvio` datetime NOT NULL DEFAULT current_timestamp(),
  `usuario_id` int(11) NOT NULL,
  `projeto_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_mensagem_projeto` (`projeto_id`),
  KEY `idx_mensagem_usuario` (`usuario_id`),
  CONSTRAINT `fk_mensagem_projeto` FOREIGN KEY (`projeto_id`) REFERENCES `projeto` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mensagem_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
--

--
-- Table structure for table `mensagem_chave`
--

DROP TABLE IF EXISTS `mensagem_chave`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mensagem_chave` (
  `mensagem_id` int(11) NOT NULL,
  `destinatario_id` int(11) NOT NULL,
  `chave_cifrada` varchar(80) NOT NULL,
  `iv` varchar(24) NOT NULL,
  PRIMARY KEY (`mensagem_id`,`destinatario_id`),
  KEY `destinatario_id` (`destinatario_id`),
  CONSTRAINT `mensagem_chave_ibfk_1` FOREIGN KEY (`mensagem_id`) REFERENCES `mensagem` (`id`) ON DELETE CASCADE,
  CONSTRAINT `mensagem_chave_ibfk_2` FOREIGN KEY (`destinatario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
--

--
-- Table structure for table `projeto`
--

DROP TABLE IF EXISTS `projeto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projeto` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `titulo` varchar(150) NOT NULL,
  `descricao` text DEFAULT NULL,
  `area_atuacao` varchar(100) DEFAULT NULL,
  `status` enum('planejado','em_andamento','concluido','cancelado') NOT NULL DEFAULT 'planejado',
  `usuario_id` int(11) NOT NULL,
  `criado_em` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `fk_projeto_usuario` (`usuario_id`),
  CONSTRAINT `fk_projeto_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
--

--
-- Table structure for table `projeto_participante`
--

DROP TABLE IF EXISTS `projeto_participante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projeto_participante` (
  `projeto_id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `status` enum('pendente','aceito','recusado') NOT NULL DEFAULT 'pendente',
  `data_inscricao` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`projeto_id`,`usuario_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `projeto_participante_ibfk_1` FOREIGN KEY (`projeto_id`) REFERENCES `projeto` (`id`) ON DELETE CASCADE,
  CONSTRAINT `projeto_participante_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
--

--
-- Table structure for table `tarefa`
--

DROP TABLE IF EXISTS `tarefa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tarefa` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `titulo` varchar(150) NOT NULL,
  `prazo` date DEFAULT NULL,
  `status` enum('pendente','em_andamento','concluida','atrasada') NOT NULL DEFAULT 'pendente',
  `projeto_id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `criado_em` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_tarefa_projeto` (`projeto_id`),
  KEY `idx_tarefa_usuario` (`usuario_id`),
  CONSTRAINT `fk_tarefa_projeto` FOREIGN KEY (`projeto_id`) REFERENCES `projeto` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_tarefa_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
--

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuario` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) NOT NULL,
  `email` varchar(150) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `biografia` text DEFAULT NULL,
  `tipo` enum('criador','participante') NOT NULL DEFAULT 'participante',
  `criado_em` datetime NOT NULL DEFAULT current_timestamp(),
  `chave_publica` varchar(130) DEFAULT NULL,
  `chave_privada_cifrada` varchar(255) DEFAULT NULL,
  `salt_chave` varchar(24) DEFAULT NULL,
  `iv_chave` varchar(24) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuario_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
--

--
-- Dados de demonstração (senha das duas contas: 123456, guardada em BCrypt)
-- As chaves do chat cifrado ficam NULL: são geradas pelo cliente no primeiro login.
--

INSERT INTO `usuario` (`id`, `nome`, `email`, `senha`, `biografia`, `tipo`) VALUES
(1,'Ana Silva','ana.silva@email.com','$2a$10$33ivB6pGu.2wd/PaJTMo6O/hG1v3KVVuW5uq3kWUNM6d7wWujwOlm','Gestora de projetos.','criador'),
(2,'Bruno Costa','bruno.costa@email.com','$2a$10$ZQIrxWvY54eG0eRIexIP.u74QnY6bUuBgIixZ7OsEV7eG2/6M29Ki','Desenvolvedor backend.','participante');

INSERT INTO `projeto` (`id`, `titulo`, `descricao`, `area_atuacao`, `status`, `usuario_id`) VALUES
(1,'Sistema de Vendas','Aplicação web para controle de vendas.','Desenvolvimento Web','em_andamento',1);

INSERT INTO `projeto_participante` (`projeto_id`, `usuario_id`, `status`) VALUES
(1,2,'aceito');

INSERT INTO `tarefa` (`id`, `titulo`, `prazo`, `status`, `projeto_id`, `usuario_id`) VALUES
(1,'Criar API de produtos','2026-12-01','pendente',1,2);

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


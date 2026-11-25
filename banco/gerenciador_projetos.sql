-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: gerenciador_projetos
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

/*!40000 DROP DATABASE IF EXISTS `gerenciador_projetos`*/;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `arquivo`
--

LOCK TABLES `arquivo` WRITE;
/*!40000 ALTER TABLE `arquivo` DISABLE KEYS */;
INSERT INTO `arquivo` VALUES (1,'Tela de login pronta com validação.','https://github.com/exemplo/carona/pull/1',4,NULL,'2026-11-19 21:56:14'),(2,'Primeira versão do mapa.','https://github.com/exemplo/carona/pull/2',4,NULL,'2026-11-19 21:56:26'),(3,'Entrega - fazer o mobile','https://apnp.moodle.com.br',7,7,'2026-11-19 22:19:16');
/*!40000 ALTER TABLE `arquivo` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entregatarefa`
--

LOCK TABLES `entregatarefa` WRITE;
/*!40000 ALTER TABLE `entregatarefa` DISABLE KEYS */;
INSERT INTO `entregatarefa` VALUES (3,'2026-11-19 22:19:16','enviada',7,3);
/*!40000 ALTER TABLE `entregatarefa` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mensagem`
--

LOCK TABLES `mensagem` WRITE;
/*!40000 ALTER TABLE `mensagem` DISABLE KEYS */;
INSERT INTO `mensagem` VALUES (1,'xcVwfoSJnaOZyvbmEyDTPFDZLTIMECdaXAt2Ps0yD6HgAIK3VeYkIskU4bc5VHHL6Q/f2ww26xYnPw3wOMdwv1KIQt2UJo5gvas=','UJROjseNYzDK6n4b','2026-11-19 21:55:41',3,2),(2,'913JmJ7ypuf6GqNsNCapn9PCFXYWlA/gpP6hPaBWTlEIqFFpF+ynXJbtQqp9ZOEjsTTE/RVZd1y3','685jJ5o7/ovlKNRR','2026-11-19 21:56:42',4,2),(3,'dtwo+iK2Dl+RdB7qf5sTUE5zqDBuggCfV9oEiQ==','Hq4XndIXeXvgKYO3','2026-11-19 22:12:42',6,6),(4,'lZq9PNofWcWNRvz1jwEJ+kI7GmdN8epOQ0WfLabakIomTw==','ZGUBz+ZB6GzIk05t','2026-11-19 22:18:08',6,6),(5,'D9a0MzYizYoQcuKY0kOWbX6jlXHRivT22lwQ2Ly5Lcw=','uRmqVD9V23O1WUDd','2026-11-19 22:19:46',7,6);
/*!40000 ALTER TABLE `mensagem` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `mensagem_chave`
--

LOCK TABLES `mensagem_chave` WRITE;
/*!40000 ALTER TABLE `mensagem_chave` DISABLE KEYS */;
INSERT INTO `mensagem_chave` VALUES (1,3,'NIAClVCmMFdpsueFxteJP4uX85nWNV+/woYEgWFtXLApTftoOHf5v25+EWn5pwJE','ibImitcNzf0Hjm1v'),(1,4,'KtXh1qVKVZ3DvDjUuFWVOHBmF1wB8YpEcWitTcX3OXwRN1g9pRHWM7j2xR52Twqe','wNCaY2tvapMPAtHf'),(2,3,'8Z3KcS8Qne27JGVdAVelbkmGM3m/S8veLb8a/SAMdTcdz5jNPGDNQL2Zrlnhdc4m','DMSnN51UV2V+PT6b'),(2,4,'LSvPLfKSm7baIy0GJXUbkbORyycjSLgZVzp9zJHY7j41+HMoT8QQRW0V94EYXc5F','ysNKLsp0U4jxsL1b'),(3,6,'eEXirdAgrhB6HPzaNmdfBe0C/R4Pnd/L9W4GKChaP6KY9t9oWD22oxioKTJNqip6','6zC5dJsOP8Yf55mA'),(4,6,'O+waR2VDRij7p1RZtvEZronbJ027OkiHF5mf8wbBtvOqFavT/DfazGAmexhomwci','yTwD6jsqCZ2KBIQj'),(4,7,'9nF5/T7a3Pxs3kIR84yMBYgx6IOrAFgXl/+qO2bc2iwX0mXpY6aL0Fd73P2LQP7h','I8JliWRzL/koKtVn'),(5,6,'nSuVjn5E/pfPtSeQY/g4FvbIE/RoCPul/iVptlUTr8+sc8XT/XiI9X1sDwfKWK3o','KwlBWD/N1Ql9dsFn'),(5,7,'7d92a5yjWg8dVj9ebTwtRuGf1VM+wkzCBYZEquPlf2e0pOJK3sHG+Ofa4+9xDx7i','GvEVtY8aglwm7xTx');
/*!40000 ALTER TABLE `mensagem_chave` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projeto`
--

LOCK TABLES `projeto` WRITE;
/*!40000 ALTER TABLE `projeto` DISABLE KEYS */;
INSERT INTO `projeto` VALUES (1,'Sistema de Vendas','Aplicação web para controle de vendas.','Desenvolvimento Web','em_andamento',1,'2026-11-19 22:39:03'),(2,'App de Carona Universitária','Aplicativo para alunos dividirem caronas até o campus.','Desenvolvimento Mobile','planejado',3,'2026-11-19 21:52:38'),(5,'sadasdasdasd','asdasdsa','Desenvolvimento Web','planejado',3,'2026-11-19 22:08:41'),(6,'teste video top','se inscreva para participar','Jogos','em_andamento',6,'2026-11-19 22:12:16');
/*!40000 ALTER TABLE `projeto` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `projeto_participante`
--

LOCK TABLES `projeto_participante` WRITE;
/*!40000 ALTER TABLE `projeto_participante` DISABLE KEYS */;
INSERT INTO `projeto_participante` VALUES (1,2,'aceito','2026-11-19 22:39:03'),(1,7,'pendente','2026-11-19 22:19:25'),(2,4,'aceito','2026-11-19 21:53:45'),(2,7,'pendente','2026-11-19 22:19:22'),(6,7,'aceito','2026-11-19 22:14:41');
/*!40000 ALTER TABLE `projeto_participante` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tarefa`
--

LOCK TABLES `tarefa` WRITE;
/*!40000 ALTER TABLE `tarefa` DISABLE KEYS */;
INSERT INTO `tarefa` VALUES (1,'Criar API de produtos','2026-12-01','pendente',1,2,'2026-11-19 22:39:03'),(7,'fazer o mobile','2026-11-25','pendente',6,7,'2026-11-19 22:17:54');
/*!40000 ALTER TABLE `tarefa` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Ana Silva','ana.silva@email.com','ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f','Gestora de projetos.','criador','2026-11-19 22:39:03',NULL,NULL,NULL,NULL),(2,'Bruno Costa','bruno.costa@email.com','ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f','Desenvolvedor backend.','participante','2026-11-19 22:39:03',NULL,NULL,NULL,NULL),(3,'Carla Mendes','carla.demo@nexproject.com','ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f',NULL,'criador','2026-11-19 21:51:50','MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEWlGvQpKk4b+LBpuIA6gx4swWmxdgFsAUmxGPPmJXLUz5mTuKdsdnq2IpxeNX2kPMPZTutRa+SsEB63WXb0g/vw==','m1cbQA9IJsJlUR3Zn6on37du6u/BXeo89QPshRTYP2b35y7T1V4ivFNL0Vj1p5Q31y7ylUOBcRsB4Wcn+9yetptwBSuF50WNLoLt5+a04v4hIAU=','7KAo1kqEhAO5qdwQOJcEbQ==','ST37bewYAVd+JS5f'),(4,'Diego Rocha','diego.demo@nexproject.com','ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f',NULL,'participante','2026-11-19 21:52:04','MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEPHM0tusyGkF4crm/2jT3uyG86XjIIFdJi0ziHSIgRTV4MTg40NCM7ZCx+lhtYQ1xRpiKp/HuZCZDUiqYen+jWA==','+6Hji1gqmTPQldHP2yViJu3oEGHr/GV1ftbQXABeZDWI3dTs6ugHZMgelMwRjZ6oOaDuq1ixBU8+2zOwtTTiNn3b32pE9u9FTsiVT/VyzswAIxQ=','TG5Ov0F4S51vxl6BluU8vA==','djX0yPUCk4MDNO9f'),(6,'joao pedro carvalho theis','ADMIN.nex@nexprojet.com','ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f',NULL,'criador','2026-11-19 22:11:34','MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEccRX1QUUTcfB+6+IwrYfvsbmKlE+5L+XnrECKt8MGtxV8s1yvMSB4r1DX/qqgfjAAHmkeOKGsDFoPtp2mhSQrw==','2FCvXHx+waNWMlk175MyFruOdFvu5SeqHABJbvF81ZAat+I5hkv4eTqn3ePG27RqKrbDpnE7agZopLSI4lA8R5SjLClVYhqFv4mSfoxqbmxY8bo=','xzEOULvI/kpLDyp5mSwtqg==','eJtosZqWbIcRvxBz'),(7,'JOASD','SHATKIP@GMAIL.COM','ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f',NULL,'participante','2026-11-19 22:14:31','MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEqNvYyRyiRbZpYzycf/8rRjeOrvioSZojLKldpcSsoTfC+m3DP24SJ3WtHEt/nSR2z3tn+F8uRkklm6VdK2i2xg==','7u71Qhmdi17FZjb5vTC4KqnrbIAYvDT5+ZB9MEzCpomzfaT+UKdioFPfp/MA7CW/rZo7elDq11LzhRSnF/Q5CedzVBhSgNnEFvGoyk5NYf3/PjQ=','JBn3k9Vp+gUihTHkLJxTqw==','wAWTzGQ2H8iCjoqR');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed

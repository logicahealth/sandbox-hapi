-- MySQL dump 10.13  Distrib 5.6.35, for macos10.12 (x86_64)
--
-- Host: localhost    Database: hspc_3_hspc3
-- ------------------------------------------------------
-- Server version	5.6.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `hfj_forced_id`
--

DROP TABLE IF EXISTS `hfj_forced_id`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_forced_id` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `forced_id` varchar(100) NOT NULL,
  `resource_pid` bigint(20) NOT NULL,
  `resource_type` varchar(100) DEFAULT '',
  PRIMARY KEY (`pid`),
  UNIQUE KEY `IDX_FORCEDID_RESID` (`resource_pid`),
  UNIQUE KEY `IDX_FORCEDID_TYPE_RESID` (`resource_type`,`resource_pid`),
  KEY `IDX_FORCEDID_TYPE_FORCEDID` (`resource_type`,`forced_id`),
  CONSTRAINT `FKorutg4p1jgmfyfxdagxpcosuy` FOREIGN KEY (`resource_pid`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_forced_id`
--

LOCK TABLES `hfj_forced_id` WRITE;
/*!40000 ALTER TABLE `hfj_forced_id` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_forced_id` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_history_tag`
--

DROP TABLE IF EXISTS `hfj_history_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_history_tag` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `tag_id` bigint(20) DEFAULT NULL,
  `res_id` bigint(20) NOT NULL,
  `res_type` varchar(30) NOT NULL,
  `res_ver_pid` bigint(20) NOT NULL,
  PRIMARY KEY (`pid`),
  KEY `FK3gc37g8b2c9qcrrccw7s50inw` (`tag_id`),
  KEY `FK1avip8hipbvvn62l2aerf5ajv` (`res_ver_pid`),
  CONSTRAINT `FK1avip8hipbvvn62l2aerf5ajv` FOREIGN KEY (`res_ver_pid`) REFERENCES `hfj_res_ver` (`pid`),
  CONSTRAINT `FK3gc37g8b2c9qcrrccw7s50inw` FOREIGN KEY (`tag_id`) REFERENCES `hfj_tag_def` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_history_tag`
--

LOCK TABLES `hfj_history_tag` WRITE;
/*!40000 ALTER TABLE `hfj_history_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_history_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_res_link`
--

DROP TABLE IF EXISTS `hfj_res_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_res_link` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `src_path` varchar(100) NOT NULL,
  `src_resource_id` bigint(20) NOT NULL,
  `source_resource_type` varchar(30) NOT NULL DEFAULT '',
  `target_resource_id` bigint(20) DEFAULT NULL,
  `target_resource_type` varchar(30) NOT NULL DEFAULT '',
  `target_resource_url` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`pid`),
  KEY `IDX_RL_TPATHRES` (`src_path`,`target_resource_id`),
  KEY `IDX_RL_SRC` (`src_resource_id`),
  KEY `IDX_RL_DEST` (`target_resource_id`),
  CONSTRAINT `FKfowb41bn1xrmbwklledq1r38u` FOREIGN KEY (`target_resource_id`) REFERENCES `hfj_resource` (`res_id`),
  CONSTRAINT `FKlk8p47lbbihulsqvwafloi4yb` FOREIGN KEY (`src_resource_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_res_link`
--

LOCK TABLES `hfj_res_link` WRITE;
/*!40000 ALTER TABLE `hfj_res_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_res_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_res_tag`
--

DROP TABLE IF EXISTS `hfj_res_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_res_tag` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `tag_id` bigint(20) DEFAULT NULL,
  `res_id` bigint(20) DEFAULT NULL,
  `res_type` varchar(30) NOT NULL,
  PRIMARY KEY (`pid`),
  KEY `FK4kiphkwif9illrg0jtooom2w1` (`tag_id`),
  KEY `FK5g62tiq5ojwcpcax3l7etxmv2` (`res_id`),
  CONSTRAINT `FK4kiphkwif9illrg0jtooom2w1` FOREIGN KEY (`tag_id`) REFERENCES `hfj_tag_def` (`tag_id`),
  CONSTRAINT `FK5g62tiq5ojwcpcax3l7etxmv2` FOREIGN KEY (`res_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_res_tag`
--

LOCK TABLES `hfj_res_tag` WRITE;
/*!40000 ALTER TABLE `hfj_res_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_res_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_res_ver`
--

DROP TABLE IF EXISTS `hfj_res_ver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_res_ver` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `res_deleted_at` datetime DEFAULT NULL,
  `res_encoding` varchar(5) NOT NULL,
  `res_version` varchar(7) DEFAULT NULL,
  `has_tags` bit(1) NOT NULL,
  `res_published` datetime NOT NULL,
  `res_text` longblob NOT NULL,
  `res_title` varchar(100) DEFAULT NULL,
  `res_updated` datetime NOT NULL,
  `res_id` bigint(20) DEFAULT NULL,
  `res_type` varchar(30) NOT NULL,
  `res_ver` bigint(20) NOT NULL,
  `forced_id_pid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`pid`),
  UNIQUE KEY `IDX_RESVER_ID_VER` (`res_id`,`res_ver`),
  KEY `IDX_RESVER_TYPE_DATE` (`res_type`,`res_updated`),
  KEY `IDX_RESVER_ID_DATE` (`res_id`,`res_updated`),
  KEY `IDX_RESVER_DATE` (`res_updated`),
  KEY `FKskhdrnruqjs5fs0kl2uv04kfk` (`forced_id_pid`),
  CONSTRAINT `FKskhdrnruqjs5fs0kl2uv04kfk` FOREIGN KEY (`forced_id_pid`) REFERENCES `hfj_forced_id` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_res_ver`
--

LOCK TABLES `hfj_res_ver` WRITE;
/*!40000 ALTER TABLE `hfj_res_ver` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_res_ver` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_resource`
--

DROP TABLE IF EXISTS `hfj_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_resource` (
  `res_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `res_deleted_at` datetime DEFAULT NULL,
  `res_encoding` varchar(5) NOT NULL,
  `res_version` varchar(7) DEFAULT NULL,
  `has_tags` bit(1) NOT NULL,
  `res_published` datetime NOT NULL,
  `res_text` longblob NOT NULL,
  `res_title` varchar(100) DEFAULT NULL,
  `res_updated` datetime NOT NULL,
  `sp_has_links` bit(1) DEFAULT NULL,
  `sp_index_status` bigint(20) DEFAULT NULL,
  `res_language` varchar(20) DEFAULT NULL,
  `sp_coords_present` bit(1) DEFAULT NULL,
  `sp_date_present` bit(1) DEFAULT NULL,
  `sp_number_present` bit(1) DEFAULT NULL,
  `sp_quantity_present` bit(1) DEFAULT NULL,
  `sp_string_present` bit(1) DEFAULT NULL,
  `sp_token_present` bit(1) DEFAULT NULL,
  `sp_uri_present` bit(1) DEFAULT NULL,
  `res_profile` varchar(200) DEFAULT NULL,
  `res_type` varchar(30) DEFAULT NULL,
  `res_ver` bigint(20) DEFAULT NULL,
  `forced_id_pid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`res_id`),
  KEY `IDX_RES_DATE` (`res_updated`),
  KEY `IDX_RES_LANG` (`res_type`,`res_language`),
  KEY `IDX_RES_PROFILE` (`res_profile`),
  KEY `IDX_INDEXSTATUS` (`sp_index_status`),
  KEY `FKpm7n4gliswjf4au4s0s8xlo9m` (`forced_id_pid`),
  CONSTRAINT `FKpm7n4gliswjf4au4s0s8xlo9m` FOREIGN KEY (`forced_id_pid`) REFERENCES `hfj_forced_id` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_resource`
--

LOCK TABLES `hfj_resource` WRITE;
/*!40000 ALTER TABLE `hfj_resource` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_search`
--

DROP TABLE IF EXISTS `hfj_search`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_search` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `last_updated_high` datetime DEFAULT NULL,
  `last_updated_low` datetime DEFAULT NULL,
  `preferred_page_size` int(11) DEFAULT NULL,
  `resource_id` bigint(20) DEFAULT NULL,
  `resource_type` varchar(200) DEFAULT NULL,
  `search_type` int(11) NOT NULL,
  `total_count` int(11) NOT NULL,
  `search_uuid` varchar(40) NOT NULL,
  PRIMARY KEY (`pid`),
  UNIQUE KEY `IDX_SEARCH_UUID` (`search_uuid`),
  KEY `JDX_SEARCH_CREATED` (`created`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_search`
--

LOCK TABLES `hfj_search` WRITE;
/*!40000 ALTER TABLE `hfj_search` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_search` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_search_include`
--

DROP TABLE IF EXISTS `hfj_search_include`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_search_include` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `search_include` varchar(200) NOT NULL,
  `inc_recurse` bit(1) NOT NULL,
  `revinclude` bit(1) NOT NULL,
  `search_pid` bigint(20) NOT NULL,
  PRIMARY KEY (`pid`),
  KEY `FK_SEARCHINC_SEARCH` (`search_pid`),
  CONSTRAINT `FK_SEARCHINC_SEARCH` FOREIGN KEY (`search_pid`) REFERENCES `hfj_search` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_search_include`
--

LOCK TABLES `hfj_search_include` WRITE;
/*!40000 ALTER TABLE `hfj_search_include` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_search_include` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_search_result`
--

DROP TABLE IF EXISTS `hfj_search_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_search_result` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `search_order` int(11) NOT NULL,
  `resource_pid` bigint(20) NOT NULL,
  `search_pid` bigint(20) NOT NULL,
  PRIMARY KEY (`pid`),
  UNIQUE KEY `IDX_SEARCHRES_ORDER` (`search_pid`,`search_order`),
  KEY `FK_SEARCHRES_RES` (`resource_pid`),
  CONSTRAINT `FK_SEARCHRES_RES` FOREIGN KEY (`resource_pid`) REFERENCES `hfj_resource` (`res_id`),
  CONSTRAINT `FK_SEARCHRES_SEARCH` FOREIGN KEY (`search_pid`) REFERENCES `hfj_search` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_search_result`
--

LOCK TABLES `hfj_search_result` WRITE;
/*!40000 ALTER TABLE `hfj_search_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_search_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_spidx_coords`
--

DROP TABLE IF EXISTS `hfj_spidx_coords`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_spidx_coords` (
  `sp_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sp_name` varchar(100) NOT NULL,
  `res_id` bigint(20) DEFAULT NULL,
  `res_type` varchar(255) NOT NULL,
  `sp_latitude` double DEFAULT NULL,
  `sp_longitude` double DEFAULT NULL,
  PRIMARY KEY (`sp_id`),
  KEY `IDX_SP_COORDS` (`res_type`,`sp_name`,`sp_latitude`,`sp_longitude`),
  KEY `IDX_SP_COORDS_RESID` (`res_id`),
  CONSTRAINT `FKnxqpd20g9qggkvcwkospuc8cb` FOREIGN KEY (`res_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_spidx_coords`
--

LOCK TABLES `hfj_spidx_coords` WRITE;
/*!40000 ALTER TABLE `hfj_spidx_coords` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_spidx_coords` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_spidx_date`
--

DROP TABLE IF EXISTS `hfj_spidx_date`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_spidx_date` (
  `sp_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sp_name` varchar(100) NOT NULL,
  `res_id` bigint(20) DEFAULT NULL,
  `res_type` varchar(255) NOT NULL,
  `sp_value_high` datetime DEFAULT NULL,
  `sp_value_low` datetime DEFAULT NULL,
  PRIMARY KEY (`sp_id`),
  KEY `IDX_SP_DATE` (`res_type`,`sp_name`,`sp_value_low`,`sp_value_high`),
  KEY `IDX_SP_DATE_RESID` (`res_id`),
  CONSTRAINT `FK30uej62bjadrnsrru0gp5oqwv` FOREIGN KEY (`res_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_spidx_date`
--

LOCK TABLES `hfj_spidx_date` WRITE;
/*!40000 ALTER TABLE `hfj_spidx_date` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_spidx_date` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_spidx_number`
--

DROP TABLE IF EXISTS `hfj_spidx_number`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_spidx_number` (
  `sp_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sp_name` varchar(100) NOT NULL,
  `res_id` bigint(20) DEFAULT NULL,
  `res_type` varchar(255) NOT NULL,
  `sp_value` decimal(19,2) DEFAULT NULL,
  PRIMARY KEY (`sp_id`),
  KEY `IDX_SP_NUMBER` (`res_type`,`sp_name`,`sp_value`),
  KEY `IDX_SP_NUMBER_RESID` (`res_id`),
  CONSTRAINT `FK3ro12qh41h23gi7v5t1dsnjsc` FOREIGN KEY (`res_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_spidx_number`
--

LOCK TABLES `hfj_spidx_number` WRITE;
/*!40000 ALTER TABLE `hfj_spidx_number` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_spidx_number` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_spidx_quantity`
--

DROP TABLE IF EXISTS `hfj_spidx_quantity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_spidx_quantity` (
  `sp_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sp_name` varchar(100) NOT NULL,
  `res_id` bigint(20) DEFAULT NULL,
  `res_type` varchar(255) NOT NULL,
  `sp_system` varchar(200) DEFAULT NULL,
  `sp_units` varchar(200) DEFAULT NULL,
  `sp_value` decimal(19,2) DEFAULT NULL,
  PRIMARY KEY (`sp_id`),
  KEY `IDX_SP_QUANTITY` (`res_type`,`sp_name`,`sp_system`,`sp_units`,`sp_value`),
  KEY `IDX_SP_QUANTITY_RESID` (`res_id`),
  CONSTRAINT `FKga8jnecq3et3xrhx9vpg7ahtq` FOREIGN KEY (`res_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_spidx_quantity`
--

LOCK TABLES `hfj_spidx_quantity` WRITE;
/*!40000 ALTER TABLE `hfj_spidx_quantity` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_spidx_quantity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_spidx_string`
--

DROP TABLE IF EXISTS `hfj_spidx_string`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_spidx_string` (
  `sp_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sp_name` varchar(100) NOT NULL,
  `res_id` bigint(20) DEFAULT NULL,
  `res_type` varchar(255) NOT NULL,
  `sp_value_exact` varchar(200) DEFAULT NULL,
  `sp_value_normalized` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`sp_id`),
  KEY `IDX_SP_STRING` (`res_type`,`sp_name`,`sp_value_normalized`),
  KEY `IDX_SP_STRING_RESID` (`res_id`),
  CONSTRAINT `FKk7lomqcc1y2eir87w09finx3j` FOREIGN KEY (`res_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_spidx_string`
--

LOCK TABLES `hfj_spidx_string` WRITE;
/*!40000 ALTER TABLE `hfj_spidx_string` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_spidx_string` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_spidx_token`
--

DROP TABLE IF EXISTS `hfj_spidx_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_spidx_token` (
  `sp_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sp_name` varchar(100) NOT NULL,
  `res_id` bigint(20) DEFAULT NULL,
  `res_type` varchar(255) NOT NULL,
  `sp_system` varchar(200) DEFAULT NULL,
  `sp_value` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`sp_id`),
  KEY `IDX_SP_TOKEN` (`res_type`,`sp_name`,`sp_system`,`sp_value`),
  KEY `IDX_SP_TOKEN_UNQUAL` (`res_type`,`sp_name`,`sp_value`),
  KEY `IDX_SP_TOKEN_RESID` (`res_id`),
  CONSTRAINT `FKec4ct4jmgago3lxhrgrrq4pwy` FOREIGN KEY (`res_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_spidx_token`
--

LOCK TABLES `hfj_spidx_token` WRITE;
/*!40000 ALTER TABLE `hfj_spidx_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_spidx_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_spidx_uri`
--

DROP TABLE IF EXISTS `hfj_spidx_uri`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_spidx_uri` (
  `sp_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sp_name` varchar(100) NOT NULL,
  `res_id` bigint(20) DEFAULT NULL,
  `res_type` varchar(255) NOT NULL,
  `sp_uri` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`sp_id`),
  KEY `IDX_SP_URI` (`res_type`,`sp_name`,`sp_uri`),
  KEY `IDX_SP_URI_RESTYPE_NAME` (`res_type`,`sp_name`),
  KEY `IDX_SP_URI_COORDS` (`res_id`),
  CONSTRAINT `FKfa98npqetoy8qtbjs69safrju` FOREIGN KEY (`res_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_spidx_uri`
--

LOCK TABLES `hfj_spidx_uri` WRITE;
/*!40000 ALTER TABLE `hfj_spidx_uri` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_spidx_uri` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_subscription`
--

DROP TABLE IF EXISTS `hfj_subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_subscription` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_interval` bigint(20) NOT NULL,
  `created_time` datetime NOT NULL,
  `last_client_poll` datetime DEFAULT NULL,
  `most_recent_match` datetime NOT NULL,
  `next_check` datetime NOT NULL,
  `res_id` bigint(20) DEFAULT NULL,
  `subscription_status` varchar(20) NOT NULL,
  PRIMARY KEY (`pid`),
  UNIQUE KEY `IDX_SUBS_NEXTCHECK` (`subscription_status`,`next_check`),
  UNIQUE KEY `IDX_SUBS_RESID` (`res_id`),
  CONSTRAINT `FK_SUBSCRIPTION_RESOURCE_ID` FOREIGN KEY (`res_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_subscription`
--

LOCK TABLES `hfj_subscription` WRITE;
/*!40000 ALTER TABLE `hfj_subscription` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_subscription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_subscription_flag_res`
--

DROP TABLE IF EXISTS `hfj_subscription_flag_res`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_subscription_flag_res` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `res_version` bigint(20) NOT NULL,
  `res_id` bigint(20) NOT NULL,
  `subscription_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`pid`),
  KEY `FKoq3ex11wpmkugqbbs64l3772o` (`res_id`),
  KEY `FK_SUBSFLAG_SUBS` (`subscription_id`),
  CONSTRAINT `FK_SUBSFLAG_SUBS` FOREIGN KEY (`subscription_id`) REFERENCES `hfj_subscription` (`pid`),
  CONSTRAINT `FKoq3ex11wpmkugqbbs64l3772o` FOREIGN KEY (`res_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_subscription_flag_res`
--

LOCK TABLES `hfj_subscription_flag_res` WRITE;
/*!40000 ALTER TABLE `hfj_subscription_flag_res` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_subscription_flag_res` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hfj_tag_def`
--

DROP TABLE IF EXISTS `hfj_tag_def`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hfj_tag_def` (
  `tag_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tag_code` varchar(200) DEFAULT NULL,
  `tag_display` varchar(200) DEFAULT NULL,
  `tag_system` varchar(200) DEFAULT NULL,
  `tag_type` int(11) NOT NULL,
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `UKfckaon069c67c5kcte6ytrfoi` (`tag_type`,`tag_system`,`tag_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hfj_tag_def`
--

LOCK TABLES `hfj_tag_def` WRITE;
/*!40000 ALTER TABLE `hfj_tag_def` DISABLE KEYS */;
/*!40000 ALTER TABLE `hfj_tag_def` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trm_codesystem`
--

DROP TABLE IF EXISTS `trm_codesystem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trm_codesystem` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `code_system_uri` varchar(255) NOT NULL,
  `res_id` bigint(20) DEFAULT NULL,
  `current_version_pid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`pid`),
  UNIQUE KEY `IDX_CS_CODESYSTEM` (`code_system_uri`),
  KEY `FK7yug955tdfrg4w4qko0q5h00l` (`current_version_pid`),
  KEY `FKooojm9v7c96qeqh5wxbco1ues` (`res_id`),
  CONSTRAINT `FK7yug955tdfrg4w4qko0q5h00l` FOREIGN KEY (`current_version_pid`) REFERENCES `trm_codesystem_ver` (`pid`),
  CONSTRAINT `FKooojm9v7c96qeqh5wxbco1ues` FOREIGN KEY (`res_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trm_codesystem`
--

LOCK TABLES `trm_codesystem` WRITE;
/*!40000 ALTER TABLE `trm_codesystem` DISABLE KEYS */;
/*!40000 ALTER TABLE `trm_codesystem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trm_codesystem_ver`
--

DROP TABLE IF EXISTS `trm_codesystem_ver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trm_codesystem_ver` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `res_version_id` bigint(20) NOT NULL,
  `res_id` bigint(20) NOT NULL,
  PRIMARY KEY (`pid`),
  UNIQUE KEY `IDX_CSV_RESOURCEPID_AND_VER` (`res_id`,`res_version_id`),
  CONSTRAINT `FK_CODESYSVER_RES_ID` FOREIGN KEY (`res_id`) REFERENCES `hfj_resource` (`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trm_codesystem_ver`
--

LOCK TABLES `trm_codesystem_ver` WRITE;
/*!40000 ALTER TABLE `trm_codesystem_ver` DISABLE KEYS */;
/*!40000 ALTER TABLE `trm_codesystem_ver` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trm_concept`
--

DROP TABLE IF EXISTS `trm_concept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trm_concept` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(100) NOT NULL,
  `codesystem_pid` bigint(20) DEFAULT NULL,
  `display` varchar(400) DEFAULT NULL,
  `index_status` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`pid`),
  UNIQUE KEY `IDX_CONCEPT_CS_CODE` (`codesystem_pid`,`code`),
  KEY `IDX_CONCEPT_INDEXSTATUS` (`index_status`),
  CONSTRAINT `FK_CONCEPT_PID_CS_PID` FOREIGN KEY (`codesystem_pid`) REFERENCES `trm_codesystem_ver` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trm_concept`
--

LOCK TABLES `trm_concept` WRITE;
/*!40000 ALTER TABLE `trm_concept` DISABLE KEYS */;
/*!40000 ALTER TABLE `trm_concept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trm_concept_pc_link`
--

DROP TABLE IF EXISTS `trm_concept_pc_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trm_concept_pc_link` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `rel_type` int(11) DEFAULT NULL,
  `child_pid` bigint(20) NOT NULL,
  `codesystem_pid` bigint(20) NOT NULL,
  `parent_pid` bigint(20) NOT NULL,
  PRIMARY KEY (`pid`),
  KEY `FK_TERM_CONCEPTPC_CHILD` (`child_pid`),
  KEY `FK_TERM_CONCEPTPC_CS` (`codesystem_pid`),
  KEY `FK_TERM_CONCEPTPC_PARENT` (`parent_pid`),
  CONSTRAINT `FK_TERM_CONCEPTPC_CHILD` FOREIGN KEY (`child_pid`) REFERENCES `trm_concept` (`pid`),
  CONSTRAINT `FK_TERM_CONCEPTPC_CS` FOREIGN KEY (`codesystem_pid`) REFERENCES `trm_codesystem_ver` (`pid`),
  CONSTRAINT `FK_TERM_CONCEPTPC_PARENT` FOREIGN KEY (`parent_pid`) REFERENCES `trm_concept` (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trm_concept_pc_link`
--

LOCK TABLES `trm_concept_pc_link` WRITE;
/*!40000 ALTER TABLE `trm_concept_pc_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `trm_concept_pc_link` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-01-12 18:41:28

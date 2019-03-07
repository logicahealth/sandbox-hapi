--
--  * #%L
--  *
--  * %%
--  * Copyright (C) 2014-2019 Healthcare Services Platform Consortium
--  * %%
--  * Licensed under the Apache License, Version 2.0 (the "License");
--  * you may not use this file except in compliance with the License.
--  * You may obtain a copy of the License at
--  *
--  *      http://www.apache.org/licenses/LICENSE-2.0
--  *
--  * Unless required by applicable law or agreed to in writing, software
--  * distributed under the License is distributed on an "AS IS" BASIS,
--  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--  * See the License for the specific language governing permissions and
--  * limitations under the License.
--  * #L%
--

-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: 127.0.0.1    Database: hspc_8_hspc9
-- ------------------------------------------------------
-- Server version	5.7.23

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
-- Table structure for table `HFJ_HISTORY_TAG`
--

DROP TABLE IF EXISTS `HFJ_HISTORY_TAG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_HISTORY_TAG` (
  `PID` bigint(20) NOT NULL,
  `TAG_ID` bigint(20) DEFAULT NULL,
  `RES_ID` bigint(20) NOT NULL,
  `RES_TYPE` varchar(30) NOT NULL,
  `RES_VER_PID` bigint(20) NOT NULL,
  PRIMARY KEY (`PID`),
  UNIQUE KEY `IDX_RESHISTTAG_TAGID` (`RES_VER_PID`,`TAG_ID`),
  KEY `FKtderym7awj6q8iq5c51xv4ndw` (`TAG_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_HISTORY_TAG`
--

LOCK TABLES `HFJ_HISTORY_TAG` WRITE;
/*!40000 ALTER TABLE `HFJ_HISTORY_TAG` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_HISTORY_TAG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_IDX_CMP_STRING_UNIQ`
--

DROP TABLE IF EXISTS `HFJ_IDX_CMP_STRING_UNIQ`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_IDX_CMP_STRING_UNIQ` (
  `PID` bigint(20) NOT NULL,
  `IDX_STRING` varchar(150) NOT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`PID`),
  UNIQUE KEY `IDX_IDXCMPSTRUNIQ_STRING` (`IDX_STRING`),
  KEY `IDX_IDXCMPSTRUNIQ_RESOURCE` (`RES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_IDX_CMP_STRING_UNIQ`
--

LOCK TABLES `HFJ_IDX_CMP_STRING_UNIQ` WRITE;
/*!40000 ALTER TABLE `HFJ_IDX_CMP_STRING_UNIQ` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_IDX_CMP_STRING_UNIQ` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_RESOURCE`
--

DROP TABLE IF EXISTS `HFJ_RESOURCE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_RESOURCE` (
  `RES_ID` bigint(20) NOT NULL,
  `RES_DELETED_AT` datetime DEFAULT NULL,
  `RES_VERSION` varchar(7) DEFAULT NULL,
  `HAS_TAGS` bit(1) NOT NULL,
  `RES_PUBLISHED` datetime NOT NULL,
  `RES_TITLE` varchar(100) DEFAULT NULL,
  `RES_UPDATED` datetime NOT NULL,
  `SP_HAS_LINKS` bit(1) DEFAULT NULL,
  `HASH_SHA256` varchar(64) DEFAULT NULL,
  `SP_INDEX_STATUS` bigint(20) DEFAULT NULL,
  `RES_LANGUAGE` varchar(20) DEFAULT NULL,
  `SP_CMPSTR_UNIQ_PRESENT` bit(1) DEFAULT NULL,
  `SP_COORDS_PRESENT` bit(1) DEFAULT NULL,
  `SP_DATE_PRESENT` bit(1) DEFAULT NULL,
  `SP_NUMBER_PRESENT` bit(1) DEFAULT NULL,
  `SP_QUANTITY_PRESENT` bit(1) DEFAULT NULL,
  `SP_STRING_PRESENT` bit(1) DEFAULT NULL,
  `SP_TOKEN_PRESENT` bit(1) DEFAULT NULL,
  `SP_URI_PRESENT` bit(1) DEFAULT NULL,
  `RES_PROFILE` varchar(200) DEFAULT NULL,
  `RES_TYPE` varchar(30) DEFAULT NULL,
  `RES_VER` bigint(20) DEFAULT NULL,
  `FORCED_ID_PID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`RES_ID`),
  KEY `IDX_RES_DATE` (`RES_UPDATED`),
  KEY `IDX_RES_LANG` (`RES_TYPE`,`RES_LANGUAGE`),
  KEY `IDX_RES_PROFILE` (`RES_PROFILE`),
  KEY `IDX_RES_TYPE` (`RES_TYPE`),
  KEY `IDX_INDEXSTATUS` (`SP_INDEX_STATUS`),
  KEY `FKhjgj8cp879gfxko25cx5o692r` (`FORCED_ID_PID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_RESOURCE`
--

LOCK TABLES `HFJ_RESOURCE` WRITE;
/*!40000 ALTER TABLE `HFJ_RESOURCE` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_RESOURCE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_RES_LINK`
--

DROP TABLE IF EXISTS `HFJ_RES_LINK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_RES_LINK` (
  `PID` bigint(20) NOT NULL,
  `SRC_PATH` varchar(200) NOT NULL,
  `SRC_RESOURCE_ID` bigint(20) NOT NULL,
  `SOURCE_RESOURCE_TYPE` varchar(30) NOT NULL DEFAULT '',
  `TARGET_RESOURCE_ID` bigint(20) DEFAULT NULL,
  `TARGET_RESOURCE_TYPE` varchar(30) NOT NULL DEFAULT '',
  `TARGET_RESOURCE_URL` varchar(200) DEFAULT NULL,
  `SP_UPDATED` datetime DEFAULT NULL,
  PRIMARY KEY (`PID`),
  KEY `IDX_RL_TPATHRES` (`SRC_PATH`,`TARGET_RESOURCE_ID`),
  KEY `IDX_RL_SRC` (`SRC_RESOURCE_ID`),
  KEY `IDX_RL_DEST` (`TARGET_RESOURCE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_RES_LINK`
--

LOCK TABLES `HFJ_RES_LINK` WRITE;
/*!40000 ALTER TABLE `HFJ_RES_LINK` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_RES_LINK` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_RES_REINDEX_JOB`
--

DROP TABLE IF EXISTS `HFJ_RES_REINDEX_JOB`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_RES_REINDEX_JOB` (
  `PID` bigint(20) NOT NULL,
  `JOB_DELETED` bit(1) NOT NULL,
  `RES_TYPE` varchar(255) DEFAULT NULL,
  `SUSPENDED_UNTIL` datetime(6) DEFAULT NULL,
  `UPDATE_THRESHOLD_HIGH` datetime(6) NOT NULL,
  `UPDATE_THRESHOLD_LOW` datetime(6) DEFAULT NULL,
  `REINDEX_COUNT` int(11) DEFAULT NULL,
  PRIMARY KEY (`PID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_RES_REINDEX_JOB`
--

LOCK TABLES `HFJ_RES_REINDEX_JOB` WRITE;
/*!40000 ALTER TABLE `HFJ_RES_REINDEX_JOB` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_RES_REINDEX_JOB` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_RES_TAG`
--

DROP TABLE IF EXISTS `HFJ_RES_TAG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_RES_TAG` (
  `PID` bigint(20) NOT NULL,
  `TAG_ID` bigint(20) DEFAULT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  `RES_TYPE` varchar(30) NOT NULL,
  PRIMARY KEY (`PID`),
  UNIQUE KEY `IDX_RESTAG_TAGID` (`RES_ID`,`TAG_ID`),
  KEY `FKbfcjbaftmiwr3rxkwsy23vneo` (`TAG_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_RES_TAG`
--

LOCK TABLES `HFJ_RES_TAG` WRITE;
/*!40000 ALTER TABLE `HFJ_RES_TAG` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_RES_TAG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_RES_VER`
--

DROP TABLE IF EXISTS `HFJ_RES_VER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_RES_VER` (
  `PID` bigint(20) NOT NULL,
  `RES_DELETED_AT` datetime DEFAULT NULL,
  `RES_ENCODING` varchar(5) DEFAULT NULL,
  `RES_VERSION` varchar(7) DEFAULT NULL,
  `HAS_TAGS` bit(1) NOT NULL,
  `RES_PUBLISHED` datetime NOT NULL,
  `RES_TEXT` longblob,
  `RES_TITLE` varchar(100) DEFAULT NULL,
  `RES_UPDATED` datetime NOT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  `RES_TYPE` varchar(30) NOT NULL,
  `RES_VER` bigint(20) NOT NULL,
  `FORCED_ID_PID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`PID`),
  UNIQUE KEY `IDX_RESVER_ID_VER` (`RES_ID`,`RES_VER`),
  KEY `IDX_RESVER_TYPE_DATE` (`RES_TYPE`,`RES_UPDATED`),
  KEY `IDX_RESVER_ID_DATE` (`RES_ID`,`RES_UPDATED`),
  KEY `IDX_RESVER_DATE` (`RES_UPDATED`),
  KEY `FKh20i7lcbchkaxekvwg9ix4hc5` (`FORCED_ID_PID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_RES_VER`
--

LOCK TABLES `HFJ_RES_VER` WRITE;
/*!40000 ALTER TABLE `HFJ_RES_VER` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_RES_VER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_SEARCH`
--

DROP TABLE IF EXISTS `HFJ_SEARCH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_SEARCH` (
  `PID` bigint(20) NOT NULL,
  `CREATED` datetime NOT NULL,
  `FAILURE_CODE` int(11) DEFAULT NULL,
  `FAILURE_MESSAGE` varchar(500) DEFAULT NULL,
  `LAST_UPDATED_HIGH` datetime DEFAULT NULL,
  `LAST_UPDATED_LOW` datetime DEFAULT NULL,
  `NUM_FOUND` int(11) NOT NULL,
  `PREFERRED_PAGE_SIZE` int(11) DEFAULT NULL,
  `RESOURCE_ID` bigint(20) DEFAULT NULL,
  `RESOURCE_TYPE` varchar(200) DEFAULT NULL,
  `SEARCH_LAST_RETURNED` datetime NOT NULL,
  `SEARCH_QUERY_STRING` longtext,
  `SEARCH_QUERY_STRING_HASH` int(11) DEFAULT NULL,
  `SEARCH_TYPE` int(11) NOT NULL,
  `SEARCH_STATUS` varchar(10) NOT NULL,
  `TOTAL_COUNT` int(11) DEFAULT NULL,
  `SEARCH_UUID` varchar(40) NOT NULL,
  `SEARCH_DELETED` bit(1) DEFAULT NULL,
  `SEARCH_PARAM_MAP` longblob,
  `OPTLOCK_VERSION` int(11) DEFAULT NULL,
  PRIMARY KEY (`PID`),
  UNIQUE KEY `IDX_SEARCH_UUID` (`SEARCH_UUID`),
  KEY `IDX_SEARCH_LASTRETURNED` (`SEARCH_LAST_RETURNED`),
  KEY `IDX_SEARCH_RESTYPE_HASHS` (`RESOURCE_TYPE`,`SEARCH_QUERY_STRING_HASH`,`CREATED`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_SEARCH`
--

LOCK TABLES `HFJ_SEARCH` WRITE;
/*!40000 ALTER TABLE `HFJ_SEARCH` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_SEARCH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_SEARCH_INCLUDE`
--

DROP TABLE IF EXISTS `HFJ_SEARCH_INCLUDE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_SEARCH_INCLUDE` (
  `PID` bigint(20) NOT NULL,
  `SEARCH_INCLUDE` varchar(200) NOT NULL,
  `INC_RECURSE` bit(1) NOT NULL,
  `REVINCLUDE` bit(1) NOT NULL,
  `SEARCH_PID` bigint(20) NOT NULL,
  PRIMARY KEY (`PID`),
  KEY `FK_SEARCHINC_SEARCH` (`SEARCH_PID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_SEARCH_INCLUDE`
--

LOCK TABLES `HFJ_SEARCH_INCLUDE` WRITE;
/*!40000 ALTER TABLE `HFJ_SEARCH_INCLUDE` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_SEARCH_INCLUDE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_SEARCH_PARM`
--

DROP TABLE IF EXISTS `HFJ_SEARCH_PARM`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_SEARCH_PARM` (
  `PID` bigint(20) NOT NULL,
  `PARAM_NAME` varchar(100) NOT NULL,
  `RES_TYPE` varchar(30) NOT NULL,
  PRIMARY KEY (`PID`),
  UNIQUE KEY `IDX_SEARCHPARM_RESTYPE_SPNAME` (`RES_TYPE`,`PARAM_NAME`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_SEARCH_PARM`
--

LOCK TABLES `HFJ_SEARCH_PARM` WRITE;
/*!40000 ALTER TABLE `HFJ_SEARCH_PARM` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_SEARCH_PARM` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_SEARCH_RESULT`
--

DROP TABLE IF EXISTS `HFJ_SEARCH_RESULT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_SEARCH_RESULT` (
  `PID` bigint(20) NOT NULL,
  `SEARCH_ORDER` int(11) NOT NULL,
  `RESOURCE_PID` bigint(20) NOT NULL,
  `SEARCH_PID` bigint(20) NOT NULL,
  PRIMARY KEY (`PID`),
  UNIQUE KEY `IDX_SEARCHRES_ORDER` (`SEARCH_PID`,`SEARCH_ORDER`),
  KEY `FK_SEARCHRES_RES` (`RESOURCE_PID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_SEARCH_RESULT`
--

LOCK TABLES `HFJ_SEARCH_RESULT` WRITE;
/*!40000 ALTER TABLE `HFJ_SEARCH_RESULT` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_SEARCH_RESULT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_SPIDX_COORDS`
--

DROP TABLE IF EXISTS `HFJ_SPIDX_COORDS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_SPIDX_COORDS` (
  `SP_ID` bigint(20) NOT NULL,
  `SP_MISSING` bit(1) DEFAULT NULL,
  `SP_NAME` varchar(100) NOT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  `RES_TYPE` varchar(255) NOT NULL,
  `SP_UPDATED` datetime DEFAULT NULL,
  `SP_LATITUDE` double DEFAULT NULL,
  `SP_LONGITUDE` double DEFAULT NULL,
  `HASH_IDENTITY` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`SP_ID`),
  KEY `IDX_SP_COORDS` (`RES_TYPE`,`SP_NAME`,`SP_LATITUDE`,`SP_LONGITUDE`),
  KEY `IDX_SP_COORDS_UPDATED` (`SP_UPDATED`),
  KEY `IDX_SP_COORDS_RESID` (`RES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_SPIDX_COORDS`
--

LOCK TABLES `HFJ_SPIDX_COORDS` WRITE;
/*!40000 ALTER TABLE `HFJ_SPIDX_COORDS` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_SPIDX_COORDS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_SPIDX_DATE`
--

DROP TABLE IF EXISTS `HFJ_SPIDX_DATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_SPIDX_DATE` (
  `SP_ID` bigint(20) NOT NULL,
  `SP_MISSING` bit(1) DEFAULT NULL,
  `SP_NAME` varchar(100) NOT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  `RES_TYPE` varchar(255) NOT NULL,
  `SP_UPDATED` datetime DEFAULT NULL,
  `SP_VALUE_HIGH` datetime DEFAULT NULL,
  `SP_VALUE_LOW` datetime DEFAULT NULL,
  `HASH_IDENTITY` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`SP_ID`),
  KEY `IDX_SP_DATE` (`RES_TYPE`,`SP_NAME`,`SP_VALUE_LOW`,`SP_VALUE_HIGH`),
  KEY `IDX_SP_DATE_UPDATED` (`SP_UPDATED`),
  KEY `IDX_SP_DATE_RESID` (`RES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_SPIDX_DATE`
--

LOCK TABLES `HFJ_SPIDX_DATE` WRITE;
/*!40000 ALTER TABLE `HFJ_SPIDX_DATE` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_SPIDX_DATE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_SPIDX_NUMBER`
--

DROP TABLE IF EXISTS `HFJ_SPIDX_NUMBER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_SPIDX_NUMBER` (
  `SP_ID` bigint(20) NOT NULL,
  `SP_MISSING` bit(1) DEFAULT NULL,
  `SP_NAME` varchar(100) NOT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  `RES_TYPE` varchar(255) NOT NULL,
  `SP_UPDATED` datetime DEFAULT NULL,
  `SP_VALUE` decimal(19,2) DEFAULT NULL,
  `HASH_IDENTITY` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`SP_ID`),
  KEY `IDX_SP_NUMBER` (`RES_TYPE`,`SP_NAME`,`SP_VALUE`),
  KEY `IDX_SP_NUMBER_UPDATED` (`SP_UPDATED`),
  KEY `IDX_SP_NUMBER_RESID` (`RES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_SPIDX_NUMBER`
--

LOCK TABLES `HFJ_SPIDX_NUMBER` WRITE;
/*!40000 ALTER TABLE `HFJ_SPIDX_NUMBER` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_SPIDX_NUMBER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_SPIDX_QUANTITY`
--

DROP TABLE IF EXISTS `HFJ_SPIDX_QUANTITY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_SPIDX_QUANTITY` (
  `SP_ID` bigint(20) NOT NULL,
  `SP_MISSING` bit(1) DEFAULT NULL,
  `SP_NAME` varchar(100) NOT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  `RES_TYPE` varchar(255) NOT NULL,
  `SP_UPDATED` datetime DEFAULT NULL,
  `SP_SYSTEM` varchar(200) DEFAULT NULL,
  `SP_UNITS` varchar(200) DEFAULT NULL,
  `SP_VALUE` decimal(19,2) DEFAULT NULL,
  `HASH_UNITS_AND_VALPREFIX` bigint(20) DEFAULT NULL,
  `HASH_VALPREFIX` bigint(20) DEFAULT NULL,
  `HASH_IDENTITY` bigint(20) DEFAULT NULL,
  `HASH_IDENTITY_AND_UNITS` bigint(20) DEFAULT NULL,
  `HASH_IDENTITY_SYS_UNITS` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`SP_ID`),
  KEY `IDX_SP_QUANTITY` (`RES_TYPE`,`SP_NAME`,`SP_SYSTEM`,`SP_UNITS`,`SP_VALUE`),
  KEY `IDX_SP_QUANTITY_UPDATED` (`SP_UPDATED`),
  KEY `IDX_SP_QUANTITY_RESID` (`RES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_SPIDX_QUANTITY`
--

LOCK TABLES `HFJ_SPIDX_QUANTITY` WRITE;
/*!40000 ALTER TABLE `HFJ_SPIDX_QUANTITY` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_SPIDX_QUANTITY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_SPIDX_STRING`
--

DROP TABLE IF EXISTS `HFJ_SPIDX_STRING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_SPIDX_STRING` (
  `SP_ID` bigint(20) NOT NULL,
  `SP_MISSING` bit(1) DEFAULT NULL,
  `SP_NAME` varchar(100) NOT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  `RES_TYPE` varchar(255) NOT NULL,
  `SP_UPDATED` datetime DEFAULT NULL,
  `SP_VALUE_EXACT` varchar(200) DEFAULT NULL,
  `SP_VALUE_NORMALIZED` varchar(200) DEFAULT NULL,
  `HASH_EXACT` bigint(20) DEFAULT NULL,
  `HASH_NORM_PREFIX` bigint(20) DEFAULT NULL,
  `HASH_IDENTITY` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`SP_ID`),
  KEY `IDX_SP_STRING` (`RES_TYPE`,`SP_NAME`,`SP_VALUE_NORMALIZED`),
  KEY `IDX_SP_STRING_UPDATED` (`SP_UPDATED`),
  KEY `IDX_SP_STRING_RESID` (`RES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_SPIDX_STRING`
--

LOCK TABLES `HFJ_SPIDX_STRING` WRITE;
/*!40000 ALTER TABLE `HFJ_SPIDX_STRING` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_SPIDX_STRING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_SPIDX_TOKEN`
--

DROP TABLE IF EXISTS `HFJ_SPIDX_TOKEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_SPIDX_TOKEN` (
  `SP_ID` bigint(20) NOT NULL,
  `SP_MISSING` bit(1) DEFAULT NULL,
  `SP_NAME` varchar(100) NOT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  `RES_TYPE` varchar(255) NOT NULL,
  `SP_UPDATED` datetime DEFAULT NULL,
  `SP_SYSTEM` varchar(200) DEFAULT NULL,
  `SP_VALUE` varchar(200) DEFAULT NULL,
  `HASH_SYS` bigint(20) DEFAULT NULL,
  `HASH_SYS_AND_VALUE` bigint(20) DEFAULT NULL,
  `HASH_VALUE` bigint(20) DEFAULT NULL,
  `HASH_IDENTITY` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`SP_ID`),
  KEY `IDX_SP_TOKEN` (`RES_TYPE`,`SP_NAME`,`SP_SYSTEM`,`SP_VALUE`),
  KEY `IDX_SP_TOKEN_UNQUAL` (`RES_TYPE`,`SP_NAME`,`SP_VALUE`),
  KEY `IDX_SP_TOKEN_UPDATED` (`SP_UPDATED`),
  KEY `IDX_SP_TOKEN_RESID` (`RES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_SPIDX_TOKEN`
--

LOCK TABLES `HFJ_SPIDX_TOKEN` WRITE;
/*!40000 ALTER TABLE `HFJ_SPIDX_TOKEN` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_SPIDX_TOKEN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_SPIDX_URI`
--

DROP TABLE IF EXISTS `HFJ_SPIDX_URI`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_SPIDX_URI` (
  `SP_ID` bigint(20) NOT NULL,
  `SP_MISSING` bit(1) DEFAULT NULL,
  `SP_NAME` varchar(100) NOT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  `RES_TYPE` varchar(255) NOT NULL,
  `SP_UPDATED` datetime DEFAULT NULL,
  `SP_URI` varchar(255) DEFAULT NULL,
  `HASH_URI` bigint(20) DEFAULT NULL,
  `HASH_IDENTITY` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`SP_ID`),
  KEY `IDX_SP_URI` (`RES_TYPE`,`SP_NAME`,`SP_URI`),
  KEY `IDX_SP_URI_RESTYPE_NAME` (`RES_TYPE`,`SP_NAME`),
  KEY `IDX_SP_URI_UPDATED` (`SP_UPDATED`),
  KEY `IDX_SP_URI_COORDS` (`RES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_SPIDX_URI`
--

LOCK TABLES `HFJ_SPIDX_URI` WRITE;
/*!40000 ALTER TABLE `HFJ_SPIDX_URI` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_SPIDX_URI` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_SUBSCRIPTION_STATS`
--

DROP TABLE IF EXISTS `HFJ_SUBSCRIPTION_STATS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_SUBSCRIPTION_STATS` (
  `PID` bigint(20) NOT NULL,
  `CREATED_TIME` datetime NOT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`PID`),
  UNIQUE KEY `IDX_SUBSC_RESID` (`RES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_SUBSCRIPTION_STATS`
--

LOCK TABLES `HFJ_SUBSCRIPTION_STATS` WRITE;
/*!40000 ALTER TABLE `HFJ_SUBSCRIPTION_STATS` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_SUBSCRIPTION_STATS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_TAG_DEF`
--

DROP TABLE IF EXISTS `HFJ_TAG_DEF`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_TAG_DEF` (
  `TAG_ID` bigint(20) NOT NULL,
  `TAG_CODE` varchar(200) DEFAULT NULL,
  `TAG_DISPLAY` varchar(200) DEFAULT NULL,
  `myHashCode` int(11) DEFAULT NULL,
  `TAG_SYSTEM` varchar(200) DEFAULT NULL,
  `TAG_TYPE` int(11) NOT NULL,
  PRIMARY KEY (`TAG_ID`),
  UNIQUE KEY `IDX_TAGDEF_TYPESYSCODE` (`TAG_TYPE`,`TAG_SYSTEM`,`TAG_CODE`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_TAG_DEF`
--

LOCK TABLES `HFJ_TAG_DEF` WRITE;
/*!40000 ALTER TABLE `HFJ_TAG_DEF` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_TAG_DEF` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_CNCPT_MAP_GRP_ELM_TGT_PID`
--

DROP TABLE IF EXISTS `SEQ_CNCPT_MAP_GRP_ELM_TGT_PID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_CNCPT_MAP_GRP_ELM_TGT_PID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_CNCPT_MAP_GRP_ELM_TGT_PID`
--

LOCK TABLES `SEQ_CNCPT_MAP_GRP_ELM_TGT_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_CNCPT_MAP_GRP_ELM_TGT_PID` DISABLE KEYS */;
INSERT INTO `SEQ_CNCPT_MAP_GRP_ELM_TGT_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_CNCPT_MAP_GRP_ELM_TGT_PID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_CODESYSTEMVER_PID`
--

DROP TABLE IF EXISTS `SEQ_CODESYSTEMVER_PID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_CODESYSTEMVER_PID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_CODESYSTEMVER_PID`
--

LOCK TABLES `SEQ_CODESYSTEMVER_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_CODESYSTEMVER_PID` DISABLE KEYS */;
INSERT INTO `SEQ_CODESYSTEMVER_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_CODESYSTEMVER_PID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_CODESYSTEM_PID`
--

DROP TABLE IF EXISTS `SEQ_CODESYSTEM_PID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_CODESYSTEM_PID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_CODESYSTEM_PID`
--

LOCK TABLES `SEQ_CODESYSTEM_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_CODESYSTEM_PID` DISABLE KEYS */;
INSERT INTO `SEQ_CODESYSTEM_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_CODESYSTEM_PID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_CONCEPT_DESIG_PID`
--

DROP TABLE IF EXISTS `SEQ_CONCEPT_DESIG_PID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_CONCEPT_DESIG_PID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_CONCEPT_DESIG_PID`
--

LOCK TABLES `SEQ_CONCEPT_DESIG_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_CONCEPT_DESIG_PID` DISABLE KEYS */;
INSERT INTO `SEQ_CONCEPT_DESIG_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_CONCEPT_DESIG_PID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_CONCEPT_MAP_GROUP_PID`
--

DROP TABLE IF EXISTS `SEQ_CONCEPT_MAP_GROUP_PID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_CONCEPT_MAP_GROUP_PID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_CONCEPT_MAP_GROUP_PID`
--

LOCK TABLES `SEQ_CONCEPT_MAP_GROUP_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_CONCEPT_MAP_GROUP_PID` DISABLE KEYS */;
INSERT INTO `SEQ_CONCEPT_MAP_GROUP_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_CONCEPT_MAP_GROUP_PID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_CONCEPT_MAP_GRP_ELM_PID`
--

DROP TABLE IF EXISTS `SEQ_CONCEPT_MAP_GRP_ELM_PID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_CONCEPT_MAP_GRP_ELM_PID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_CONCEPT_MAP_GRP_ELM_PID`
--

LOCK TABLES `SEQ_CONCEPT_MAP_GRP_ELM_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_CONCEPT_MAP_GRP_ELM_PID` DISABLE KEYS */;
INSERT INTO `SEQ_CONCEPT_MAP_GRP_ELM_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_CONCEPT_MAP_GRP_ELM_PID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_CONCEPT_MAP_PID`
--

DROP TABLE IF EXISTS `SEQ_CONCEPT_MAP_PID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_CONCEPT_MAP_PID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_CONCEPT_MAP_PID`
--

LOCK TABLES `SEQ_CONCEPT_MAP_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_CONCEPT_MAP_PID` DISABLE KEYS */;
INSERT INTO `SEQ_CONCEPT_MAP_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_CONCEPT_MAP_PID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_CONCEPT_PC_PID`
--

DROP TABLE IF EXISTS `SEQ_CONCEPT_PC_PID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_CONCEPT_PC_PID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_CONCEPT_PC_PID`
--

LOCK TABLES `SEQ_CONCEPT_PC_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_CONCEPT_PC_PID` DISABLE KEYS */;
INSERT INTO `SEQ_CONCEPT_PC_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_CONCEPT_PC_PID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_CONCEPT_PID`
--

DROP TABLE IF EXISTS `SEQ_CONCEPT_PID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_CONCEPT_PID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_CONCEPT_PID`
--

LOCK TABLES `SEQ_CONCEPT_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_CONCEPT_PID` DISABLE KEYS */;
INSERT INTO `SEQ_CONCEPT_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_CONCEPT_PID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_CONCEPT_PROP_PID`
--

DROP TABLE IF EXISTS `SEQ_CONCEPT_PROP_PID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_CONCEPT_PROP_PID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_CONCEPT_PROP_PID`
--

LOCK TABLES `SEQ_CONCEPT_PROP_PID` WRITE;
/*!40000 ALTER TABLE `SEQ_CONCEPT_PROP_PID` DISABLE KEYS */;
INSERT INTO `SEQ_CONCEPT_PROP_PID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_CONCEPT_PROP_PID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_FORCEDID_ID`
--

DROP TABLE IF EXISTS `SEQ_FORCEDID_ID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_FORCEDID_ID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_FORCEDID_ID`
--

LOCK TABLES `SEQ_FORCEDID_ID` WRITE;
/*!40000 ALTER TABLE `SEQ_FORCEDID_ID` DISABLE KEYS */;
INSERT INTO `SEQ_FORCEDID_ID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_FORCEDID_ID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_HISTORYTAG_ID`
--

DROP TABLE IF EXISTS `SEQ_HISTORYTAG_ID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_HISTORYTAG_ID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_HISTORYTAG_ID`
--

LOCK TABLES `SEQ_HISTORYTAG_ID` WRITE;
/*!40000 ALTER TABLE `SEQ_HISTORYTAG_ID` DISABLE KEYS */;
INSERT INTO `SEQ_HISTORYTAG_ID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_HISTORYTAG_ID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_IDXCMPSTRUNIQ_ID`
--

DROP TABLE IF EXISTS `SEQ_IDXCMPSTRUNIQ_ID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_IDXCMPSTRUNIQ_ID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_IDXCMPSTRUNIQ_ID`
--

LOCK TABLES `SEQ_IDXCMPSTRUNIQ_ID` WRITE;
/*!40000 ALTER TABLE `SEQ_IDXCMPSTRUNIQ_ID` DISABLE KEYS */;
INSERT INTO `SEQ_IDXCMPSTRUNIQ_ID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_IDXCMPSTRUNIQ_ID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_RESLINK_ID`
--

DROP TABLE IF EXISTS `SEQ_RESLINK_ID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_RESLINK_ID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_RESLINK_ID`
--

LOCK TABLES `SEQ_RESLINK_ID` WRITE;
/*!40000 ALTER TABLE `SEQ_RESLINK_ID` DISABLE KEYS */;
INSERT INTO `SEQ_RESLINK_ID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_RESLINK_ID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_RESOURCE_HISTORY_ID`
--

DROP TABLE IF EXISTS `SEQ_RESOURCE_HISTORY_ID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_RESOURCE_HISTORY_ID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_RESOURCE_HISTORY_ID`
--

LOCK TABLES `SEQ_RESOURCE_HISTORY_ID` WRITE;
/*!40000 ALTER TABLE `SEQ_RESOURCE_HISTORY_ID` DISABLE KEYS */;
INSERT INTO `SEQ_RESOURCE_HISTORY_ID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_RESOURCE_HISTORY_ID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_RESOURCE_ID`
--

DROP TABLE IF EXISTS `SEQ_RESOURCE_ID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_RESOURCE_ID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_RESOURCE_ID`
--

LOCK TABLES `SEQ_RESOURCE_ID` WRITE;
/*!40000 ALTER TABLE `SEQ_RESOURCE_ID` DISABLE KEYS */;
INSERT INTO `SEQ_RESOURCE_ID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_RESOURCE_ID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_RESPARMPRESENT_ID`
--

DROP TABLE IF EXISTS `SEQ_RESPARMPRESENT_ID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_RESPARMPRESENT_ID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_RESPARMPRESENT_ID`
--

LOCK TABLES `SEQ_RESPARMPRESENT_ID` WRITE;
/*!40000 ALTER TABLE `SEQ_RESPARMPRESENT_ID` DISABLE KEYS */;
INSERT INTO `SEQ_RESPARMPRESENT_ID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_RESPARMPRESENT_ID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_RESTAG_ID`
--

DROP TABLE IF EXISTS `SEQ_RESTAG_ID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_RESTAG_ID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_RESTAG_ID`
--

LOCK TABLES `SEQ_RESTAG_ID` WRITE;
/*!40000 ALTER TABLE `SEQ_RESTAG_ID` DISABLE KEYS */;
INSERT INTO `SEQ_RESTAG_ID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_RESTAG_ID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_RES_REINDEX_JOB`
--

DROP TABLE IF EXISTS `SEQ_RES_REINDEX_JOB`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_RES_REINDEX_JOB` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_RES_REINDEX_JOB`
--

LOCK TABLES `SEQ_RES_REINDEX_JOB` WRITE;
/*!40000 ALTER TABLE `SEQ_RES_REINDEX_JOB` DISABLE KEYS */;
INSERT INTO `SEQ_RES_REINDEX_JOB` VALUES (1);
/*!40000 ALTER TABLE `SEQ_RES_REINDEX_JOB` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_SEARCH`
--

DROP TABLE IF EXISTS `SEQ_SEARCH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_SEARCH` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_SEARCH`
--

LOCK TABLES `SEQ_SEARCH` WRITE;
/*!40000 ALTER TABLE `SEQ_SEARCH` DISABLE KEYS */;
INSERT INTO `SEQ_SEARCH` VALUES (1);
/*!40000 ALTER TABLE `SEQ_SEARCH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_SEARCHPARM_ID`
--

DROP TABLE IF EXISTS `SEQ_SEARCHPARM_ID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_SEARCHPARM_ID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_SEARCHPARM_ID`
--

LOCK TABLES `SEQ_SEARCHPARM_ID` WRITE;
/*!40000 ALTER TABLE `SEQ_SEARCHPARM_ID` DISABLE KEYS */;
INSERT INTO `SEQ_SEARCHPARM_ID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_SEARCHPARM_ID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_SEARCH_INC`
--

DROP TABLE IF EXISTS `SEQ_SEARCH_INC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_SEARCH_INC` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_SEARCH_INC`
--

LOCK TABLES `SEQ_SEARCH_INC` WRITE;
/*!40000 ALTER TABLE `SEQ_SEARCH_INC` DISABLE KEYS */;
INSERT INTO `SEQ_SEARCH_INC` VALUES (1);
/*!40000 ALTER TABLE `SEQ_SEARCH_INC` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_SEARCH_RES`
--

DROP TABLE IF EXISTS `SEQ_SEARCH_RES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_SEARCH_RES` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_SEARCH_RES`
--

LOCK TABLES `SEQ_SEARCH_RES` WRITE;
/*!40000 ALTER TABLE `SEQ_SEARCH_RES` DISABLE KEYS */;
INSERT INTO `SEQ_SEARCH_RES` VALUES (1);
/*!40000 ALTER TABLE `SEQ_SEARCH_RES` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_SPIDX_COORDS`
--

DROP TABLE IF EXISTS `SEQ_SPIDX_COORDS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_SPIDX_COORDS` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_SPIDX_COORDS`
--

LOCK TABLES `SEQ_SPIDX_COORDS` WRITE;
/*!40000 ALTER TABLE `SEQ_SPIDX_COORDS` DISABLE KEYS */;
INSERT INTO `SEQ_SPIDX_COORDS` VALUES (1);
/*!40000 ALTER TABLE `SEQ_SPIDX_COORDS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_SPIDX_DATE`
--

DROP TABLE IF EXISTS `SEQ_SPIDX_DATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_SPIDX_DATE` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_SPIDX_DATE`
--

LOCK TABLES `SEQ_SPIDX_DATE` WRITE;
/*!40000 ALTER TABLE `SEQ_SPIDX_DATE` DISABLE KEYS */;
INSERT INTO `SEQ_SPIDX_DATE` VALUES (1);
/*!40000 ALTER TABLE `SEQ_SPIDX_DATE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_SPIDX_NUMBER`
--

DROP TABLE IF EXISTS `SEQ_SPIDX_NUMBER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_SPIDX_NUMBER` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_SPIDX_NUMBER`
--

LOCK TABLES `SEQ_SPIDX_NUMBER` WRITE;
/*!40000 ALTER TABLE `SEQ_SPIDX_NUMBER` DISABLE KEYS */;
INSERT INTO `SEQ_SPIDX_NUMBER` VALUES (1);
/*!40000 ALTER TABLE `SEQ_SPIDX_NUMBER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_SPIDX_QUANTITY`
--

DROP TABLE IF EXISTS `SEQ_SPIDX_QUANTITY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_SPIDX_QUANTITY` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_SPIDX_QUANTITY`
--

LOCK TABLES `SEQ_SPIDX_QUANTITY` WRITE;
/*!40000 ALTER TABLE `SEQ_SPIDX_QUANTITY` DISABLE KEYS */;
INSERT INTO `SEQ_SPIDX_QUANTITY` VALUES (1);
/*!40000 ALTER TABLE `SEQ_SPIDX_QUANTITY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_SPIDX_STRING`
--

DROP TABLE IF EXISTS `SEQ_SPIDX_STRING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_SPIDX_STRING` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_SPIDX_STRING`
--

LOCK TABLES `SEQ_SPIDX_STRING` WRITE;
/*!40000 ALTER TABLE `SEQ_SPIDX_STRING` DISABLE KEYS */;
INSERT INTO `SEQ_SPIDX_STRING` VALUES (1);
/*!40000 ALTER TABLE `SEQ_SPIDX_STRING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_SPIDX_TOKEN`
--

DROP TABLE IF EXISTS `SEQ_SPIDX_TOKEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_SPIDX_TOKEN` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_SPIDX_TOKEN`
--

LOCK TABLES `SEQ_SPIDX_TOKEN` WRITE;
/*!40000 ALTER TABLE `SEQ_SPIDX_TOKEN` DISABLE KEYS */;
INSERT INTO `SEQ_SPIDX_TOKEN` VALUES (1);
/*!40000 ALTER TABLE `SEQ_SPIDX_TOKEN` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_SPIDX_URI`
--

DROP TABLE IF EXISTS `SEQ_SPIDX_URI`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_SPIDX_URI` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_SPIDX_URI`
--

LOCK TABLES `SEQ_SPIDX_URI` WRITE;
/*!40000 ALTER TABLE `SEQ_SPIDX_URI` DISABLE KEYS */;
INSERT INTO `SEQ_SPIDX_URI` VALUES (1);
/*!40000 ALTER TABLE `SEQ_SPIDX_URI` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_SUBSCRIPTION_ID`
--

DROP TABLE IF EXISTS `SEQ_SUBSCRIPTION_ID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_SUBSCRIPTION_ID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_SUBSCRIPTION_ID`
--

LOCK TABLES `SEQ_SUBSCRIPTION_ID` WRITE;
/*!40000 ALTER TABLE `SEQ_SUBSCRIPTION_ID` DISABLE KEYS */;
INSERT INTO `SEQ_SUBSCRIPTION_ID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_SUBSCRIPTION_ID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQ_TAGDEF_ID`
--

DROP TABLE IF EXISTS `SEQ_TAGDEF_ID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQ_TAGDEF_ID` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQ_TAGDEF_ID`
--

LOCK TABLES `SEQ_TAGDEF_ID` WRITE;
/*!40000 ALTER TABLE `SEQ_TAGDEF_ID` DISABLE KEYS */;
INSERT INTO `SEQ_TAGDEF_ID` VALUES (1);
/*!40000 ALTER TABLE `SEQ_TAGDEF_ID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRM_CODESYSTEM`
--

DROP TABLE IF EXISTS `TRM_CODESYSTEM`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRM_CODESYSTEM` (
  `PID` bigint(20) NOT NULL,
  `CODE_SYSTEM_URI` varchar(255) NOT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  `CURRENT_VERSION_PID` bigint(20) DEFAULT NULL,
  `CS_NAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`PID`),
  UNIQUE KEY `IDX_CS_CODESYSTEM` (`CODE_SYSTEM_URI`),
  KEY `FK_TRMCODESYSTEM_CURVER` (`CURRENT_VERSION_PID`),
  KEY `FK_TRMCODESYSTEM_RES` (`RES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRM_CODESYSTEM`
--

LOCK TABLES `TRM_CODESYSTEM` WRITE;
/*!40000 ALTER TABLE `TRM_CODESYSTEM` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRM_CODESYSTEM` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRM_CODESYSTEM_VER`
--

DROP TABLE IF EXISTS `TRM_CODESYSTEM_VER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRM_CODESYSTEM_VER` (
  `PID` bigint(20) NOT NULL,
  `RES_ID` bigint(20) NOT NULL,
  `CS_VERSION_ID` varchar(255) DEFAULT NULL,
  `CODESYSTEM_PID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`PID`),
  KEY `FK_CODESYSVER_CS_ID` (`CODESYSTEM_PID`),
  KEY `FK_CODESYSVER_RES_ID` (`RES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRM_CODESYSTEM_VER`
--

LOCK TABLES `TRM_CODESYSTEM_VER` WRITE;
/*!40000 ALTER TABLE `TRM_CODESYSTEM_VER` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRM_CODESYSTEM_VER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRM_CONCEPT`
--

DROP TABLE IF EXISTS `TRM_CONCEPT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRM_CONCEPT` (
  `PID` bigint(20) NOT NULL,
  `CODE` varchar(500) DEFAULT NULL,
  `CODESYSTEM_PID` bigint(20) DEFAULT NULL,
  `DISPLAY` varchar(400) DEFAULT NULL,
  `INDEX_STATUS` bigint(20) DEFAULT NULL,
  `CODE_SEQUENCE` int(11) DEFAULT NULL,
  `PARENT_PIDS` longtext,
  `CONCEPT_UPDATED` datetime DEFAULT NULL,
  PRIMARY KEY (`PID`),
  UNIQUE KEY `IDX_CONCEPT_CS_CODE` (`CODESYSTEM_PID`,`CODE`),
  KEY `IDX_CONCEPT_INDEXSTATUS` (`INDEX_STATUS`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRM_CONCEPT`
--

LOCK TABLES `TRM_CONCEPT` WRITE;
/*!40000 ALTER TABLE `TRM_CONCEPT` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRM_CONCEPT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRM_CONCEPT_DESIG`
--

DROP TABLE IF EXISTS `TRM_CONCEPT_DESIG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRM_CONCEPT_DESIG` (
  `PID` bigint(20) NOT NULL,
  `LANG` varchar(500) DEFAULT NULL,
  `USE_CODE` varchar(500) DEFAULT NULL,
  `USE_DISPLAY` varchar(500) DEFAULT NULL,
  `USE_SYSTEM` varchar(500) DEFAULT NULL,
  `VAL` varchar(500) NOT NULL,
  `CONCEPT_PID` bigint(20) DEFAULT NULL,
  `CS_VER_PID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`PID`),
  KEY `FK_CONCEPTDESIG_CONCEPT` (`CONCEPT_PID`),
  KEY `FK_CONCEPTDESIG_CSV` (`CS_VER_PID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRM_CONCEPT_DESIG`
--

LOCK TABLES `TRM_CONCEPT_DESIG` WRITE;
/*!40000 ALTER TABLE `TRM_CONCEPT_DESIG` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRM_CONCEPT_DESIG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRM_CONCEPT_MAP_GRP_ELEMENT`
--

DROP TABLE IF EXISTS `TRM_CONCEPT_MAP_GRP_ELEMENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRM_CONCEPT_MAP_GRP_ELEMENT` (
  `PID` bigint(20) NOT NULL,
  `SOURCE_CODE` varchar(500) NOT NULL,
  `myConceptMapUrl` varchar(255) DEFAULT NULL,
  `SOURCE_DISPLAY` varchar(400) DEFAULT NULL,
  `mySystem` varchar(255) DEFAULT NULL,
  `mySystemVersion` varchar(255) DEFAULT NULL,
  `myValueSet` varchar(255) DEFAULT NULL,
  `CONCEPT_MAP_GROUP_PID` bigint(20) NOT NULL,
  PRIMARY KEY (`PID`),
  KEY `FK_TCMGELEMENT_GROUP` (`CONCEPT_MAP_GROUP_PID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `TRM_CONCEPT_MAP_GRP_ELM_TGT`
--

DROP TABLE IF EXISTS `TRM_CONCEPT_MAP_GRP_ELM_TGT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRM_CONCEPT_MAP_GRP_ELM_TGT` (
  `PID` bigint(20) NOT NULL,
  `TARGET_CODE` varchar(500) NOT NULL,
  `myConceptMapUrl` varchar(255) DEFAULT NULL,
  `TARGET_DISPLAY` varchar(400) DEFAULT NULL,
  `TARGET_EQUIVALENCE` varchar(50) DEFAULT NULL,
  `mySystem` varchar(255) DEFAULT NULL,
  `mySystemVersion` varchar(255) DEFAULT NULL,
  `myValueSet` varchar(255) DEFAULT NULL,
  `CONCEPT_MAP_GRP_ELM_PID` bigint(20) NOT NULL,
  PRIMARY KEY (`PID`),
  KEY `FK_TCMGETARGET_ELEMENT` (`CONCEPT_MAP_GRP_ELM_PID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRM_CONCEPT_MAP_GRP_ELM_TGT`
--

LOCK TABLES `TRM_CONCEPT_MAP_GRP_ELM_TGT` WRITE;
/*!40000 ALTER TABLE `TRM_CONCEPT_MAP_GRP_ELM_TGT` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRM_CONCEPT_MAP_GRP_ELM_TGT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRM_CONCEPT_PC_LINK`
--

DROP TABLE IF EXISTS `TRM_CONCEPT_PC_LINK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRM_CONCEPT_PC_LINK` (
  `PID` bigint(20) NOT NULL,
  `CHILD_PID` bigint(20) DEFAULT NULL,
  `PARENT_PID` bigint(20) DEFAULT NULL,
  `REL_TYPE` int(11) DEFAULT NULL,
  `CODESYSTEM_PID` bigint(20) NOT NULL,
  PRIMARY KEY (`PID`),
  KEY `FK_TERM_CONCEPTPC_CHILD` (`CHILD_PID`),
  KEY `FK_TERM_CONCEPTPC_CS` (`CODESYSTEM_PID`),
  KEY `FK_TERM_CONCEPTPC_PARENT` (`PARENT_PID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRM_CONCEPT_PC_LINK`
--

LOCK TABLES `TRM_CONCEPT_PC_LINK` WRITE;
/*!40000 ALTER TABLE `TRM_CONCEPT_PC_LINK` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRM_CONCEPT_PC_LINK` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRM_CONCEPT_PROPERTY`
--

DROP TABLE IF EXISTS `TRM_CONCEPT_PROPERTY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRM_CONCEPT_PROPERTY` (
  `PID` bigint(20) NOT NULL,
  `PROP_KEY` varchar(500) NOT NULL,
  `PROP_VAL` varchar(500) DEFAULT NULL,
  `CONCEPT_PID` bigint(20) DEFAULT NULL,
  `PROP_CODESYSTEM` varchar(500) DEFAULT NULL,
  `PROP_DISPLAY` varchar(500) DEFAULT NULL,
  `PROP_TYPE` int(11) NOT NULL,
  `CS_VER_PID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`PID`),
  KEY `FK_CONCEPTPROP_CONCEPT` (`CONCEPT_PID`),
  KEY `FK_CONCEPTPROP_CSV` (`CS_VER_PID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRM_CONCEPT_PROPERTY`
--

LOCK TABLES `TRM_CONCEPT_PROPERTY` WRITE;
/*!40000 ALTER TABLE `TRM_CONCEPT_PROPERTY` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRM_CONCEPT_PROPERTY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_FORCED_ID`
--

DROP TABLE IF EXISTS `HFJ_FORCED_ID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_FORCED_ID` (
  `PID` bigint(20) NOT NULL,
  `FORCED_ID` varchar(100) NOT NULL,
  `RESOURCE_PID` bigint(20) NOT NULL,
  `RESOURCE_TYPE` varchar(100) DEFAULT '',
  PRIMARY KEY (`PID`),
  UNIQUE KEY `IDX_FORCEDID_RESID` (`RESOURCE_PID`),
  KEY `IDX_FORCEDID_TYPE_FID` (`RESOURCE_TYPE`,`FORCED_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_FORCED_ID`
--

LOCK TABLES `HFJ_FORCED_ID` WRITE;
/*!40000 ALTER TABLE `HFJ_FORCED_ID` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_FORCED_ID` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `HFJ_RES_PARAM_PRESENT`
--

DROP TABLE IF EXISTS `HFJ_RES_PARAM_PRESENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HFJ_RES_PARAM_PRESENT` (
  `PID` bigint(20) NOT NULL,
  `SP_PRESENT` bit(1) NOT NULL,
  `RES_ID` bigint(20) NOT NULL,
  `SP_ID` bigint(20) NOT NULL,
  `HASH_PRESENCE` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`PID`),
  KEY `IDX_RESPARMPRESENT_RESID` (`RES_ID`),
  KEY `IDX_RESPARMPRESENT_HASHPRES` (`HASH_PRESENCE`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `HFJ_RES_PARAM_PRESENT`
--

LOCK TABLES `HFJ_RES_PARAM_PRESENT` WRITE;
/*!40000 ALTER TABLE `HFJ_RES_PARAM_PRESENT` DISABLE KEYS */;
/*!40000 ALTER TABLE `HFJ_RES_PARAM_PRESENT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hspc_tenant_info`
--

DROP TABLE IF EXISTS `hspc_tenant_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hspc_tenant_info` (
  `tenant_id` varchar(255) NOT NULL,
  `hspc_schema_version` varchar(10) NOT NULL,
  `allow_open_endpoint` varchar(1) NOT NULL,
  `baseline_date` date DEFAULT NULL,
  `properties` varchar(4096) DEFAULT NULL,
  PRIMARY KEY (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hspc_tenant_info`
--

LOCK TABLES `hspc_tenant_info` WRITE;
/*!40000 ALTER TABLE `hspc_tenant_info` DISABLE KEYS */;
INSERT INTO `hspc_tenant_info` VALUES ('hspc9','8','T',NULL,NULL);
/*!40000 ALTER TABLE `hspc_tenant_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRM_CONCEPT_MAP`
--

DROP TABLE IF EXISTS `TRM_CONCEPT_MAP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRM_CONCEPT_MAP` (
  `PID` bigint(20) NOT NULL,
  `RES_ID` bigint(20) DEFAULT NULL,
  `SOURCE_URL` varchar(200) DEFAULT NULL,
  `TARGET_URL` varchar(200) DEFAULT NULL,
  `URL` varchar(200) NOT NULL,
  PRIMARY KEY (`PID`),
  UNIQUE KEY `IDX_CONCEPT_MAP_URL` (`URL`),
  KEY `FK_TRMCONCEPTMAP_RES` (`RES_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRM_CONCEPT_MAP`
--

LOCK TABLES `TRM_CONCEPT_MAP` WRITE;
/*!40000 ALTER TABLE `TRM_CONCEPT_MAP` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRM_CONCEPT_MAP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TRM_CONCEPT_MAP_GROUP`
--

DROP TABLE IF EXISTS `TRM_CONCEPT_MAP_GROUP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TRM_CONCEPT_MAP_GROUP` (
  `PID` bigint(20) NOT NULL,
  `myConceptMapUrl` varchar(255) DEFAULT NULL,
  `SOURCE_URL` varchar(200) NOT NULL,
  `mySourceValueSet` varchar(255) DEFAULT NULL,
  `SOURCE_VERSION` varchar(100) DEFAULT NULL,
  `TARGET_URL` varchar(200) NOT NULL,
  `myTargetValueSet` varchar(255) DEFAULT NULL,
  `TARGET_VERSION` varchar(100) DEFAULT NULL,
  `CONCEPT_MAP_PID` bigint(20) NOT NULL,
  PRIMARY KEY (`PID`),
  KEY `FK_TCMGROUP_CONCEPTMAP` (`CONCEPT_MAP_PID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TRM_CONCEPT_MAP_GROUP`
--

LOCK TABLES `TRM_CONCEPT_MAP_GROUP` WRITE;
/*!40000 ALTER TABLE `TRM_CONCEPT_MAP_GROUP` DISABLE KEYS */;
/*!40000 ALTER TABLE `TRM_CONCEPT_MAP_GROUP` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-02-25 16:28:44

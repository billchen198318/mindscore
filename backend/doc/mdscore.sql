/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-12.2.2-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: mdscore
-- ------------------------------------------------------
-- Server version	12.2.2-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `md_action_item`
--

DROP TABLE IF EXISTS `md_action_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_action_item` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `PLAN_OID` char(36) NOT NULL COMMENT 'Action Plan OID',
  `PARENT_OID` char(36) DEFAULT NULL COMMENT '上層 Action Item OID',
  `ITEM_NAME` varchar(300) NOT NULL COMMENT 'Action Item 名稱',
  `ACTION_STAGE` varchar(32) NOT NULL DEFAULT 'DO' COMMENT '階段 PLAN/DO/CHECK/ACT',
  `DESCRIPTION` varchar(2000) DEFAULT NULL COMMENT '說明',
  `START_DATE` date DEFAULT NULL COMMENT '開始日期',
  `END_DATE` date DEFAULT NULL COMMENT '結束日期',
  `DONE_DATE` date DEFAULT NULL COMMENT '完成日期',
  `PROGRESS_VALUE` decimal(10,4) NOT NULL DEFAULT 0.0000 COMMENT '進度百分比',
  `STATUS` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '狀態',
  `SORT_NO` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  KEY `IDX_MD_ACTION_ITEM_PLAN` (`PLAN_OID`),
  KEY `IDX_MD_ACTION_ITEM_PARENT` (`PARENT_OID`),
  KEY `IDX_MD_ACTION_ITEM_STATUS` (`STATUS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore Action Item';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_action_item`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_action_item` WRITE;
/*!40000 ALTER TABLE `md_action_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_action_item` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_action_owner`
--

DROP TABLE IF EXISTS `md_action_owner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_action_owner` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `ACTION_TYPE` varchar(32) NOT NULL COMMENT 'Action 類型 PLAN/ITEM',
  `ACTION_OID` char(36) NOT NULL COMMENT 'Action Plan 或 Action Item OID',
  `OWNER_TYPE` varchar(32) NOT NULL COMMENT 'owner 類型 ACCOUNT/ORG',
  `ACCOUNT` varchar(24) DEFAULT NULL COMMENT 'qifu4 帳號',
  `ORG_OID` char(36) DEFAULT NULL COMMENT '組織 OID',
  `OWNER_ROLE` varchar(32) NOT NULL DEFAULT 'OWNER' COMMENT 'owner 角色 OWNER/VIEWER/APPROVER',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  KEY `IDX_MD_ACTION_OWNER_ACTION` (`ACTION_TYPE`,`ACTION_OID`),
  KEY `IDX_MD_ACTION_OWNER_ACCOUNT` (`ACCOUNT`),
  KEY `IDX_MD_ACTION_OWNER_ORG` (`ORG_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore Action owner';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_action_owner`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_action_owner` WRITE;
/*!40000 ALTER TABLE `md_action_owner` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_action_owner` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_action_plan`
--

DROP TABLE IF EXISTS `md_action_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_action_plan` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `PLAN_CODE` varchar(64) NOT NULL COMMENT 'Action Plan 代碼',
  `PLAN_NAME` varchar(300) NOT NULL COMMENT 'Action Plan 名稱',
  `DESCRIPTION` varchar(2000) DEFAULT NULL COMMENT '說明',
  `START_DATE` date DEFAULT NULL COMMENT '開始日期',
  `END_DATE` date DEFAULT NULL COMMENT '結束日期',
  `PROGRESS_VALUE` decimal(10,4) NOT NULL DEFAULT 0.0000 COMMENT '進度百分比',
  `STATUS` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '狀態',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_ACTION_PLAN_CODE` (`PLAN_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore Action Plan';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_action_plan`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_action_plan` WRITE;
/*!40000 ALTER TABLE `md_action_plan` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_action_plan` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_action_source_link`
--

DROP TABLE IF EXISTS `md_action_source_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_action_source_link` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `ACTION_TYPE` varchar(32) NOT NULL COMMENT 'Action 類型 PLAN/ITEM',
  `ACTION_OID` char(36) NOT NULL COMMENT 'Action OID',
  `SOURCE_TYPE` varchar(32) NOT NULL COMMENT '來源類型 KPI/OKR_OBJECTIVE/OKR_KR/STRATEGY/INSIGHT',
  `SOURCE_OID` char(36) NOT NULL COMMENT '來源 OID',
  `LINK_REASON` varchar(1000) DEFAULT NULL COMMENT '關聯原因',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_ACTION_SOURCE_LINK` (`ACTION_TYPE`,`ACTION_OID`,`SOURCE_TYPE`,`SOURCE_OID`),
  KEY `IDX_MD_ACTION_SOURCE_ACTION` (`ACTION_TYPE`,`ACTION_OID`),
  KEY `IDX_MD_ACTION_SOURCE_REF` (`SOURCE_TYPE`,`SOURCE_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore Action 來源關聯';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_action_source_link`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_action_source_link` WRITE;
/*!40000 ALTER TABLE `md_action_source_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_action_source_link` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_aggregation_method`
--

DROP TABLE IF EXISTS `md_aggregation_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_aggregation_method` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `AGGR_CODE` varchar(64) NOT NULL COMMENT '彙總方法代碼',
  `AGGR_NAME` varchar(200) NOT NULL COMMENT '彙總方法名稱',
  `AGGR_TYPE` varchar(32) NOT NULL DEFAULT 'BUILTIN' COMMENT '彙總類型 BUILTIN/CUSTOM/SCRIPT',
  `EXPRESSION` mediumtext DEFAULT NULL COMMENT '彙總公式或腳本',
  `DESCRIPTION` varchar(2000) DEFAULT NULL COMMENT '彙總方法中文說明',
  `ENABLED` varchar(1) NOT NULL DEFAULT 'Y' COMMENT '是否啟用 Y/N',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_AGGR_CODE` (`AGGR_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore KPI 彙總方法';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_aggregation_method`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_aggregation_method` WRITE;
/*!40000 ALTER TABLE `md_aggregation_method` DISABLE KEYS */;
INSERT INTO `md_aggregation_method` VALUES
('038ba957-6980-11f1-a592-005056c00001','SUM','Sum','BUILTIN',NULL,'Sum all calculated score values.','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038bb513-6980-11f1-a592-005056c00001','AVG','Average','BUILTIN',NULL,'Average all calculated score values.','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038bbb0e-6980-11f1-a592-005056c00001','MAX','Maximum','BUILTIN',NULL,'Use maximum calculated score value.','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038bc0fb-6980-11f1-a592-005056c00001','MIN','Minimum','BUILTIN',NULL,'Use minimum calculated score value.','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038bc909-6980-11f1-a592-005056c00001','CNT','Count','BUILTIN',NULL,'Count calculated score values.','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038bcf2c-6980-11f1-a592-005056c00001','DISTINCT','Distinct Count','BUILTIN',NULL,'Count distinct calculated score values.','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038bd54a-6980-11f1-a592-005056c00001','LATEST_ACTUAL','Latest Actual','BUILTIN',NULL,'Use latest measure actual value.','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038be3b8-6980-11f1-a592-005056c00001','FIRST_ACTUAL','First Actual','BUILTIN',NULL,'Use first measure actual value.','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038bf1d6-6980-11f1-a592-005056c00001','NON_NULL_CNT','Non-null Count','BUILTIN',NULL,'Count measure rows with actual value.','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038bf828-6980-11f1-a592-005056c00001','VALID_RATE','Valid Rate','BUILTIN',NULL,'Percentage of measure rows with actual value.','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038bff6f-6980-11f1-a592-005056c00001','ACHIEVEMENT_RATE','Achievement Rate','BUILTIN',NULL,'Latest actual divided by target as percentage.','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038c05ba-6980-11f1-a592-005056c00001','PASS_RATE','Pass Rate','BUILTIN',NULL,'Percentage of measure rows that pass KPI compare rule.','Y','system','2026-06-16 20:36:22',NULL,NULL);
/*!40000 ALTER TABLE `md_aggregation_method` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_formula`
--

DROP TABLE IF EXISTS `md_formula`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_formula` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `FORMULA_CODE` varchar(64) NOT NULL COMMENT '公式代碼',
  `FORMULA_NAME` varchar(200) NOT NULL COMMENT '公式名稱',
  `FORMULA_TYPE` varchar(32) NOT NULL DEFAULT 'BUILTIN' COMMENT '公式類型 BUILTIN/CUSTOM/SCRIPT',
  `SCRIPT_TYPE` varchar(32) NOT NULL DEFAULT 'JAVA' COMMENT '腳本類型 JAVA/GROOVY/JS/EXPR',
  `EXPRESSION` mediumtext DEFAULT NULL COMMENT '公式內容或運算式',
  `RETURN_TYPE` varchar(32) NOT NULL DEFAULT 'DECIMAL' COMMENT '回傳型別',
  `VERSION_NO` int(11) NOT NULL DEFAULT 1 COMMENT '公式版本',
  `IS_SYSTEM` varchar(1) NOT NULL DEFAULT 'N' COMMENT '是否系統內建 Y/N',
  `IS_RECOMMENDABLE` varchar(1) NOT NULL DEFAULT 'Y' COMMENT '是否可被系統推薦 Y/N',
  `DESCRIPTION` varchar(2000) DEFAULT NULL COMMENT '公式中文說明',
  `EXAMPLE_TEXT` varchar(2000) DEFAULT NULL COMMENT '公式範例說明',
  `ENABLED` varchar(1) NOT NULL DEFAULT 'Y' COMMENT '是否啟用 Y/N',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_FORMULA_CODE_VER` (`FORMULA_CODE`,`VERSION_NO`),
  KEY `IDX_MD_FORMULA_RECOMMEND` (`IS_RECOMMENDABLE`,`ENABLED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore KPI 計算公式';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_formula`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_formula` WRITE;
/*!40000 ALTER TABLE `md_formula` DISABLE KEYS */;
INSERT INTO `md_formula` VALUES
('038af272-6980-11f1-a592-005056c00001','BIGGER_IS_BETTER_LINEAR','Bigger Is Better Linear','BUILTIN','GROOVY','$P{actual} == null || $P{target} == null || $P{target} == 0 ? 0 : (($P{actual} * 100 / $P{target}) > 100 ? 100 : ($P{actual} * 100 / $P{target}))','DECIMAL',1,'Y','Y','For KPIs where larger actual value is better. Score is actual / target * 100, capped at 100.','actual=80,target=100 => 80; actual=120,target=100 => 100','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038b05c5-6980-11f1-a592-005056c00001','SMALLER_IS_BETTER_LINEAR','Smaller Is Better Linear','BUILTIN','GROOVY','$P{actual} == null || $P{target} == null ? 0 : ($P{actual} <= $P{target} ? 100 : ($P{actual} == 0 ? 0 : ($P{target} * 100 / $P{actual})))','DECIMAL',1,'Y','Y','For KPIs where smaller actual value is better. Values at or below target score 100; values above target decay by target / actual.','actual=8,target=10 => 100; actual=20,target=10 => 50','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038b0fea-6980-11f1-a592-005056c00001','RANGE_IN_BOUNDS','Range In Bounds','BUILTIN','GROOVY','$P{actual} == null ? 0 : ($P{kpi.min} != null && $P{actual} < $P{kpi.min} ? ($P{kpi.min} == 0 ? 0 : ($P{actual} * 100 / $P{kpi.min})) : ($P{kpi.max} != null && $P{actual} > $P{kpi.max} ? ($P{actual} == 0 ? 0 : ($P{kpi.max} * 100 / $P{actual})) : 100))','DECIMAL',1,'Y','Y','For KPIs that pass when actual value stays inside min and max. Outside the range, score decays linearly.','min=90,max=110,actual=100 => 100; actual=120 => 91.6667','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038b17bf-6980-11f1-a592-005056c00001','QUASI_IS_BETTER_TARGET','Quasi Is Better Target','BUILTIN','GROOVY','$P{actual} == null || $P{target} == null || $P{target} == 0 ? 0 : (((100 - (($P{actual} - $P{target}).abs() * 100 / $P{target}.abs())) < 0) ? 0 : (100 - (($P{actual} - $P{target}).abs() * 100 / $P{target}.abs())))','DECIMAL',1,'Y','Y','For KPIs where closest to target is best. Score decreases by percentage distance from target.','actual=95,target=100 => 95; actual=110,target=100 => 90','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038b2039-6980-11f1-a592-005056c00001','MANUAL_SCORE','Manual Score','BUILTIN','GROOVY','$P{actual} == null ? 0 : ($P{actual} > 100 ? 100 : ($P{actual} < 0 ? 0 : $P{actual}))','DECIMAL',1,'Y','Y','Use actual value as already-scored manual input, capped to 0-100.','actual=85 => 85','Y','system','2026-06-16 20:36:22',NULL,NULL),
('038b26bf-6980-11f1-a592-005056c00001','BOOLEAN_PASS_FAIL','Boolean Pass Fail','BUILTIN','GROOVY','$P{actual} == null ? 0 : ($P{actual} > 0 ? 100 : 0)','DECIMAL',1,'Y','Y','For yes/no KPIs. Non-zero actual means pass and scores 100; zero scores 0.','actual=1 => 100; actual=0 => 0','Y','system','2026-06-16 20:36:22',NULL,NULL);
/*!40000 ALTER TABLE `md_formula` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_formula_recommend_rule`
--

DROP TABLE IF EXISTS `md_formula_recommend_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_formula_recommend_rule` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `RULE_CODE` varchar(64) NOT NULL COMMENT '推薦規則代碼',
  `RULE_NAME` varchar(200) NOT NULL COMMENT '推薦規則名稱',
  `MANAGEMENT_MODE` varchar(32) NOT NULL COMMENT '管理模式 BIGGER/SMALLER/QUASI/MANUAL',
  `COMPARE_MODE` varchar(32) DEFAULT NULL COMMENT '比較模式 TARGET/MINIMUM/MAXIMUM/RANGE/CUSTOM',
  `PERIOD_TYPE` varchar(32) DEFAULT NULL COMMENT '適用週期類型，空字串代表不限',
  `DATA_TYPE` varchar(32) DEFAULT NULL COMMENT '資料類型 NUMBER/PERCENT/CURRENCY/BOOLEAN/MANUAL，空字串代表不限',
  `RECOMMENDED_FORMULA_OID` char(36) NOT NULL COMMENT '推薦公式 OID',
  `PRIORITY_NO` int(11) NOT NULL DEFAULT 100 COMMENT '推薦優先順序，數字越小越優先',
  `IS_DEFAULT` varchar(1) NOT NULL DEFAULT 'N' COMMENT '是否預設規則 Y/N',
  `ENABLED` varchar(1) NOT NULL DEFAULT 'Y' COMMENT '是否啟用 Y/N',
  `DESCRIPTION` varchar(2000) DEFAULT NULL COMMENT '推薦原因中文說明',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_FORMULA_RECOMM_RULE` (`RULE_CODE`),
  KEY `IDX_MD_FORMULA_RECOMM_MATCH` (`MANAGEMENT_MODE`,`COMPARE_MODE`,`DATA_TYPE`,`ENABLED`),
  KEY `IDX_MD_FORMULA_RECOMM_FORMULA` (`RECOMMENDED_FORMULA_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore 公式推薦規則';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_formula_recommend_rule`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_formula_recommend_rule` WRITE;
/*!40000 ALTER TABLE `md_formula_recommend_rule` DISABLE KEYS */;
INSERT INTO `md_formula_recommend_rule` VALUES
('038c5d51-6980-11f1-a592-005056c00001','REC_BIGGER_TARGET','Bigger Target Formula','BIGGER','TARGET','','','038af272-6980-11f1-a592-005056c00001',100,'N','Y','Use bigger-is-better scoring for target-based KPI.','system','2026-06-16 20:36:22',NULL,NULL),
('038c67e0-6980-11f1-a592-005056c00001','REC_BIGGER_MINIMUM','Bigger Minimum Formula','BIGGER','MINIMUM','','','038af272-6980-11f1-a592-005056c00001',100,'N','Y','Use bigger-is-better scoring for minimum-threshold KPI.','system','2026-06-16 20:36:22',NULL,NULL),
('038c718c-6980-11f1-a592-005056c00001','REC_SMALLER_TARGET','Smaller Target Formula','SMALLER','TARGET','','','038b05c5-6980-11f1-a592-005056c00001',100,'N','Y','Use smaller-is-better scoring for target-based KPI.','system','2026-06-16 20:36:22',NULL,NULL),
('038c7f16-6980-11f1-a592-005056c00001','REC_SMALLER_MAXIMUM','Smaller Maximum Formula','SMALLER','MAXIMUM','','','038b05c5-6980-11f1-a592-005056c00001',100,'N','Y','Use smaller-is-better scoring for maximum-threshold KPI.','system','2026-06-16 20:36:22',NULL,NULL),
('038c8d60-6980-11f1-a592-005056c00001','REC_QUASI_TARGET','Quasi Target Formula','QUASI','TARGET','','','038b17bf-6980-11f1-a592-005056c00001',100,'N','Y','Use closest-to-target scoring for quasi KPI.','system','2026-06-16 20:36:22',NULL,NULL),
('038c940a-6980-11f1-a592-005056c00001','REC_QUASI_RANGE','Quasi Range Formula','QUASI','RANGE','','','038b0fea-6980-11f1-a592-005056c00001',100,'N','Y','Use in-range scoring for quasi range KPI.','system','2026-06-16 20:36:22',NULL,NULL),
('038c9d7d-6980-11f1-a592-005056c00001','REC_MANUAL_CUSTOM','Manual Custom Formula','MANUAL','CUSTOM','','','038b2039-6980-11f1-a592-005056c00001',100,'N','Y','Use actual value as manual score.','system','2026-06-16 20:36:22',NULL,NULL),
('038ca80b-6980-11f1-a592-005056c00001','REC_MANUAL_TARGET','Manual Target Formula','MANUAL','TARGET','','','038b2039-6980-11f1-a592-005056c00001',100,'N','Y','Use actual value as manual score.','system','2026-06-16 20:36:22',NULL,NULL),
('038cae6c-6980-11f1-a592-005056c00001','REC_BOOLEAN_BIGGER_TARGET','Boolean Bigger Target Formula','BIGGER','TARGET','','BOOLEAN','038b26bf-6980-11f1-a592-005056c00001',110,'N','Y','Use pass/fail scoring for boolean KPI.','system','2026-06-16 20:36:22',NULL,NULL),
('038cb47f-6980-11f1-a592-005056c00001','REC_BOOLEAN_MANUAL_CUSTOM','Boolean Manual Custom Formula','MANUAL','CUSTOM','','BOOLEAN','038b26bf-6980-11f1-a592-005056c00001',110,'N','Y','Use pass/fail scoring for boolean KPI.','system','2026-06-16 20:36:22',NULL,NULL);
/*!40000 ALTER TABLE `md_formula_recommend_rule` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_kpi`
--

DROP TABLE IF EXISTS `md_kpi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_kpi` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `KPI_CODE` varchar(64) NOT NULL COMMENT 'KPI 代碼',
  `KPI_NAME` varchar(200) NOT NULL COMMENT 'KPI 名稱',
  `DESCRIPTION` varchar(2000) DEFAULT NULL COMMENT 'KPI 說明',
  `UNIT_NAME` varchar(50) DEFAULT NULL COMMENT '單位，例如 %, 元, 件, 天',
  `DATA_TYPE` varchar(32) NOT NULL DEFAULT 'NUMBER' COMMENT '資料類型 NUMBER/PERCENT/CURRENCY/BOOLEAN/MANUAL',
  `PERIOD_TYPE` varchar(32) NOT NULL DEFAULT 'ALL' COMMENT '允許輸入週期 ALL/DAY/WEEK/MONTH/QUARTER/HALFYEAR/YEAR；ALL 代表量測時可選任一實際週期',
  `MANAGEMENT_MODE` varchar(32) NOT NULL COMMENT '管理模式 BIGGER/SMALLER/QUASI/MANUAL',
  `COMPARE_MODE` varchar(32) NOT NULL DEFAULT 'TARGET' COMMENT '比較模式 TARGET/MINIMUM/MAXIMUM/RANGE/CUSTOM',
  `MIN_VALUE` decimal(24,6) DEFAULT NULL COMMENT '最低值或下限門檻',
  `TARGET_VALUE` decimal(24,6) DEFAULT NULL COMMENT '目標值',
  `MAX_VALUE` decimal(24,6) DEFAULT NULL COMMENT '最高值或上限門檻',
  `QUASI_RANGE` decimal(10,4) NOT NULL DEFAULT 0.0000 COMMENT '準目標容忍範圍百分比，例如 5 代表正負 5%',
  `SCORE_CAP_MODE` varchar(32) NOT NULL DEFAULT 'CAP_100' COMMENT '分數封頂方式 CAP_100/ALLOW_OVER_100/CUSTOM',
  `SCORING_POLICY` varchar(64) DEFAULT NULL COMMENT '內建計分策略代碼',
  `FORMULA_OID` char(36) NOT NULL COMMENT '實際使用公式 OID',
  `RECOMMENDED_FORMULA_OID` char(36) DEFAULT NULL COMMENT '系統推薦公式 OID',
  `FORMULA_SELECTION_MODE` varchar(32) NOT NULL DEFAULT 'AUTO' COMMENT '公式選取方式 AUTO/MANUAL_OVERRIDE/CUSTOM',
  `AGGR_METHOD_OID` char(36) NOT NULL COMMENT '彙總方法 OID',
  `FORMULA_VERSION_NO` int(11) NOT NULL DEFAULT 1 COMMENT '使用公式版本',
  `WEIGHT_VALUE` decimal(10,4) NOT NULL DEFAULT 0.0000 COMMENT '預設權重',
  `ENABLED` varchar(1) NOT NULL DEFAULT 'Y' COMMENT '是否啟用 Y/N',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_KPI_CODE` (`KPI_CODE`),
  KEY `IDX_MD_KPI_FORMULA` (`FORMULA_OID`),
  KEY `IDX_MD_KPI_RECOMM_FORMULA` (`RECOMMENDED_FORMULA_OID`),
  KEY `IDX_MD_KPI_AGGR` (`AGGR_METHOD_OID`),
  KEY `IDX_MD_KPI_MODE` (`MANAGEMENT_MODE`,`COMPARE_MODE`,`ENABLED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore KPI 主檔';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_kpi`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_kpi` WRITE;
/*!40000 ALTER TABLE `md_kpi` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_kpi` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_kpi_measure_data`
--

DROP TABLE IF EXISTS `md_kpi_measure_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_kpi_measure_data` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `KPI_OID` char(36) NOT NULL COMMENT 'KPI OID',
  `PERIOD_TYPE` varchar(32) NOT NULL COMMENT '週期類型',
  `PERIOD_KEY` varchar(32) NOT NULL COMMENT '週期鍵，例如 2026-06 或 2026-Q2',
  `MEASURE_DATE` date DEFAULT NULL COMMENT '實際量測日期',
  `TARGET_VALUE` decimal(24,6) DEFAULT NULL COMMENT '本期目標值',
  `ACTUAL_VALUE` decimal(24,6) DEFAULT NULL COMMENT '本期實際值',
  `MIN_VALUE` decimal(24,6) DEFAULT NULL COMMENT '本期最低門檻，可覆寫 KPI 主檔',
  `MAX_VALUE` decimal(24,6) DEFAULT NULL COMMENT '本期最高門檻，可覆寫 KPI 主檔',
  `DATA_FOR_TYPE` varchar(32) NOT NULL DEFAULT 'GLOBAL' COMMENT '資料歸屬 GLOBAL/ACCOUNT/ORG',
  `ACCOUNT` varchar(24) DEFAULT NULL COMMENT '資料歸屬帳號',
  `ORG_OID` char(36) DEFAULT NULL COMMENT '資料歸屬組織',
  `SOURCE_TYPE` varchar(64) DEFAULT 'MANUAL' COMMENT '資料來源 MANUAL/API/CONNECTOR/IMPORT',
  `SOURCE_REF` varchar(200) DEFAULT NULL COMMENT '來源參照，例如 connector job id',
  `EVIDENCE_TEXT` varchar(2000) DEFAULT NULL COMMENT '資料證據或備註',
  `LOCKED` varchar(1) NOT NULL DEFAULT 'N' COMMENT '是否鎖定 Y/N',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_KPI_MEASURE_DATA` (`KPI_OID`,`PERIOD_TYPE`,`PERIOD_KEY`,`DATA_FOR_TYPE`,`ACCOUNT`,`ORG_OID`),
  KEY `IDX_MD_KPI_MEASURE_KPI` (`KPI_OID`),
  KEY `IDX_MD_KPI_MEASURE_PERIOD` (`PERIOD_TYPE`,`PERIOD_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore KPI 量測資料';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_kpi_measure_data`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_kpi_measure_data` WRITE;
/*!40000 ALTER TABLE `md_kpi_measure_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_kpi_measure_data` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_kpi_owner`
--

DROP TABLE IF EXISTS `md_kpi_owner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_kpi_owner` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `KPI_OID` char(36) NOT NULL COMMENT 'KPI OID',
  `OWNER_TYPE` varchar(32) NOT NULL COMMENT 'owner 類型 ACCOUNT/ORG',
  `ACCOUNT` varchar(24) DEFAULT NULL COMMENT 'qifu4 帳號，OWNER_TYPE=ACCOUNT 時使用',
  `ORG_OID` char(36) DEFAULT NULL COMMENT '組織 OID，OWNER_TYPE=ORG 時使用',
  `OWNER_ROLE` varchar(32) NOT NULL DEFAULT 'OWNER' COMMENT 'owner 角色 OWNER/VIEWER/APPROVER',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  KEY `IDX_MD_KPI_OWNER_KPI` (`KPI_OID`),
  KEY `IDX_MD_KPI_OWNER_ACCOUNT` (`ACCOUNT`),
  KEY `IDX_MD_KPI_OWNER_ORG` (`ORG_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore KPI owner';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_kpi_owner`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_kpi_owner` WRITE;
/*!40000 ALTER TABLE `md_kpi_owner` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_kpi_owner` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_kpi_score_color`
--

DROP TABLE IF EXISTS `md_kpi_score_color`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_kpi_score_color` (
  `OID` char(36) NOT NULL COMMENT 'Color rule OID',
  `SCOPE_TYPE` varchar(32) NOT NULL DEFAULT 'GLOBAL' COMMENT 'Color scope GLOBAL/KPI',
  `SCOPE_KEY` varchar(64) NOT NULL DEFAULT 'GLOBAL' COMMENT 'GLOBAL or KPI OID, used for unique rule key',
  `KPI_OID` char(36) DEFAULT NULL COMMENT 'KPI OID, required when SCOPE_TYPE=KPI',
  `COLOR_TYPE` varchar(32) NOT NULL DEFAULT 'CUSTOM' COMMENT 'Color type CUSTOM/DEFAULT',
  `COLOR_CODE` varchar(64) NOT NULL COMMENT 'Color rule code',
  `COLOR_NAME` varchar(100) NOT NULL COMMENT 'Color rule name',
  `SCORE_MIN` decimal(10,4) DEFAULT NULL COMMENT 'Score range start, used for CUSTOM color',
  `SCORE_MAX` decimal(10,4) DEFAULT NULL COMMENT 'Score range end, used for CUSTOM color',
  `SCORE_STATUS` varchar(32) NOT NULL DEFAULT 'UNKNOWN' COMMENT 'Score status GOOD/WARNING/BAD/UNKNOWN',
  `FONT_COLOR` varchar(32) NOT NULL COMMENT 'Font color, for example #FFFFFF',
  `BG_COLOR` varchar(32) NOT NULL COMMENT 'Background color, for example #198754',
  `SORT_NO` int(11) NOT NULL DEFAULT 0 COMMENT 'Sort number',
  `ENABLED` varchar(1) NOT NULL DEFAULT 'Y' COMMENT 'Enabled Y/N',
  `DESCRIPTION` varchar(1000) DEFAULT NULL COMMENT 'Description',
  `CUSERID` varchar(24) NOT NULL COMMENT 'Create user',
  `CDATE` datetime NOT NULL COMMENT 'Create date',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT 'Update user',
  `UDATE` datetime DEFAULT NULL COMMENT 'Update date',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_KPI_SCORE_COLOR_CODE` (`SCOPE_TYPE`,`SCOPE_KEY`,`COLOR_CODE`),
  KEY `IDX_MD_KPI_SCORE_COLOR_SCOPE` (`SCOPE_TYPE`,`SCOPE_KEY`),
  KEY `IDX_MD_KPI_SCORE_COLOR_KPI` (`KPI_OID`),
  KEY `IDX_MD_KPI_SCORE_COLOR_STATUS` (`SCORE_STATUS`),
  KEY `IDX_MD_KPI_SCORE_COLOR_RANGE` (`SCORE_MIN`,`SCORE_MAX`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore KPI score color rule';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_kpi_score_color`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_kpi_score_color` WRITE;
/*!40000 ALTER TABLE `md_kpi_score_color` DISABLE KEYS */;
INSERT INTO `md_kpi_score_color` VALUES
('4851432a-687a-11f1-8693-3bbb3e091bdb','GLOBAL','GLOBAL',NULL,'CUSTOM','A01','KPI Color 01',80.0000,100.0000,'GOOD','#f7ff85','#198754',100,'Y','test','admin','2026-06-15 13:23:03','admin','2026-06-15 13:26:55');
/*!40000 ALTER TABLE `md_kpi_score_color` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_kpi_score_snapshot`
--

DROP TABLE IF EXISTS `md_kpi_score_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_kpi_score_snapshot` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `KPI_OID` char(36) NOT NULL COMMENT 'KPI OID',
  `PERIOD_TYPE` varchar(32) NOT NULL COMMENT '週期類型',
  `PERIOD_KEY` varchar(32) NOT NULL COMMENT '週期鍵',
  `DATA_FOR_TYPE` varchar(32) NOT NULL DEFAULT 'GLOBAL' COMMENT '分數歸屬 GLOBAL/ACCOUNT/ORG',
  `ACCOUNT` varchar(24) DEFAULT NULL COMMENT '分數歸屬帳號',
  `ORG_OID` char(36) DEFAULT NULL COMMENT '分數歸屬組織',
  `RAW_TARGET` decimal(24,6) DEFAULT NULL COMMENT '計算時目標值',
  `RAW_ACTUAL` decimal(24,6) DEFAULT NULL COMMENT '計算時實際值',
  `SCORE_VALUE` decimal(10,4) NOT NULL COMMENT '官方分數',
  `SCORE_STATUS` varchar(32) NOT NULL DEFAULT 'UNKNOWN' COMMENT '分數狀態 GOOD/WARNING/BAD/UNKNOWN',
  `FORMULA_OID` char(36) NOT NULL COMMENT '計算使用公式 OID',
  `FORMULA_VERSION_NO` int(11) NOT NULL COMMENT '計算使用公式版本',
  `AGGR_METHOD_OID` char(36) NOT NULL COMMENT '計算使用彙總方法 OID',
  `CALCULATION_TRACE` mediumtext DEFAULT NULL COMMENT '計算過程 JSON，供稽核與解釋',
  `CALCULATED_AT` datetime NOT NULL COMMENT '計算時間',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_KPI_SCORE_SNAPSHOT` (`KPI_OID`,`PERIOD_TYPE`,`PERIOD_KEY`,`DATA_FOR_TYPE`,`ACCOUNT`,`ORG_OID`),
  KEY `IDX_MD_KPI_SCORE_KPI` (`KPI_OID`),
  KEY `IDX_MD_KPI_SCORE_PERIOD` (`PERIOD_TYPE`,`PERIOD_KEY`),
  KEY `IDX_MD_KPI_SCORE_STATUS` (`SCORE_STATUS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore KPI 分數快照';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_kpi_score_snapshot`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_kpi_score_snapshot` WRITE;
/*!40000 ALTER TABLE `md_kpi_score_snapshot` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_kpi_score_snapshot` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_llm_provider_config`
--

DROP TABLE IF EXISTS `md_llm_provider_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_llm_provider_config` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `PROVIDER_CODE` varchar(64) NOT NULL COMMENT 'Provider 設定代碼',
  `PROVIDER_NAME` varchar(200) NOT NULL COMMENT 'Provider 顯示名稱',
  `PROVIDER_TYPE` varchar(32) NOT NULL COMMENT 'OPENAI/GEMINI',
  `API_BASE_URL` varchar(500) DEFAULT NULL COMMENT 'API Base URL',
  `DEFAULT_MODEL` varchar(128) NOT NULL COMMENT '預設模型',
  `API_KEY_ENCRYPTED` text NOT NULL COMMENT 'AES-GCM encrypted API key',
  `API_KEY_MASKED` varchar(128) DEFAULT NULL COMMENT '畫面顯示遮罩值',
  `ENABLED_FLAG` char(1) NOT NULL DEFAULT 'Y' COMMENT 'Y/N',
  `DEFAULT_FLAG` char(1) NOT NULL DEFAULT 'N' COMMENT '是否為預設 Provider',
  `CONNECT_STATUS` varchar(32) DEFAULT NULL COMMENT 'SUCCESS/FAILED',
  `LAST_TEST_AT` datetime DEFAULT NULL COMMENT '最後連線測試時間',
  `LAST_ERROR_MESSAGE` varchar(2000) DEFAULT NULL COMMENT '最後錯誤訊息',
  `CONFIG_JSON` longtext DEFAULT NULL COMMENT 'Provider 額外設定 JSON',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_LLM_PROVIDER_CODE` (`PROVIDER_CODE`),
  KEY `IDX_MD_LLM_PROVIDER_TYPE` (`PROVIDER_TYPE`),
  KEY `IDX_MD_LLM_PROVIDER_ENABLED` (`ENABLED_FLAG`,`DEFAULT_FLAG`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore LLM Provider 設定';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_llm_provider_config`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_llm_provider_config` WRITE;
/*!40000 ALTER TABLE `md_llm_provider_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_llm_provider_config` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_llm_run_log`
--

DROP TABLE IF EXISTS `md_llm_run_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_llm_run_log` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `PROVIDER_OID` char(36) NOT NULL COMMENT 'Provider Config OID',
  `PROVIDER_TYPE` varchar(32) NOT NULL COMMENT 'OPENAI/GEMINI',
  `MODEL_NAME` varchar(128) NOT NULL COMMENT '實際使用模型',
  `REQUEST_TYPE` varchar(32) NOT NULL COMMENT 'TEST/INSIGHT/RECOMMENDATION',
  `REF_TYPE` varchar(32) DEFAULT NULL COMMENT '業務來源類型',
  `REF_OID` char(36) DEFAULT NULL COMMENT '業務來源 OID',
  `REQUEST_ID` varchar(128) DEFAULT NULL COMMENT '外部服務 Request ID',
  `STATUS` varchar(32) NOT NULL COMMENT 'RUNNING/SUCCESS/FAILED',
  `STARTED_AT` datetime NOT NULL COMMENT '開始時間',
  `FINISHED_AT` datetime DEFAULT NULL COMMENT '完成時間',
  `DURATION_MS` bigint(20) DEFAULT NULL COMMENT '執行毫秒數',
  `INPUT_TOKENS` int(11) DEFAULT NULL COMMENT '輸入 Token 數',
  `OUTPUT_TOKENS` int(11) DEFAULT NULL COMMENT '輸出 Token 數',
  `TOTAL_TOKENS` int(11) DEFAULT NULL COMMENT '總 Token 數',
  `COST_ESTIMATE` decimal(18,8) DEFAULT NULL COMMENT '預估費用',
  `CURRENCY_CODE` varchar(8) DEFAULT NULL COMMENT '費用幣別',
  `ERROR_CODE` varchar(128) DEFAULT NULL COMMENT '錯誤代碼',
  `ERROR_MESSAGE` varchar(4000) DEFAULT NULL COMMENT '錯誤訊息',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  PRIMARY KEY (`OID`),
  KEY `IDX_MD_LLM_RUN_PROVIDER` (`PROVIDER_OID`),
  KEY `IDX_MD_LLM_RUN_REF` (`REF_TYPE`,`REF_OID`),
  KEY `IDX_MD_LLM_RUN_STATUS` (`STATUS`,`STARTED_AT`),
  KEY `IDX_MD_LLM_RUN_REQUEST_ID` (`REQUEST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore LLM 呼叫紀錄';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_llm_run_log`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_llm_run_log` WRITE;
/*!40000 ALTER TABLE `md_llm_run_log` DISABLE KEYS */;
INSERT INTO `md_llm_run_log` VALUES
('894d2230-6eb7-11f1-9c56-27fbabceb5cb','7fc3ba2b-6eb7-11f1-9c56-2756481e9ac8','OPENAI','GPT-5.5','TEST',NULL,NULL,'6e28b7a9-b0d9-4a36-809a-bb0b4ea26571','SUCCESS','2026-06-23 11:56:37','2026-06-23 11:56:38',836,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2026-06-23 11:56:38');
/*!40000 ALTER TABLE `md_llm_run_log` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_okr_checkin`
--

DROP TABLE IF EXISTS `md_okr_checkin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_okr_checkin` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `KR_OID` char(36) NOT NULL COMMENT 'KR OID',
  `CHECKIN_DATE` date NOT NULL COMMENT 'Check-in 日期',
  `CURRENT_VALUE` decimal(24,6) DEFAULT NULL COMMENT '更新後目前值',
  `PROGRESS_VALUE` decimal(10,4) NOT NULL DEFAULT 0.0000 COMMENT '更新後進度百分比',
  `CONFIDENCE_SCORE` decimal(10,4) DEFAULT NULL COMMENT '信心分數',
  `COMMENT_TEXT` varchar(2000) DEFAULT NULL COMMENT 'Check-in 說明',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  KEY `IDX_MD_OKR_CHECKIN_KR` (`KR_OID`),
  KEY `IDX_MD_OKR_CHECKIN_DATE` (`CHECKIN_DATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore OKR Check-in';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_okr_checkin`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_okr_checkin` WRITE;
/*!40000 ALTER TABLE `md_okr_checkin` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_okr_checkin` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_okr_cycle`
--

DROP TABLE IF EXISTS `md_okr_cycle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_okr_cycle` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `CYCLE_CODE` varchar(64) NOT NULL COMMENT '週期代碼',
  `CYCLE_NAME` varchar(200) NOT NULL COMMENT '週期名稱',
  `PERIOD_TYPE` varchar(32) NOT NULL COMMENT '週期類型',
  `START_DATE` date NOT NULL COMMENT '開始日期',
  `END_DATE` date NOT NULL COMMENT '結束日期',
  `STATUS` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '狀態 DRAFT/ACTIVE/CLOSED/ARCHIVED',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_OKR_CYCLE_CODE` (`CYCLE_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore OKR 週期';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_okr_cycle`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_okr_cycle` WRITE;
/*!40000 ALTER TABLE `md_okr_cycle` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_okr_cycle` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_okr_initiative`
--

DROP TABLE IF EXISTS `md_okr_initiative`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_okr_initiative` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `OBJECTIVE_OID` char(36) NOT NULL COMMENT 'Objective OID',
  `INITIATIVE_CODE` varchar(64) NOT NULL COMMENT 'Initiative 代碼',
  `INITIATIVE_NAME` varchar(300) NOT NULL COMMENT 'Initiative 名稱',
  `CONTENT` varchar(2000) DEFAULT NULL COMMENT 'Initiative 說明',
  `SORT_NO` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `STATUS` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '狀態',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_OKR_INITIATIVE_CODE` (`OBJECTIVE_OID`,`INITIATIVE_CODE`),
  KEY `IDX_MD_OKR_INITIATIVE_OBJECTIVE` (`OBJECTIVE_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore OKR Initiative';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_okr_initiative`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_okr_initiative` WRITE;
/*!40000 ALTER TABLE `md_okr_initiative` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_okr_initiative` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_okr_key_result`
--

DROP TABLE IF EXISTS `md_okr_key_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_okr_key_result` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `OBJECTIVE_OID` char(36) NOT NULL COMMENT 'Objective OID',
  `KR_CODE` varchar(64) NOT NULL COMMENT 'KR 代碼',
  `KR_NAME` varchar(300) NOT NULL COMMENT 'KR 名稱',
  `KR_TYPE` varchar(32) NOT NULL COMMENT 'KR 類型 INCREASE/DECREASE/PERCENT/MILESTONE/BINARY/MANUAL',
  `START_VALUE` decimal(24,6) DEFAULT NULL COMMENT '起始值',
  `TARGET_VALUE` decimal(24,6) DEFAULT NULL COMMENT '目標值',
  `CURRENT_VALUE` decimal(24,6) DEFAULT NULL COMMENT '目前值',
  `PROGRESS_VALUE` decimal(10,4) NOT NULL DEFAULT 0.0000 COMMENT '進度百分比',
  `WEIGHT_VALUE` decimal(10,4) NOT NULL DEFAULT 0.0000 COMMENT 'KR 權重',
  `UNIT_NAME` varchar(50) DEFAULT NULL COMMENT '單位',
  `SORT_NO` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `STATUS` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '狀態',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_OKR_KR_CODE` (`OBJECTIVE_OID`,`KR_CODE`),
  KEY `IDX_MD_OKR_KR_OBJECTIVE` (`OBJECTIVE_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore OKR Key Result';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_okr_key_result`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_okr_key_result` WRITE;
/*!40000 ALTER TABLE `md_okr_key_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_okr_key_result` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_okr_objective`
--

DROP TABLE IF EXISTS `md_okr_objective`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_okr_objective` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `CYCLE_OID` char(36) NOT NULL COMMENT 'OKR 週期 OID',
  `OBJECTIVE_CODE` varchar(64) NOT NULL COMMENT 'Objective 代碼',
  `OBJECTIVE_NAME` varchar(300) NOT NULL COMMENT 'Objective 名稱',
  `DESCRIPTION` varchar(2000) DEFAULT NULL COMMENT 'Objective 說明',
  `PARENT_OID` char(36) DEFAULT NULL COMMENT '上層 Objective OID',
  `CONFIDENCE_SCORE` decimal(10,4) DEFAULT NULL COMMENT '信心分數',
  `PROGRESS_VALUE` decimal(10,4) NOT NULL DEFAULT 0.0000 COMMENT '目前進度百分比',
  `STATUS` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '狀態',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_OKR_OBJECTIVE_CODE` (`CYCLE_OID`,`OBJECTIVE_CODE`),
  KEY `IDX_MD_OKR_OBJECTIVE_CYCLE` (`CYCLE_OID`),
  KEY `IDX_MD_OKR_OBJECTIVE_PARENT` (`PARENT_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore OKR Objective';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_okr_objective`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_okr_objective` WRITE;
/*!40000 ALTER TABLE `md_okr_objective` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_okr_objective` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_okr_objective_owner`
--

DROP TABLE IF EXISTS `md_okr_objective_owner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_okr_objective_owner` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `OBJECTIVE_OID` char(36) NOT NULL COMMENT 'Objective OID',
  `OWNER_TYPE` varchar(32) NOT NULL COMMENT 'owner 類型 ACCOUNT/ORG',
  `ACCOUNT` varchar(24) DEFAULT NULL COMMENT 'qifu4 帳號',
  `ORG_OID` char(36) DEFAULT NULL COMMENT '組織 OID',
  `OWNER_ROLE` varchar(32) NOT NULL DEFAULT 'OWNER' COMMENT 'owner 角色 OWNER/VIEWER/APPROVER',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  KEY `IDX_MD_OKR_OBJ_OWNER_OBJ` (`OBJECTIVE_OID`),
  KEY `IDX_MD_OKR_OBJ_OWNER_ACCOUNT` (`ACCOUNT`),
  KEY `IDX_MD_OKR_OBJ_OWNER_ORG` (`ORG_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore OKR Objective owner';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_okr_objective_owner`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_okr_objective_owner` WRITE;
/*!40000 ALTER TABLE `md_okr_objective_owner` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_okr_objective_owner` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_okr_snapshot`
--

DROP TABLE IF EXISTS `md_okr_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_okr_snapshot` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `OBJECTIVE_OID` char(36) NOT NULL COMMENT 'Objective OID',
  `PERIOD_KEY` varchar(32) NOT NULL COMMENT '快照週期鍵',
  `PROGRESS_VALUE` decimal(10,4) NOT NULL COMMENT 'Objective 進度百分比',
  `CONFIDENCE_SCORE` decimal(10,4) DEFAULT NULL COMMENT '信心分數',
  `SCORE_STATUS` varchar(32) NOT NULL DEFAULT 'UNKNOWN' COMMENT '狀態 GOOD/WARNING/BAD/UNKNOWN',
  `CALCULATION_TRACE` mediumtext DEFAULT NULL COMMENT '計算過程 JSON',
  `SNAPSHOT_AT` datetime NOT NULL COMMENT '快照時間',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_OKR_SNAPSHOT` (`OBJECTIVE_OID`,`PERIOD_KEY`),
  KEY `IDX_MD_OKR_SNAPSHOT_OBJ` (`OBJECTIVE_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore OKR 快照';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_okr_snapshot`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_okr_snapshot` WRITE;
/*!40000 ALTER TABLE `md_okr_snapshot` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_okr_snapshot` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_org_member`
--

DROP TABLE IF EXISTS `md_org_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_org_member` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `ORG_OID` char(36) NOT NULL COMMENT '組織 OID',
  `ACCOUNT` varchar(24) NOT NULL COMMENT 'qifu4 帳號',
  `DISPLAY_NAME` varchar(100) DEFAULT NULL COMMENT '顯示名稱',
  `EMPLOYEE_ID` varchar(32) DEFAULT NULL COMMENT '員工編號',
  `EMAIL` varchar(100) DEFAULT NULL COMMENT 'Email',
  `JOB_TITLE` varchar(100) DEFAULT NULL COMMENT '職稱',
  `IS_MANAGER` varchar(1) NOT NULL DEFAULT 'N' COMMENT '是否主管 Y/N',
  `ENABLED` varchar(1) NOT NULL DEFAULT 'Y' COMMENT '是否啟用 Y/N',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_ORG_MEMBER` (`ORG_OID`,`ACCOUNT`),
  KEY `IDX_MD_ORG_MEMBER_ACCOUNT` (`ACCOUNT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore 組織成員';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_org_member`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_org_member` WRITE;
/*!40000 ALTER TABLE `md_org_member` DISABLE KEYS */;
INSERT INTO `md_org_member` VALUES
('32d49908-911e-4c5c-a590-3e5a7c3168ec','fb8df964-65fe-11f1-bc28-b315ecf56715','admin','Administrator','T0001','chen.xin.nien@gmail.com','Administrator','N','Y','admin','2026-06-23 20:19:49',NULL,NULL),
('3bfceee8-385b-4ca6-b086-34221a99c4ea','fb8df964-65fe-11f1-bc28-b315ecf56715','steven','Steven','T0003','test@test.org','MIS','N','Y','admin','2026-06-23 20:23:43',NULL,NULL),
('7634503d-8418-4d65-8393-4534ade34be3','fb8df964-65fe-11f1-bc28-b315ecf56715','tester','Tester','T0002','chen.xin.nien@gmail.com','Tester','N','Y','admin','2026-06-23 20:21:56',NULL,NULL),
('8f63d1f4-6601-11f1-ae4f-b14e72636d69','fb8df964-65fe-11f1-bc28-b315ecf56715','peter','小明2','A000102012','test@test.org','專員2','N','Y','admin','2026-06-12 09:53:51',NULL,NULL),
('dff154b0-e45f-4e45-9bed-48be76dae18c','fb8df964-65fe-11f1-bc28-b315ecf56715','tiffany','Tiffany wang','T0004','test@test.org','Manager','Y','Y','admin','2026-06-23 20:25:03',NULL,NULL);
/*!40000 ALTER TABLE `md_org_member` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_org_unit`
--

DROP TABLE IF EXISTS `md_org_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_org_unit` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `ORG_CODE` varchar(64) NOT NULL COMMENT '組織代碼',
  `ORG_NAME` varchar(200) NOT NULL COMMENT '組織名稱',
  `PARENT_OID` char(36) DEFAULT NULL COMMENT '上層組織 OID',
  `ORG_LEVEL` int(11) NOT NULL DEFAULT 1 COMMENT '組織層級',
  `SORT_NO` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `ENABLED` varchar(1) NOT NULL DEFAULT 'Y' COMMENT '是否啟用 Y/N',
  `DESCRIPTION` varchar(1000) DEFAULT NULL COMMENT '組織說明',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_ORG_UNIT_CODE` (`ORG_CODE`),
  KEY `IDX_MD_ORG_UNIT_PARENT` (`PARENT_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore 組織單位';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_org_unit`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_org_unit` WRITE;
/*!40000 ALTER TABLE `md_org_unit` DISABLE KEYS */;
INSERT INTO `md_org_unit` VALUES
('fb8df964-65fe-11f1-bc28-b315ecf56715','A001','測試單位A5','00000000-0000-0000-0000-000000000000',1,0,'Y','','admin','2026-06-12 09:35:24',NULL,NULL),
('fdaa97d6-65fe-11f1-bc28-f142032c710a','B001','測試單位B',NULL,1,0,'Y','','admin','2026-06-12 09:35:27','admin','2026-06-12 09:54:47');
/*!40000 ALTER TABLE `md_org_unit` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_password_reset_token`
--

DROP TABLE IF EXISTS `md_password_reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_password_reset_token` (
  `OID` char(36) NOT NULL,
  `ACCOUNT` varchar(24) NOT NULL COMMENT 'qifu4 帳號',
  `TOKEN_HASH` varchar(64) NOT NULL COMMENT 'SHA-256 token hash',
  `EXPIRES_TIME` datetime NOT NULL,
  `USED_FLAG` varchar(1) NOT NULL DEFAULT 'N',
  `USED_TIME` datetime DEFAULT NULL,
  `REVOKED_FLAG` varchar(1) NOT NULL DEFAULT 'N',
  `REVOKED_TIME` datetime DEFAULT NULL,
  `CUSERID` varchar(24) DEFAULT NULL,
  `CDATE` datetime DEFAULT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_PASSWORD_RESET_TOKEN` (`TOKEN_HASH`),
  KEY `IDX_MD_PASSWORD_RESET_TOKEN_ACCOUNT` (`ACCOUNT`),
  KEY `IDX_MD_PASSWORD_RESET_TOKEN_STATUS` (`USED_FLAG`,`REVOKED_FLAG`,`EXPIRES_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_password_reset_token`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_password_reset_token` WRITE;
/*!40000 ALTER TABLE `md_password_reset_token` DISABLE KEYS */;
INSERT INTO `md_password_reset_token` VALUES
('a4568a8f-6f8e-11f1-b7b4-47585fba908c','tester','c447478be76bc07abca47aa48ef803299bf26842edeab6ad0e225be24fcaffe3','2026-06-24 14:11:25','N',NULL,'N',NULL,'admin','2026-06-24 13:36:25',NULL,NULL);
/*!40000 ALTER TABLE `md_password_reset_token` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_strategy_objective`
--

DROP TABLE IF EXISTS `md_strategy_objective`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_strategy_objective` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `THEME_OID` char(36) NOT NULL COMMENT '策略主題 OID',
  `OBJECTIVE_CODE` varchar(64) NOT NULL COMMENT '策略目標代碼',
  `OBJECTIVE_NAME` varchar(300) NOT NULL COMMENT '策略目標名稱',
  `WEIGHT_VALUE` decimal(10,4) NOT NULL DEFAULT 0.0000 COMMENT '權重',
  `SORT_NO` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `DESCRIPTION` varchar(2000) DEFAULT NULL COMMENT '說明',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_STRATEGY_OBJECTIVE` (`THEME_OID`,`OBJECTIVE_CODE`),
  KEY `IDX_MD_STRATEGY_OBJECTIVE_THEME` (`THEME_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore 策略目標';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_strategy_objective`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_strategy_objective` WRITE;
/*!40000 ALTER TABLE `md_strategy_objective` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_strategy_objective` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_strategy_objective_link`
--

DROP TABLE IF EXISTS `md_strategy_objective_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_strategy_objective_link` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `STRATEGY_OBJECTIVE_OID` char(36) NOT NULL COMMENT '策略目標 OID',
  `LINK_TYPE` varchar(32) NOT NULL COMMENT '連結類型 KPI/OKR_OBJECTIVE',
  `LINK_OID` char(36) NOT NULL COMMENT '連結物件 OID',
  `WEIGHT_VALUE` decimal(10,4) NOT NULL DEFAULT 0.0000 COMMENT '此連結權重',
  `SORT_NO` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_STRATEGY_OBJ_LINK` (`STRATEGY_OBJECTIVE_OID`,`LINK_TYPE`,`LINK_OID`),
  KEY `IDX_MD_STRATEGY_OBJ_LINK_SO` (`STRATEGY_OBJECTIVE_OID`),
  KEY `IDX_MD_STRATEGY_OBJ_LINK_REF` (`LINK_TYPE`,`LINK_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore 策略目標連結';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_strategy_objective_link`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_strategy_objective_link` WRITE;
/*!40000 ALTER TABLE `md_strategy_objective_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_strategy_objective_link` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_strategy_snapshot`
--

DROP TABLE IF EXISTS `md_strategy_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_strategy_snapshot` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `WORKSPACE_OID` char(36) NOT NULL COMMENT '策略工作區 OID',
  `PERIOD_TYPE` varchar(32) NOT NULL COMMENT '週期類型',
  `PERIOD_KEY` varchar(32) NOT NULL COMMENT '週期鍵',
  `SCORE_VALUE` decimal(10,4) NOT NULL COMMENT '策略工作區分數',
  `KPI_COUNT` int(11) NOT NULL DEFAULT 0 COMMENT '納入計算 KPI 數量',
  `OKR_COUNT` int(11) NOT NULL DEFAULT 0 COMMENT '納入計算 OKR 數量',
  `CALCULATION_TRACE` mediumtext DEFAULT NULL COMMENT '計算過程 JSON',
  `SNAPSHOT_AT` datetime NOT NULL COMMENT '快照時間',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_STRATEGY_SNAPSHOT` (`WORKSPACE_OID`,`PERIOD_TYPE`,`PERIOD_KEY`),
  KEY `IDX_MD_STRATEGY_SNAPSHOT_WS` (`WORKSPACE_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore 策略快照';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_strategy_snapshot`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_strategy_snapshot` WRITE;
/*!40000 ALTER TABLE `md_strategy_snapshot` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_strategy_snapshot` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_strategy_theme`
--

DROP TABLE IF EXISTS `md_strategy_theme`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_strategy_theme` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `WORKSPACE_OID` char(36) NOT NULL COMMENT '策略工作區 OID',
  `THEME_CODE` varchar(64) NOT NULL COMMENT '策略主題代碼',
  `THEME_NAME` varchar(200) NOT NULL COMMENT '策略主題名稱',
  `WEIGHT_VALUE` decimal(10,4) NOT NULL DEFAULT 0.0000 COMMENT '權重',
  `SORT_NO` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `DESCRIPTION` varchar(2000) DEFAULT NULL COMMENT '說明',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_STRATEGY_THEME` (`WORKSPACE_OID`,`THEME_CODE`),
  KEY `IDX_MD_STRATEGY_THEME_WS` (`WORKSPACE_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore 策略主題';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_strategy_theme`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_strategy_theme` WRITE;
/*!40000 ALTER TABLE `md_strategy_theme` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_strategy_theme` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `md_strategy_workspace`
--

DROP TABLE IF EXISTS `md_strategy_workspace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_strategy_workspace` (
  `OID` char(36) NOT NULL COMMENT '主鍵 OID',
  `WORKSPACE_CODE` varchar(64) NOT NULL COMMENT '策略工作區代碼',
  `WORKSPACE_NAME` varchar(200) NOT NULL COMMENT '策略工作區名稱',
  `VISION_TEXT` varchar(2000) DEFAULT NULL COMMENT '願景',
  `MISSION_TEXT` varchar(2000) DEFAULT NULL COMMENT '使命',
  `DESCRIPTION` varchar(2000) DEFAULT NULL COMMENT '說明',
  `STATUS` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '狀態',
  `CUSERID` varchar(24) NOT NULL COMMENT '建立者',
  `CDATE` datetime NOT NULL COMMENT '建立時間',
  `UUSERID` varchar(24) DEFAULT NULL COMMENT '更新者',
  `UDATE` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_MD_STRATEGY_WORKSPACE` (`WORKSPACE_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci COMMENT='MindScore 策略工作區';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_strategy_workspace`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `md_strategy_workspace` WRITE;
/*!40000 ALTER TABLE `md_strategy_workspace` DISABLE KEYS */;
/*!40000 ALTER TABLE `md_strategy_workspace` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_account`
--

DROP TABLE IF EXISTS `tb_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_account` (
  `OID` char(36) NOT NULL,
  `ACCOUNT` varchar(24) NOT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  `ON_JOB` varchar(50) NOT NULL DEFAULT 'Y',
  `CUSERID` varchar(24) DEFAULT NULL,
  `CDATE` datetime DEFAULT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`ACCOUNT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_account`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_account` WRITE;
/*!40000 ALTER TABLE `tb_account` DISABLE KEYS */;
INSERT INTO `tb_account` VALUES
('0','admin','$2y$12$Q4x02Q0WKHWXAQ.NoGCs8ObX4sac890xeRnaNUxNnz/VEiHWazIp.','Y','admin','2012-11-11 10:56:23','admin','2014-04-19 11:32:04'),
('15822da5-25dc-490c-bdfb-be75f5ff4843','tester','$2y$12$Q4x02Q0WKHWXAQ.NoGCs8ObX4sac890xeRnaNUxNnz/VEiHWazIp.','Y','admin','2015-04-23 11:26:53','admin','2026-06-24 13:30:23'),
('52cb274e-388d-419f-a81e-67ca599bfb63','steven','$2y$12$Q4x02Q0WKHWXAQ.NoGCs8ObX4sac890xeRnaNUxNnz/VEiHWazIp.','Y','admin','2015-09-11 10:33:53',NULL,NULL),
('8f624b52-6601-11f1-ae4f-01a04108344b','peter','$2y$12$Q4x02Q0WKHWXAQ.NoGCs8ObX4sac890xeRnaNUxNnz/VEiHWazIp.','Y','admin','2026-06-12 09:53:51',NULL,NULL),
('9c239d19-3646-41db-b394-d34c5bf34671','tiffany','$2y$12$Q4x02Q0WKHWXAQ.NoGCs8ObX4sac890xeRnaNUxNnz/VEiHWazIp.','Y','admin','2015-09-11 10:15:29',NULL,NULL);
/*!40000 ALTER TABLE `tb_account` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_role`
--

DROP TABLE IF EXISTS `tb_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_role` (
  `OID` char(36) NOT NULL,
  `ROLE` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(500) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(50) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`ROLE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_role`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_role` WRITE;
/*!40000 ALTER TABLE `tb_role` DISABLE KEYS */;
INSERT INTO `tb_role` VALUES
('19f1523b-5afc-11f1-86b7-bd261cfeb8ff','testrole','test','admin','2026-05-29 09:17:03',NULL,NULL),
('4b1796ad-0bb7-4a65-b45e-439540ba5dbd','admin','administrator role!','admin','2014-10-09 15:02:24',NULL,NULL),
('58914623-46ea-4797-bbec-2dadc5d0800e','COMMON01','Common role!','admin','2017-05-09 13:31:42',NULL,NULL),
('c7c69396-e5e6-48ca-b09c-9445b69e2ad5','*','all role','admin','2014-10-09 15:02:54',NULL,NULL);
/*!40000 ALTER TABLE `tb_role` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_role_permission`
--

DROP TABLE IF EXISTS `tb_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_role_permission` (
  `OID` char(36) NOT NULL,
  `ROLE` varchar(50) NOT NULL,
  `PERMISSION` varchar(255) NOT NULL,
  `PERM_TYPE` varchar(15) NOT NULL,
  `DESCRIPTION` varchar(500) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(50) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`ROLE`,`PERMISSION`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_role_permission`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_role_permission` WRITE;
/*!40000 ALTER TABLE `tb_role_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_role_permission` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys`
--

DROP TABLE IF EXISTS `tb_sys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys` (
  `OID` char(36) NOT NULL,
  `SYS_ID` varchar(10) NOT NULL,
  `NAME` varchar(100) NOT NULL,
  `HOST` varchar(200) NOT NULL,
  `CONTEXT_PATH` varchar(100) NOT NULL,
  `IS_LOCAL` varchar(1) NOT NULL DEFAULT 'Y',
  `ICON` varchar(20) NOT NULL DEFAULT ' ',
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`SYS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys` WRITE;
/*!40000 ALTER TABLE `tb_sys` DISABLE KEYS */;
INSERT INTO `tb_sys` VALUES
('c6643182-85a5-4f91-9e73-10567ebd0dd5','CORE','Core-system','127.0.0.1:8080','core-web','Y','SYSTEM','admin','2017-04-10 20:42:00','admin','2026-06-01 15:35:15');
/*!40000 ALTER TABLE `tb_sys` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_bean_help`
--

DROP TABLE IF EXISTS `tb_sys_bean_help`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_bean_help` (
  `OID` char(36) NOT NULL,
  `BEAN_ID` varchar(255) NOT NULL,
  `METHOD` varchar(100) NOT NULL,
  `SYSTEM` varchar(10) NOT NULL,
  `ENABLE_FLAG` varchar(1) NOT NULL,
  `DESCRIPTION` varchar(500) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`BEAN_ID`,`METHOD`,`SYSTEM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_bean_help`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_bean_help` WRITE;
/*!40000 ALTER TABLE `tb_sys_bean_help` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_bean_help` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_bean_help_expr`
--

DROP TABLE IF EXISTS `tb_sys_bean_help_expr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_bean_help_expr` (
  `OID` char(36) NOT NULL,
  `HELP_OID` char(36) NOT NULL,
  `EXPR_ID` varchar(20) NOT NULL,
  `EXPR_SEQ` varchar(10) NOT NULL,
  `RUN_TYPE` varchar(10) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`EXPR_ID`,`HELP_OID`,`RUN_TYPE`),
  KEY `IDX_1` (`HELP_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_bean_help_expr`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_bean_help_expr` WRITE;
/*!40000 ALTER TABLE `tb_sys_bean_help_expr` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_bean_help_expr` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_bean_help_expr_map`
--

DROP TABLE IF EXISTS `tb_sys_bean_help_expr_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_bean_help_expr_map` (
  `OID` char(36) NOT NULL,
  `HELP_EXPR_OID` char(36) NOT NULL,
  `METHOD_RESULT_FLAG` varchar(1) NOT NULL DEFAULT 'N',
  `METHOD_PARAM_CLASS` varchar(255) NOT NULL DEFAULT ' ',
  `METHOD_PARAM_INDEX` int(3) NOT NULL DEFAULT 0,
  `VAR_NAME` varchar(255) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`VAR_NAME`,`HELP_EXPR_OID`),
  KEY `IDX_1` (`HELP_EXPR_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_bean_help_expr_map`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_bean_help_expr_map` WRITE;
/*!40000 ALTER TABLE `tb_sys_bean_help_expr_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_bean_help_expr_map` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_code`
--

DROP TABLE IF EXISTS `tb_sys_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_code` (
  `OID` char(36) NOT NULL,
  `CODE` varchar(25) NOT NULL,
  `TYPE` varchar(10) NOT NULL,
  `NAME` varchar(100) NOT NULL,
  `PARAM1` varchar(100) DEFAULT NULL,
  `PARAM2` varchar(100) DEFAULT NULL,
  `PARAM3` varchar(100) DEFAULT NULL,
  `PARAM4` varchar(100) DEFAULT NULL,
  `PARAM5` varchar(100) DEFAULT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_code`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_code` WRITE;
/*!40000 ALTER TABLE `tb_sys_code` DISABLE KEYS */;
INSERT INTO `tb_sys_code` VALUES
('2d9c84e4-a956-42ac-96cb-1f6292d182a9','CNF_CONF002','CNF','enable mail sender!','Y',NULL,NULL,NULL,NULL,'admin','2014-12-25 09:09:57','admin','2020-09-14 04:36:34'),
('4df770a6-6a9c-4d25-bdcd-1dee819d2ba6','CNF_CONF001','CNF','default mail from account!','root@localhost',NULL,NULL,NULL,NULL,'admin','2014-12-24 21:51:16','admin','2020-09-14 04:36:34'),
('57877c4d-4f3e-4679-880a-a262eeba0c3d','TOKEN','AUTH','QiFu3 Client token','9TYM7TRuILqFk9XoR0v6Yx672','COMMON01',NULL,NULL,NULL,'admin','2021-10-30 17:12:04',NULL,NULL),
('a5f7ee37-f33f-48a6-b448-92ccb8cdf96a','CNF_CONF003','CNF','first load javascript','addTab(\'CORE_PROG999D9999Q\', null);',NULL,NULL,NULL,NULL,'admin','2014-12-25 09:09:57',NULL,NULL),
('caf00ba5-fe63-4dc4-a1a3-32527f6629b2','CMM_CONF001','CMM','Common role for default user!','COMMON01',NULL,NULL,NULL,NULL,'admin','2017-05-09 12:29:00',NULL,NULL);
/*!40000 ALTER TABLE `tb_sys_code` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_event_log`
--

DROP TABLE IF EXISTS `tb_sys_event_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_event_log` (
  `OID` char(36) NOT NULL,
  `USER` varchar(24) NOT NULL,
  `SYS_ID` varchar(10) NOT NULL,
  `EXECUTE_EVENT` varchar(255) NOT NULL,
  `IS_PERMIT` varchar(1) NOT NULL DEFAULT 'N',
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  KEY `IDX_1` (`USER`),
  KEY `IDX_2` (`CDATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_event_log`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_event_log` WRITE;
/*!40000 ALTER TABLE `tb_sys_event_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_event_log` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_expr_job`
--

DROP TABLE IF EXISTS `tb_sys_expr_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_expr_job` (
  `OID` char(36) NOT NULL,
  `SYSTEM` varchar(10) NOT NULL,
  `ID` varchar(20) NOT NULL,
  `NAME` varchar(100) NOT NULL,
  `ACTIVE` varchar(1) NOT NULL DEFAULT 'Y',
  `DESCRIPTION` varchar(500) DEFAULT NULL,
  `RUN_STATUS` varchar(1) NOT NULL DEFAULT 'Y',
  `CHECK_FAULT` varchar(1) NOT NULL DEFAULT 'N',
  `EXPR_ID` varchar(20) NOT NULL,
  `RUN_DAY_OF_WEEK` varchar(1) NOT NULL,
  `RUN_HOUR` varchar(2) NOT NULL,
  `RUN_MINUTE` varchar(2) NOT NULL,
  `CONTACT_MODE` varchar(1) NOT NULL DEFAULT '0',
  `CONTACT` varchar(500) DEFAULT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`ID`),
  KEY `IDX_1` (`SYSTEM`,`ACTIVE`,`EXPR_ID`,`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_expr_job`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_expr_job` WRITE;
/*!40000 ALTER TABLE `tb_sys_expr_job` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_expr_job` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_expr_job_log`
--

DROP TABLE IF EXISTS `tb_sys_expr_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_expr_job_log` (
  `OID` char(36) NOT NULL,
  `ID` varchar(20) NOT NULL,
  `LOG_STATUS` varchar(1) NOT NULL DEFAULT 'N',
  `BEGIN_DATETIME` datetime NOT NULL,
  `END_DATETIME` datetime NOT NULL,
  `FAULT_MSG` varchar(2000) DEFAULT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  KEY `IDX_1` (`ID`,`LOG_STATUS`,`BEGIN_DATETIME`),
  KEY `IDX_2` (`CDATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_expr_job_log`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_expr_job_log` WRITE;
/*!40000 ALTER TABLE `tb_sys_expr_job_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_expr_job_log` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_expression`
--

DROP TABLE IF EXISTS `tb_sys_expression`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_expression` (
  `OID` char(36) NOT NULL,
  `EXPR_ID` varchar(20) NOT NULL,
  `TYPE` varchar(10) NOT NULL,
  `NAME` varchar(100) NOT NULL,
  `CONTENT` varchar(8000) NOT NULL,
  `DESCRIPTION` varchar(500) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`EXPR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_expression`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_expression` WRITE;
/*!40000 ALTER TABLE `tb_sys_expression` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_expression` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_icon`
--

DROP TABLE IF EXISTS `tb_sys_icon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_icon` (
  `OID` char(36) NOT NULL,
  `ICON_ID` varchar(20) NOT NULL,
  `FILE_NAME` varchar(200) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`ICON_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_icon`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_icon` WRITE;
/*!40000 ALTER TABLE `tb_sys_icon` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_icon` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_jreport`
--

DROP TABLE IF EXISTS `tb_sys_jreport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_jreport` (
  `OID` char(36) NOT NULL,
  `REPORT_ID` varchar(50) NOT NULL,
  `FILE` varchar(100) NOT NULL,
  `IS_COMPILE` varchar(50) NOT NULL DEFAULT 'N',
  `CONTENT` mediumblob NOT NULL,
  `DESCRIPTION` varchar(500) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`REPORT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_jreport`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_jreport` WRITE;
/*!40000 ALTER TABLE `tb_sys_jreport` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_jreport` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_jreport_param`
--

DROP TABLE IF EXISTS `tb_sys_jreport_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_jreport_param` (
  `OID` char(36) NOT NULL,
  `REPORT_ID` varchar(50) NOT NULL,
  `URL_PARAM` varchar(100) NOT NULL,
  `RPT_PARAM` varchar(100) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`REPORT_ID`,`RPT_PARAM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_jreport_param`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_jreport_param` WRITE;
/*!40000 ALTER TABLE `tb_sys_jreport_param` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_jreport_param` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_login_log`
--

DROP TABLE IF EXISTS `tb_sys_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_login_log` (
  `OID` char(36) NOT NULL,
  `USER` varchar(24) NOT NULL,
  `FAIL_FLAG` char(1) NOT NULL DEFAULT 'N',
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  KEY `IDX_1` (`USER`),
  KEY `IDX_2` (`CDATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_login_log`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_login_log` WRITE;
/*!40000 ALTER TABLE `tb_sys_login_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_login_log` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_mail_helper`
--

DROP TABLE IF EXISTS `tb_sys_mail_helper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_mail_helper` (
  `OID` char(36) NOT NULL,
  `MAIL_ID` varchar(17) NOT NULL,
  `SUBJECT` varchar(200) NOT NULL,
  `TEXT` blob DEFAULT NULL,
  `MAIL_FROM` varchar(100) NOT NULL,
  `MAIL_TO` varchar(100) NOT NULL,
  `MAIL_CC` varchar(1000) DEFAULT NULL,
  `MAIL_BCC` varchar(1000) DEFAULT NULL,
  `SUCCESS_FLAG` varchar(1) NOT NULL DEFAULT 'N',
  `SUCCESS_TIME` datetime DEFAULT NULL,
  `RETAIN_FLAG` varchar(1) NOT NULL DEFAULT 'N',
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`MAIL_ID`),
  KEY `IDX_1` (`MAIL_ID`),
  KEY `IDX_2` (`SUCCESS_FLAG`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_mail_helper`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_mail_helper` WRITE;
/*!40000 ALTER TABLE `tb_sys_mail_helper` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_mail_helper` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_menu`
--

DROP TABLE IF EXISTS `tb_sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_menu` (
  `OID` char(36) NOT NULL,
  `PROG_ID` varchar(50) NOT NULL,
  `PARENT_OID` char(36) NOT NULL,
  `ENABLE_FLAG` varchar(1) NOT NULL DEFAULT 'Y',
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`PROG_ID`,`PARENT_OID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_menu`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_menu` WRITE;
/*!40000 ALTER TABLE `tb_sys_menu` DISABLE KEYS */;
INSERT INTO `tb_sys_menu` VALUES
('079c507d-6adc-11f1-8576-2d8a171b1ad4','MD_PROG008D0001Q','8748b563-6ab5-11f1-b802-2d3506a5d7f6','Y','admin','2026-06-18 14:07:47',NULL,NULL),
('079f5dbe-6adc-11f1-8576-7d5f2d10c2a1','MD_PROG008D0002Q','8748b563-6ab5-11f1-b802-2d3506a5d7f6','Y','admin','2026-06-18 14:07:47',NULL,NULL),
('07a0481f-6adc-11f1-8576-77fc06a38a11','MD_PROG008D0005Q','8748b563-6ab5-11f1-b802-2d3506a5d7f6','Y','admin','2026-06-18 14:07:47',NULL,NULL),
('2201f1a7-6adf-11f1-ba7d-25acac64ae00','MD_PROG009D','00000000-0000-0000-0000-000000000000','Y','admin','2026-06-18 14:30:00',NULL,NULL),
('22028de8-6adf-11f1-ba7d-bb09e31178cf','MD_PROG009D0001Q','2201f1a7-6adf-11f1-ba7d-25acac64ae00','Y','admin','2026-06-18 14:30:00',NULL,NULL),
('25b62ac5-6e3a-11f1-ada3-f96ece44675b','MD_PROG010D','00000000-0000-0000-0000-000000000000','Y','admin','2026-06-22 20:59:04',NULL,NULL),
('37fa0e49-6674-11f1-9dca-0d721c5c0f0c','MD_PROG004D','00000000-0000-0000-0000-000000000000','Y','admin','2026-06-12 23:34:36',NULL,NULL),
('37fb94ea-6674-11f1-9dca-0d6665973f3b','MD_PROG004D0001Q','37fa0e49-6674-11f1-9dca-0d721c5c0f0c','Y','admin','2026-06-12 23:34:36',NULL,NULL),
('4bd4d202-5feb-495b-8c8c-ec6b7f5b8041','CORE_PROG002D0002Q','79e1cf24-2522-4cdf-abcc-6455b47d545b','Y','admin','2017-05-10 14:20:12',NULL,NULL),
('57b2fa07-6985-11f1-9ff0-6fc50c28457a','MD_PROG006D','00000000-0000-0000-0000-000000000000','Y','admin','2026-06-16 21:14:44',NULL,NULL),
('5e055f61-bfc5-402c-93b4-f241dc17b00b','CORE_PROG004D','00000000-0000-0000-0000-000000000000','Y','admin','2017-06-03 14:23:17',NULL,NULL),
('6383c095-6633-11f1-aa10-b7dc47bd3176','MD_PROG002D0001Q','bef9d6cc-6605-11f1-81b4-6babbff1c7f5','Y','admin','2026-06-12 15:50:32',NULL,NULL),
('63845cd6-6633-11f1-aa10-2bc96535409b','MD_PROG002D0002Q','bef9d6cc-6605-11f1-81b4-6babbff1c7f5','Y','admin','2026-06-12 15:50:32',NULL,NULL),
('63856e47-6633-11f1-aa10-0dba6dbbdbe5','MD_PROG002D0003Q','bef9d6cc-6605-11f1-81b4-6babbff1c7f5','Y','admin','2026-06-12 15:50:32',NULL,NULL),
('79e1cf24-2522-4cdf-abcc-6455b47d545b','CORE_PROG002D','00000000-0000-0000-0000-000000000000','Y','admin','2017-05-08 21:32:59',NULL,NULL),
('7aa1208a-5fc2-11f1-afe9-33fb6c1b9ce7','CORE_PROG005D','00000000-0000-0000-0000-000000000000','Y','admin','2026-06-04 11:07:11',NULL,NULL),
('7aa2590b-5fc2-11f1-afe9-73b1551d818b','CORE_PROG005D0001Q','7aa1208a-5fc2-11f1-afe9-33fb6c1b9ce7','Y','admin','2026-06-04 11:07:11',NULL,NULL),
('7ea68636-c93a-4669-ac42-dafc3770d20d','CORE_PROG001D','00000000-0000-0000-0000-000000000000','Y','admin','2017-04-20 11:24:53',NULL,NULL),
('8748b563-6ab5-11f1-b802-2d3506a5d7f6','MD_PROG008D','00000000-0000-0000-0000-000000000000','Y','admin','2026-06-18 09:32:11',NULL,NULL),
('8f83e222-6639-11f1-bf8d-b59eb088d935','MD_PROG003D','00000000-0000-0000-0000-000000000000','Y','admin','2026-06-12 16:34:43',NULL,NULL),
('91f90746-6558-11f1-8ea0-31612e6b0ce2','MD_PROG001D','00000000-0000-0000-0000-000000000000','Y','admin','2026-06-11 13:44:10',NULL,NULL),
('947ccbfe-6558-11f1-8ea0-db03aa0e50b5','MD_PROG001D0001Q','91f90746-6558-11f1-8ea0-31612e6b0ce2','Y','admin','2026-06-11 13:44:14',NULL,NULL),
('947d412f-6558-11f1-8ea0-5d8cbc8c4b4c','MD_PROG001D0002Q','91f90746-6558-11f1-8ea0-31612e6b0ce2','Y','admin','2026-06-11 13:44:14',NULL,NULL),
('947db660-6558-11f1-8ea0-f1e5fa666cce','MD_PROG001D0003Q','91f90746-6558-11f1-8ea0-31612e6b0ce2','Y','admin','2026-06-11 13:44:14',NULL,NULL),
('9972c249-2985-49ac-9b8b-f6c25c65fd4e','CORE_PROG002D0003Q','79e1cf24-2522-4cdf-abcc-6455b47d545b','Y','admin','2017-05-10 14:20:12',NULL,NULL),
('b575783b-692f-11f1-b477-7bfe882fcfd1','MD_PROG005D','00000000-0000-0000-0000-000000000000','Y','admin','2026-06-16 11:01:45',NULL,NULL),
('b576629c-692f-11f1-b477-61800eed04aa','MD_PROG005D0001Q','b575783b-692f-11f1-b477-7bfe882fcfd1','Y','admin','2026-06-16 11:01:45',NULL,NULL),
('bef9d6cc-6605-11f1-81b4-6babbff1c7f5','MD_PROG002D','00000000-0000-0000-0000-000000000000','Y','admin','2026-06-12 10:23:49',NULL,NULL),
('c14a7614-6a4b-11f1-bdd1-ed1d9f24b304','MD_PROG007D','00000000-0000-0000-0000-000000000000','Y','admin','2026-06-17 20:55:02',NULL,NULL),
('c5349a26-6d6e-4d94-b817-82be6d14d5ed','CORE_PROG002D0001Q','79e1cf24-2522-4cdf-abcc-6455b47d545b','Y','admin','2017-05-10 14:20:12',NULL,NULL),
('c5817a7d-6877-11f1-856d-1dee65015868','MD_PROG003D0001Q','8f83e222-6639-11f1-bf8d-b59eb088d935','Y','admin','2026-06-15 13:05:04',NULL,NULL),
('c582b2fe-6877-11f1-856d-c95cf26024bf','MD_PROG003D0002Q','8f83e222-6639-11f1-bf8d-b59eb088d935','Y','admin','2026-06-15 13:05:04',NULL,NULL),
('c99d77bb-69f5-11f1-bebf-b7719b4a5893','MD_PROG006D0001Q','57b2fa07-6985-11f1-9ff0-6fc50c28457a','Y','admin','2026-06-17 10:39:39',NULL,NULL),
('c99e3b0c-69f5-11f1-bebf-9530db7d0080','MD_PROG006D0002Q','57b2fa07-6985-11f1-9ff0-6fc50c28457a','Y','admin','2026-06-17 10:39:39',NULL,NULL),
('c99eb03d-69f5-11f1-bebf-33ff7fb0fa78','MD_PROG006D0003Q','57b2fa07-6985-11f1-9ff0-6fc50c28457a','Y','admin','2026-06-17 10:39:39',NULL,NULL),
('c99f4c7e-69f5-11f1-bebf-7fea55a3c4fc','MD_PROG006D0004Q','57b2fa07-6985-11f1-9ff0-6fc50c28457a','Y','admin','2026-06-17 10:39:39',NULL,NULL),
('c99fc1af-69f5-11f1-bebf-b1337abc82f4','MD_PROG006D0005Q','57b2fa07-6985-11f1-9ff0-6fc50c28457a','Y','admin','2026-06-17 10:39:39',NULL,NULL),
('c9a035e0-69f5-11f1-bebf-81136f31c9d1','MD_PROG006D0006Q','57b2fa07-6985-11f1-9ff0-6fc50c28457a','Y','admin','2026-06-17 10:39:39',NULL,NULL),
('ed5d43f3-6a56-11f1-970f-fd796fa45e2f','MD_PROG007D0001Q','c14a7614-6a4b-11f1-bdd1-ed1d9f24b304','Y','admin','2026-06-17 22:15:00',NULL,NULL),
('ed5e0744-6a56-11f1-970f-1994bac14b23','MD_PROG007D0002Q','c14a7614-6a4b-11f1-bdd1-ed1d9f24b304','Y','admin','2026-06-17 22:15:00',NULL,NULL),
('ed5ef1a5-6a56-11f1-970f-61fba1e0d034','MD_PROG007D0003Q','c14a7614-6a4b-11f1-bdd1-ed1d9f24b304','Y','admin','2026-06-17 22:15:00',NULL,NULL),
('ed602a26-6a56-11f1-970f-37d9d1dcb3f6','MD_PROG007D0004Q','c14a7614-6a4b-11f1-bdd1-ed1d9f24b304','Y','admin','2026-06-17 22:15:00',NULL,NULL),
('ed60ed77-6a56-11f1-970f-1513e8c5d221','MD_PROG007D0005Q','c14a7614-6a4b-11f1-bdd1-ed1d9f24b304','Y','admin','2026-06-17 22:15:00',NULL,NULL),
('ed61d7d8-6a56-11f1-970f-5d69f79cefd8','MD_PROG007D0006Q','c14a7614-6a4b-11f1-bdd1-ed1d9f24b304','Y','admin','2026-06-17 22:15:00',NULL,NULL),
('f0242c17-4487-11ee-b50d-a593cf4a05bf','CORE_PROG001D0001Q','7ea68636-c93a-4669-ac42-dafc3770d20d','Y','admin','2023-08-27 11:15:13',NULL,NULL),
('f0253d88-4487-11ee-b50d-7f3d9b9812d0','CORE_PROG001D0002Q','7ea68636-c93a-4669-ac42-dafc3770d20d','Y','admin','2023-08-27 11:15:13',NULL,NULL),
('f0264ef9-4487-11ee-b50d-a55549dc8acf','CORE_PROG001D0003Q','7ea68636-c93a-4669-ac42-dafc3770d20d','Y','admin','2023-08-27 11:15:13',NULL,NULL),
('f027877a-4487-11ee-b50d-8fe1228e511a','CORE_PROG001D0004Q','7ea68636-c93a-4669-ac42-dafc3770d20d','Y','admin','2023-08-27 11:15:13',NULL,NULL),
('f02898eb-4487-11ee-b50d-45ee94442a45','CORE_PROG001D0005Q','7ea68636-c93a-4669-ac42-dafc3770d20d','Y','admin','2023-08-27 11:15:13',NULL,NULL),
('f07acfb8-4612-11ee-9a04-71984fef28fa','CORE_PROG004D0001Q','5e055f61-bfc5-402c-93b4-f241dc17b00b','Y','admin','2023-08-29 10:22:45',NULL,NULL),
('f07b9309-4612-11ee-9a04-9f3e4fe17b25','CORE_PROG004D0002Q','5e055f61-bfc5-402c-93b4-f241dc17b00b','Y','admin','2023-08-29 10:22:45',NULL,NULL),
('fad2fc9c-6f94-11f1-8db8-fdb6a36a0b30','MD_PROG010D0001Q','25b62ac5-6e3a-11f1-ada3-f96ece44675b','Y','admin','2026-06-24 14:21:47',NULL,NULL),
('fad398dd-6f94-11f1-8db8-c5479b253ba7','MD_PROG010D0002Q','25b62ac5-6e3a-11f1-ada3-f96ece44675b','Y','admin','2026-06-24 14:21:47',NULL,NULL);
/*!40000 ALTER TABLE `tb_sys_menu` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_menu_role`
--

DROP TABLE IF EXISTS `tb_sys_menu_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_menu_role` (
  `OID` char(36) NOT NULL,
  `PROG_ID` varchar(50) NOT NULL,
  `ROLE` varchar(50) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`PROG_ID`,`ROLE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_menu_role`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_menu_role` WRITE;
/*!40000 ALTER TABLE `tb_sys_menu_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_menu_role` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_prog`
--

DROP TABLE IF EXISTS `tb_sys_prog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_prog` (
  `OID` char(36) NOT NULL,
  `PROG_ID` varchar(50) NOT NULL,
  `NAME` varchar(100) NOT NULL,
  `URL` varchar(255) NOT NULL,
  `EDIT_MODE` varchar(1) NOT NULL DEFAULT 'N',
  `IS_DIALOG` varchar(1) NOT NULL DEFAULT 'N',
  `DIALOG_W` int(4) NOT NULL DEFAULT 0,
  `DIALOG_H` int(4) NOT NULL DEFAULT 0,
  `PROG_SYSTEM` varchar(10) NOT NULL,
  `ITEM_TYPE` varchar(10) NOT NULL,
  `ICON` varchar(20) NOT NULL,
  `FONT_ICON_CLASS_ID` varchar(100) NOT NULL DEFAULT 'circle-o',
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`PROG_ID`),
  KEY `IDX_1` (`PROG_SYSTEM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_prog`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_prog` WRITE;
/*!40000 ALTER TABLE `tb_sys_prog` DISABLE KEYS */;
INSERT INTO `tb_sys_prog` VALUES
('069f28b3-6a4e-11f1-a592-005056c00001','MD_PROG007D0002Q','AG02 - Strategy Theme','#/md_prog007d0002','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-2','admin','2026-06-17 21:11:00',NULL,NULL),
('069f2daa-6a4e-11f1-a592-005056c00001','MD_PROG007D0002A','AG02 - Strategy Theme (Create)','#/md_prog007d0002/create','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-2','admin','2026-06-17 21:11:00',NULL,NULL),
('069f2e0d-6a4e-11f1-a592-005056c00001','MD_PROG007D0002E','AG02 - Strategy Theme (Edit)','#/md_prog007d0002/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','diagram-2','admin','2026-06-17 21:11:00',NULL,NULL),
('078a5671-6558-11f1-8ea0-91de96b88a3d','MD_PROG001D0001E','AA01 - 組織單位 (編輯)','#/md_prog001d0001/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','house','admin','2026-06-11 13:40:18','admin','2026-06-11 13:42:11'),
('0f496e9f-698a-11f1-a592-005056c00001','MD_PROG006D0003Q','AF03 - OKR Key Result','#/md_prog006d0003','N','N',0,0,'CORE','ITEM','SYSTEM','list-check','admin','2026-06-16 21:48:17',NULL,NULL),
('0f497316-698a-11f1-a592-005056c00001','MD_PROG006D0003A','AF03 - OKR Key Result (新增)','#/md_prog006d0003/create','N','N',0,0,'CORE','ITEM','SYSTEM','list-check','admin','2026-06-16 21:48:17',NULL,NULL),
('0f4973cb-698a-11f1-a592-005056c00001','MD_PROG006D0003E','AF03 - OKR Key Result (編輯)','#/md_prog006d0003/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','list-check','admin','2026-06-16 21:48:17',NULL,NULL),
('132d76b3-6558-11f1-8ea0-ad52a9ef6410','MD_PROG001D0002Q','AA02 - 成員維護','#/md_prog001d0002','N','N',0,0,'CORE','ITEM','SYSTEM','person','admin','2026-06-11 13:40:37',NULL,NULL),
('186b1fb1-749f-4b6f-97d1-6b7fb8115345','CORE_PROG001D0004E','ZA04 - Freemarker樣板 (Edit)','#/prog001d0004/edit','Y','N',0,0,'CORE','ITEM','TEMPLATE','file-text','admin','2017-05-12 10:40:10','admin','2023-08-16 21:48:56'),
('1a12e638-6adc-11f1-a6b2-244bfee7c856','MD_PROG008D0005Q','AH05 - Action / PDCA Report','#/md_prog008d0005','N','N',0,0,'CORE','ITEM','SYSTEM','bar-chart-line','admin','2026-06-18 14:06:31',NULL,NULL),
('1b11c7eb-6133-48fb-87f0-dfbd098ce914','CORE_PROG001D0001E','ZA01 - System site (Edit)','#/prog001d0001/edit','Y','N',0,0,'CORE','ITEM','COMPUTER','globe2','admin','2014-10-02 00:00:00','admin','2021-01-20 08:20:58'),
('1e393fe3-8bbc-482c-aa23-bbb22a1dbafb','CORE_PROG001D0005A','ZA05 - JasperReport (Create)','#/prog001d0005/create','N','N',0,0,'CORE','ITEM','APPLICATION_PDF','file-pdf','admin','2017-05-18 09:55:46','admin','2023-08-24 20:20:27'),
('22560527-90fb-4e5a-a89b-353d2aa1d433','CORE_PROG001D0005E','ZA05 - JasperReport (Edit)','#/prog001d0005/edit','Y','N',0,0,'CORE','ITEM','APPLICATION_PDF','file-pdf','admin','2017-05-18 09:56:27','admin','2023-08-24 20:20:40'),
('25aabd77-6558-11f1-8ea0-49b47cb32597','MD_PROG001D0002E','AA02 - 成員維護 (編輯)','#/md_prog001d0002/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','person','admin','2026-06-11 13:41:08','admin','2026-06-11 13:42:20'),
('2e8bde04-b188-4440-8f27-15a75ef1a094','MD_PROG002D0002A','AB02 - 彙總方法 (建立)','#/md_prog002d0002/create','N','N',0,0,'CORE','ITEM','SYSTEM','calculator-fill','admin','2026-06-12 14:24:38',NULL,NULL),
('340d4480-6a52-11f1-a592-005056c00001','MD_PROG007D0004Q','AG04 - Strategy Objective Link','#/md_prog007d0004','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-2','admin','2026-06-17 21:40:54',NULL,NULL),
('340d4817-6a52-11f1-a592-005056c00001','MD_PROG007D0004A','AG04 - Strategy Objective Link (Create)','#/md_prog007d0004/create','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-2','admin','2026-06-17 21:40:54',NULL,NULL),
('340d487b-6a52-11f1-a592-005056c00001','MD_PROG007D0004E','AG04 - Strategy Objective Link (Edit)','#/md_prog007d0004/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','diagram-2','admin','2026-06-17 21:40:54',NULL,NULL),
('3630ee1b-6169-452f-821f-5c015dfb84d5','CORE_PROG001D','ZA. Config','/','N','N',0,0,'CORE','FOLDER','PROPERTIES','gear-fill','admin','2014-10-02 00:00:00','admin','2023-08-15 19:16:31'),
('3862b6d0-0551-45d8-8dd1-cd988a5e8e50','CORE_PROG004D0002Q','ZD02 - Token log','#/prog004d0002','N','N',0,0,'CORE','ITEM','PROPERTIES','clipboard-check','admin','2017-06-03 14:22:29','admin','2023-08-29 10:23:05'),
('3ca500d9-6558-11f1-8ea0-61333ff04564','MD_PROG001D0003Q','AA03 - 組織架構','#/md_prog001d0003','N','N',0,0,'CORE','ITEM','SYSTEM','house-fill','admin','2026-06-11 13:41:47',NULL,NULL),
('41fa29d8-3a53-4fbd-b2b1-cdbfd0729767','CORE_PROG001D0004Q','ZA04 - Freemarker樣板','#/prog001d0004','N','N',0,0,'CORE','ITEM','TEMPLATE','file-text','admin','2017-05-12 10:36:41','admin','2023-08-16 21:48:29'),
('467e94ad-6633-11f1-a6b2-244bfee7c856','MD_PROG002D0003Q','AB03 - 公式推薦規則','#/md_prog002d0003','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-3','admin','2026-06-12 15:48:30',NULL,NULL),
('467e9b5d-6633-11f1-a6b2-244bfee7c856','MD_PROG002D0003A','AB03 - 公式推薦規則 (建立)','#/md_prog002d0003/create','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-3','admin','2026-06-12 15:48:30',NULL,NULL),
('467e9bfe-6633-11f1-a6b2-244bfee7c856','MD_PROG002D0003E','AB03 - 公式推薦規則 (編輯)','#/md_prog002d0003/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','diagram-3','admin','2026-06-12 15:48:30',NULL,NULL),
('4fbcecfb-692f-11f1-a6b2-244bfee7c856','MD_PROG005D','AE. KPI Report','#','N','N',0,0,'CORE','FOLDER','SYSTEM','graph-up','admin','2026-06-16 10:57:26',NULL,NULL),
('4fbd001d-692f-11f1-a6b2-244bfee7c856','MD_PROG005D0001Q','AE01 - KPI Report','#/md_prog005d0001','N','N',0,0,'CORE','ITEM','SYSTEM','speedometer2','admin','2026-06-16 10:57:26',NULL,NULL),
('5a7d820f-6adf-11f1-a6b2-244bfee7c856','MD_PROG009D','AI. Dashboard','#','N','N',0,0,'CORE','FOLDER','SYSTEM','speedometer2','admin','2026-06-18 14:29:47',NULL,NULL),
('5c6f4387-6ab5-11f1-a6b2-244bfee7c856','MD_PROG008D','AH. Action / PDCA','#','N','N',0,0,'CORE','FOLDER','SYSTEM','clipboard-check','admin','2026-06-18 09:29:13',NULL,NULL),
('5c6f467a-6ab5-11f1-a6b2-244bfee7c856','MD_PROG008D0001Q','AH01 - Action Plan','#/md_prog008d0001','N','N',0,0,'CORE','ITEM','SYSTEM','clipboard-check','admin','2026-06-18 09:29:13',NULL,NULL),
('5c6f46d7-6ab5-11f1-a6b2-244bfee7c856','MD_PROG008D0001A','AH01 - Action Plan (Create)','#/md_prog008d0001/create','N','N',0,0,'CORE','ITEM','SYSTEM','clipboard-check','admin','2026-06-18 09:29:13',NULL,NULL),
('5c7200ab-6ab5-11f1-a6b2-244bfee7c856','MD_PROG008D0001E','AH01 - Action Plan (Edit)','#/md_prog008d0001/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','clipboard-check','admin','2026-06-18 09:29:13',NULL,NULL),
('5e082c7c-1730-4176-89c6-93e235707deb','CORE_PROG002D0001A','ZB01 - Role (Create)','#/prog002d0001/create','N','N',0,0,'CORE','ITEM','PEOPLE','person-square','admin','2017-05-09 11:15:50','admin','2023-08-27 16:46:40'),
('61aea7ff-7a42-4a92-9a0b-4a0dfe60858b','CORE_PROG004D0001Q','ZD01 - Event log','#/prog004d0001','N','N',0,0,'CORE','ITEM','PROPERTIES','clipboard-pulse','admin','2017-06-03 14:22:07','admin','2023-08-29 10:17:34'),
('6709eeb9-6605-11f1-a6b2-244bfee7c856','MD_PROG002D','AB. 計算規則','#','N','N',0,0,'CORE','FOLDER','SYSTEM','gear-wide','admin','2026-06-12 10:20:08','admin','2026-06-12 10:23:37'),
('670fcd02-6605-11f1-a6b2-244bfee7c856','MD_PROG002D0001Q','AB01 - Formula','#/md_prog002d0001','N','N',0,0,'CORE','ITEM','SYSTEM','calculator','admin','2026-06-12 10:20:08',NULL,NULL),
('67144d76-6605-11f1-a6b2-244bfee7c856','MD_PROG002D0001A','AB01 - Formula (建立)','#/md_prog002d0001/create','N','N',0,0,'CORE','ITEM','SYSTEM','calculator','admin','2026-06-12 10:20:08',NULL,NULL),
('67190e7a-6605-11f1-a6b2-244bfee7c856','MD_PROG002D0001E','AB01 - Formula (編輯)','#/md_prog002d0001/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','calculator','admin','2026-06-12 10:20:08',NULL,NULL),
('6a442973-0e0c-4a7a-d546-464f4ff5f7a9','CORE_PROG001D0003Q','ZA03 - Menu settings','#/prog001d0003','N','N',0,0,'CORE','ITEM','FOLDER','menu-down','admin','2014-10-02 00:00:00','admin','2023-08-15 19:21:23'),
('6b210525-8975-4fb5-954c-fe349f66d3fe','CORE_PROG002D0001S01Q','ZB01 - Role (permission)','#/prog002d0001/setparam','Y','N',0,0,'CORE','ITEM','IMPORTANT','globe2','admin','2017-05-09 14:32:47','admin','2021-01-20 08:48:52'),
('6d35fc74-6f93-11f1-a6b2-244bfee7c856','MD_PROG010D0002Q','AJ02 - Performance Signal','#/md_prog010d0002','N','N',0,0,'CORE','ITEM','SYSTEM','activity','admin','2026-06-24 14:08:27',NULL,NULL),
('6d3ae424-6f93-11f1-a6b2-244bfee7c856','MD_PROG010D0002U','AJ02 - Performance Signal (Generate)','#/md_prog010d0002','Y','N',0,0,'CORE','ITEM','SYSTEM','activity','admin','2026-06-24 14:08:27',NULL,NULL),
('6e6a8223-6639-11f1-a6b2-244bfee7c856','MD_PROG003D','AC. KPI','#','N','N',0,0,'CORE','FOLDER','SYSTEM','bullseye','admin','2026-06-12 16:32:33','admin','2026-06-12 16:34:25'),
('6e6a8224-6639-11f1-a6b2-1c8db4a91f11','MD_PROG003D0002Q','AC02 - KPI Score Color','#/md_prog003d0002','N','N',0,0,'CORE','ITEM','SYSTEM','palette','admin','2026-06-15 00:00:00',NULL,NULL),
('6e6a8225-6639-11f1-a6b2-52216b411fec','MD_PROG003D0002A','AC02 - KPI Score Color (Create)','#/md_prog003d0002/create','N','N',0,0,'CORE','ITEM','SYSTEM','palette','admin','2026-06-15 00:00:00',NULL,NULL),
('6e6a8226-6639-11f1-a6b2-91a91af488c1','MD_PROG003D0002E','AC02 - KPI Score Color (Edit)','#/md_prog003d0002/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','palette','admin','2026-06-15 00:00:00',NULL,NULL),
('72e6e0d1-1818-47d3-99f9-5134fb211b79','CORE_PROG002D','ZB. Role authority','/','N','N',0,0,'CORE','FOLDER','SHARED','person-square','admin','2017-05-08 21:27:52','admin','2023-08-27 16:47:03'),
('7746f746-961f-44c2-9b66-fa43c0f49838','CORE_PROG001D0004S01Q','ZA04 - Freemarker樣板 (Parameter)','#/prog001d0004/setparam','Y','N',0,0,'CORE','ITEM','TEMPLATE','file-text','admin','2017-05-12 10:42:04','admin','2023-08-16 21:49:12'),
('7cf62919-6a4b-11f1-a592-005056c00001','MD_PROG007D','AG. Strategy / BSC','#','N','N',0,0,'CORE','FOLDER','SYSTEM','diagram-2','admin','2026-06-17 20:52:50',NULL,NULL),
('7cf62ca5-6a4b-11f1-a592-005056c00001','MD_PROG007D0001Q','AG01 - Strategy Workspace','#/md_prog007d0001','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-2','admin','2026-06-17 20:52:50',NULL,NULL),
('7cf62d05-6a4b-11f1-a592-005056c00001','MD_PROG007D0001A','AG01 - Strategy Workspace (Create)','#/md_prog007d0001/create','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-2','admin','2026-06-17 20:52:50',NULL,NULL),
('7cf62d5f-6a4b-11f1-a592-005056c00001','MD_PROG007D0001E','AG01 - Strategy Workspace (Edit)','#/md_prog007d0001/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','diagram-2','admin','2026-06-17 20:52:50',NULL,NULL),
('7d9ddc45-3eab-4f61-8c0a-d5505c0cc748','CORE_PROG001D0004A','ZA04 - Freemarker樣板 (Create)','#/prog001d0004/create','N','N',0,0,'CORE','ITEM','TEMPLATE','file-text','admin','2017-05-12 10:39:20','admin','2023-08-16 21:48:49'),
('7f2dc78f-6558-11f1-8ea0-97f9dc9b5779','MD_PROG001D0002A','AA02 - 成員維護 (建立)','#/md_prog001d0002/create','N','N',0,0,'CORE','ITEM','SYSTEM','person','admin','2026-06-11 13:43:39',NULL,NULL),
('82030d69-69f0-11f1-a6b2-244bfee7c856','MD_PROG006D0005Q','AF05 - OKR Snapshot','#/md_prog006d0005','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-3','admin','2026-06-17 10:00:13',NULL,NULL),
('8499957e-6da9-4160-c2ec-dfb7dbc202fe','CORE_PROG001D0002E','ZA02 - Program (Edit)','#/prog001d0002/edit','Y','N',0,0,'CORE','ITEM','G_APP_INSTALL','filetype-html','admin','2014-10-02 00:00:00','admin','2023-08-15 19:19:17'),
('84af69c2-69f4-11f1-a6b2-244bfee7c856','MD_PROG006D0006Q','AF06 - OKR Report','#/md_prog006d0006','N','N',0,0,'CORE','ITEM','SYSTEM','bar-chart-line','admin','2026-06-17 10:28:55',NULL,NULL),
('87f9ae6a-1dd2-4585-b31d-9533bdda8fd5','CORE_PROG005D0001Q','ZE01 - MQTT Dashboard','#/prog005d0001','N','N',0,0,'CORE','ITEM','PROPERTIES','speedometer2','admin','2026-06-04 11:00:54',NULL,NULL),
('8d020001-6ab5-11f1-a6b2-244bfee7c856','MD_PROG008D0002Q','AH02 - Action Item','#/md_prog008d0002','N','N',0,0,'CORE','ITEM','SYSTEM','list-check','admin','2026-06-18 13:40:30',NULL,NULL),
('8d020002-6ab5-11f1-a6b2-244bfee7c856','MD_PROG008D0002A','AH02 - Action Item (Create)','#/md_prog008d0002/create','N','N',0,0,'CORE','ITEM','SYSTEM','list-check','admin','2026-06-18 13:40:30','admin','2026-06-18 13:42:29'),
('8d020003-6ab5-11f1-a6b2-244bfee7c856','MD_PROG008D0002E','AH02 - Action Item (Edit)','#/md_prog008d0002/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','list-check','admin','2026-06-18 13:40:30',NULL,NULL),
('a6643ff5-6a4f-11f1-a592-005056c00001','MD_PROG007D0003Q','AG03 - Strategy Objective','#/md_prog007d0003','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-2','admin','2026-06-17 21:22:38',NULL,NULL),
('a66444a4-6a4f-11f1-a592-005056c00001','MD_PROG007D0003A','AG03 - Strategy Objective (Create)','#/md_prog007d0003/create','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-2','admin','2026-06-17 21:22:38',NULL,NULL),
('a664450b-6a4f-11f1-a592-005056c00001','MD_PROG007D0003E','AG03 - Strategy Objective (Edit)','#/md_prog007d0003/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','diagram-2','admin','2026-06-17 21:22:38',NULL,NULL),
('a8f4e94f-6a56-11f1-a592-005056c00001','MD_PROG007D0006Q','AG06 - Strategy / BSC Report','#/md_prog007d0006','N','N',0,0,'CORE','ITEM','SYSTEM','bar-chart-line','admin','2026-06-17 22:12:49',NULL,NULL),
('ac5bcfd0-4abd-11e4-916c-0800200c9a66','CORE_PROG001D0001A','ZA01 - System site (Create)','#/prog001d0001/create','N','N',0,0,'CORE','ITEM','COMPUTER','globe2','admin','2014-10-02 00:00:00','admin','2021-01-20 08:20:45'),
('aee910f9-6987-11f1-a592-005056c00001','MD_PROG006D0002Q','AF02 - OKR Objective','#/md_prog006d0002','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-3','admin','2026-06-16 21:31:16',NULL,NULL),
('aee91485-6987-11f1-a592-005056c00001','MD_PROG006D0002A','AF02 - OKR Objective (新增)','#/md_prog006d0002/create','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-3','admin','2026-06-16 21:31:16',NULL,NULL),
('aee914e3-6987-11f1-a592-005056c00001','MD_PROG006D0002E','AF02 - OKR Objective (編輯)','#/md_prog006d0002/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','diagram-3','admin','2026-06-16 21:31:16',NULL,NULL),
('b1817817-6a56-11f1-a592-005056c00001','MD_PROG007D0005Q','AG05 - Strategy Snapshot Evidence','#/md_prog007d0005','N','N',0,0,'CORE','ITEM','SYSTEM','diagram-3','admin','2026-06-17 22:13:03',NULL,NULL),
('b39159ad-0707-4515-b78d-e3fc72c53974','CORE_PROG002D0001E','ZB01 - Role (Edit)','#/prog002d0001/edit','Y','N',0,0,'CORE','ITEM','PEOPLE','person-square','admin','2017-05-09 12:11:53','admin','2023-08-27 16:46:35'),
('b6b89559-6864-46ab-9ca9-0992dcf238f1','CORE_PROG001D0001Q','ZA01 - System site','#/prog001d0001','N','N',0,0,'CORE','ITEM','COMPUTER','globe2','admin','2014-10-02 00:00:00','admin','2021-01-20 08:20:29'),
('b978f706-4c5f-40f8-83b1-395492f141d4','CORE_PROG002D0001Q','ZB01 - Role','#/prog002d0001','N','N',0,0,'CORE','ITEM','PEOPLE','person-square','admin','2017-05-08 21:32:50','admin','2023-08-27 16:46:27'),
('bdcbb7ad-6e39-11f1-a592-005056c00001','MD_PROG010D','AJ. Insight / LLM','#','N','N',0,0,'CORE','FOLDER','SYSTEM','robot','admin','2026-06-22 20:55:33',NULL,NULL),
('bdcbc027-6e39-11f1-a592-005056c00001','MD_PROG010D0001Q','AJ01 - LLM Provider Config / Run Log','#/md_prog010d0001','N','N',0,0,'CORE','ITEM','SYSTEM','robot','admin','2026-06-22 20:55:33',NULL,NULL),
('bdcbed01-6e39-11f1-a592-005056c00001','MD_PROG010D0001A','AJ01 - LLM Provider Config (Create)','#/md_prog010d0001/create','N','N',0,0,'CORE','ITEM','SYSTEM','robot','admin','2026-06-22 20:55:33',NULL,NULL),
('bdcbeeed-6e39-11f1-a592-005056c00001','MD_PROG010D0001E','AJ01 - LLM Provider Config (Edit)','#/md_prog010d0001/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','robot','admin','2026-06-22 20:55:33',NULL,NULL),
('bfeb7935-334f-4a94-9666-f77433793a8a','CORE_PROG005D','ZE. MQTT','/','N','N',0,0,'CORE','FOLDER','PROPERTIES','gear-fill','admin','2026-06-04 11:00:54',NULL,NULL),
('c71ea272-6ade-11f1-a6b2-244bfee7c856','MD_PROG009D0001Q','AI01 - Management Dashboard','#/md_prog009d0001','N','N',0,0,'CORE','ITEM','SYSTEM','speedometer2','admin','2026-06-18 14:25:40',NULL,NULL),
('c96ebde8-7044-4b05-a155-68a0c2605619','CORE_PROG002D0003Q','ZB03 - Role for menu','#/prog002d0003','N','N',0,0,'CORE','ITEM','FOLDER','menu-app-fill','admin','2017-05-08 21:37:01','admin','2023-08-28 19:54:34'),
('c9d50c9d-6984-11f1-a592-005056c00001','MD_PROG006D','AF. OKR','#','N','N',0,0,'CORE','FOLDER','SYSTEM','diagram-3','admin','2026-06-16 21:10:33',NULL,NULL),
('c9d51115-6984-11f1-a592-005056c00001','MD_PROG006D0001Q','AF01 - OKR Cycle','#/md_prog006d0001','N','N',0,0,'CORE','ITEM','SYSTEM','calendar3','admin','2026-06-16 21:10:33',NULL,NULL),
('c9d511cc-6984-11f1-a592-005056c00001','MD_PROG006D0001A','AF01 - OKR Cycle (新增)','#/md_prog006d0001/create','N','N',0,0,'CORE','ITEM','SYSTEM','calendar3','admin','2026-06-16 21:10:33',NULL,NULL),
('c9d51275-6984-11f1-a592-005056c00001','MD_PROG006D0001E','AF01 - OKR Cycle (編輯)','#/md_prog006d0001/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','calendar3','admin','2026-06-16 21:10:33',NULL,NULL),
('da69253b-6557-11f1-8ea0-3792e4bd3499','MD_PROG001D','AA. 基本資料','#','N','N',0,0,'CORE','FOLDER','SYSTEM','folder','admin','2026-06-11 13:39:02',NULL,NULL),
('da7d969a-5efb-4e84-9eab-4fdae236f28c','CORE_PROG002D0002Q','ZB02 - User role','#/prog002d0002','N','N',0,0,'CORE','ITEM','PERSON','person-check','admin','2017-05-08 21:34:39','admin','2023-08-28 19:54:25'),
('dda67b1d-e3a2-4534-835a-c62d9e8421f3','CORE_PROG001D0005S01Q','ZA05 - JasperReport (Parameter)','#/prog001d0005/setparam','Y','N',0,0,'CORE','ITEM','APPLICATION_PDF','file-pdf','admin','2017-05-18 09:57:26','admin','2023-08-24 20:21:02'),
('dffe7fc8-6638-11f1-a6b2-244bfee7c856','MD_PROG003D0001Q','AC01 - KPI 基本資料','#/md_prog003d0001','N','N',0,0,'CORE','ITEM','SYSTEM','bullseye','admin','2026-06-12 16:28:34',NULL,NULL),
('dffe854b-6638-11f1-a6b2-244bfee7c856','MD_PROG003D0001A','AC01 - KPI 基本資料 (新增)','#/md_prog003d0001/create','N','N',0,0,'CORE','ITEM','SYSTEM','bullseye','admin','2026-06-12 16:28:34',NULL,NULL),
('dffe85df-6638-11f1-a6b2-244bfee7c856','MD_PROG003D0001E','AC01 - KPI 基本資料 (編輯)','#/md_prog003d0001/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','bullseye','admin','2026-06-12 16:28:34',NULL,NULL),
('e183a86a-6673-11f1-a592-005056c00001','MD_PROG004D','AD. KPI Measure data','#','N','N',0,0,'CORE','FOLDER','SYSTEM','clipboard-data','admin','2026-06-12 23:32:11','admin','2026-06-12 23:33:49'),
('e185a08a-6673-11f1-a592-005056c00001','MD_PROG004D0001Q','AD01 - Measure Data input','#/md_prog004d0001','N','N',0,0,'CORE','ITEM','SYSTEM','clipboard-data','admin','2026-06-12 23:32:11','admin','2026-06-12 23:34:23'),
('e32b9329-bb38-46d7-8552-2307bac77724','CORE_PROG001D0002A','ZA02 - Program (Create)','#/prog001d0002/create','N','N',0,0,'CORE','ITEM','G_APP_INSTALL','filetype-html','admin','2014-10-02 00:00:00','admin','2023-08-15 19:19:42'),
('e5faf3e8-3c61-43cf-b748-d60dd6f34b6d','MD_PROG002D0002Q','AB02 - 彙總方法','#/md_prog002d0002','N','N',0,0,'CORE','ITEM','SYSTEM','calculator-fill','admin','2026-06-12 14:24:38',NULL,NULL),
('e86dbb1b-6870-4827-8039-72f5e15fa4f2','CORE_PROG004D','ZD. Log','/','N','N',0,0,'CORE','FOLDER','PROPERTIES','clipboard-check-fill','admin','2017-06-03 14:21:03','admin','2023-08-29 10:14:04'),
('eb6e199f-c853-4fbf-acf3-0c9c77ba9953','CORE_PROG001D0002Q','ZA02 - Program','#/prog001d0002','N','N',0,0,'CORE','ITEM','G_APP_INSTALL','filetype-html','admin','2014-10-02 00:00:00','admin','2023-08-15 19:19:05'),
('eb786ffd-c7d1-4631-aed2-4d9d7368eb13','CORE_PROG001D0005Q','ZA05 - JasperReport','#/prog001d0005','N','N',0,0,'CORE','ITEM','APPLICATION_PDF','file-pdf','admin','2017-05-18 09:54:35','admin','2023-08-24 20:20:16'),
('eb8ecb3d-6557-11f1-8ea0-a313029ec7da','MD_PROG001D0001Q','AA01 - 組織單位','#/md_prog001d0001','N','N',0,0,'CORE','ITEM','SYSTEM','house','admin','2026-06-11 13:39:31',NULL,NULL),
('f2f984fa-963c-42ed-b281-2047b863399d','MD_PROG002D0002E','AB02 - 彙總方法 (編輯)','#/md_prog002d0002/edit','Y','N',0,0,'CORE','ITEM','SYSTEM','calculator-fill','admin','2026-06-12 14:24:38',NULL,NULL),
('f50f2063-698c-11f1-a592-005056c00001','MD_PROG006D0004Q','AF04 - OKR Check-in','#/md_prog006d0004','N','N',0,0,'CORE','ITEM','SYSTEM','clipboard-check','admin','2026-06-16 22:09:01',NULL,NULL),
('f50f2490-698c-11f1-a592-005056c00001','MD_PROG006D0004A','AF04 - OKR Check-in (新增)','#/md_prog006d0004','N','N',0,0,'CORE','ITEM','SYSTEM','clipboard-check','admin','2026-06-16 22:09:01',NULL,NULL),
('f9ed13df-6557-11f1-8ea0-ebb67d5fab5b','MD_PROG001D0001A','AA01 - 組織單位 (建立)','#/md_prog001d0001/create','N','N',0,0,'CORE','ITEM','SYSTEM','house','admin','2026-06-11 13:39:55',NULL,NULL);
/*!40000 ALTER TABLE `tb_sys_prog` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_qfield_log`
--

DROP TABLE IF EXISTS `tb_sys_qfield_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_qfield_log` (
  `OID` char(36) NOT NULL,
  `SYSTEM` varchar(10) NOT NULL,
  `PROG_ID` varchar(50) NOT NULL,
  `METHOD_NAME` varchar(255) NOT NULL,
  `FIELD_NAME` varchar(255) NOT NULL,
  `FIELD_VALUE` varchar(500) DEFAULT NULL,
  `QUERY_USER_ID` varchar(24) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  KEY `IDX_1` (`SYSTEM`,`PROG_ID`),
  KEY `IDX_2` (`QUERY_USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_qfield_log`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_qfield_log` WRITE;
/*!40000 ALTER TABLE `tb_sys_qfield_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_qfield_log` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_template`
--

DROP TABLE IF EXISTS `tb_sys_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_template` (
  `OID` char(36) NOT NULL,
  `TEMPLATE_ID` varchar(10) NOT NULL,
  `TITLE` varchar(200) NOT NULL,
  `MESSAGE` varchar(4000) NOT NULL,
  `DESCRIPTION` varchar(200) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`TEMPLATE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_template`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_template` WRITE;
/*!40000 ALTER TABLE `tb_sys_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_template` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_template_param`
--

DROP TABLE IF EXISTS `tb_sys_template_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_template_param` (
  `OID` char(36) NOT NULL,
  `TEMPLATE_ID` varchar(10) NOT NULL,
  `IS_TITLE` varchar(1) NOT NULL DEFAULT 'N',
  `TEMPLATE_VAR` varchar(100) NOT NULL,
  `OBJECT_VAR` varchar(100) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`TEMPLATE_ID`,`TEMPLATE_VAR`,`IS_TITLE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_template_param`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_template_param` WRITE;
/*!40000 ALTER TABLE `tb_sys_template_param` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_template_param` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_token`
--

DROP TABLE IF EXISTS `tb_sys_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_token` (
  `OID` char(36) NOT NULL,
  `USER_ID` varchar(24) NOT NULL,
  `TOKEN` varchar(2048) NOT NULL,
  `EXPIRES_DATE` datetime NOT NULL,
  `RF_EXPIRES_DATE` datetime NOT NULL,
  `CDATE` datetime NOT NULL,
  PRIMARY KEY (`OID`),
  KEY `IDX_1` (`USER_ID`),
  KEY `IDX_2` (`TOKEN`(1024))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_token`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_token` WRITE;
/*!40000 ALTER TABLE `tb_sys_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_token` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_upload`
--

DROP TABLE IF EXISTS `tb_sys_upload`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_upload` (
  `OID` char(36) NOT NULL,
  `SYSTEM` varchar(10) NOT NULL,
  `SUB_DIR` varchar(4) NOT NULL,
  `TYPE` varchar(10) NOT NULL,
  `FILE_NAME` varchar(50) NOT NULL,
  `SHOW_NAME` varchar(255) NOT NULL,
  `IS_FILE` varchar(1) NOT NULL DEFAULT 'Y',
  `CONTENT` mediumblob DEFAULT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  KEY `IDX_1` (`SYSTEM`,`TYPE`,`SUB_DIR`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_upload`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_upload` WRITE;
/*!40000 ALTER TABLE `tb_sys_upload` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_upload` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_sys_usess`
--

DROP TABLE IF EXISTS `tb_sys_usess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sys_usess` (
  `OID` char(36) NOT NULL,
  `SESSION_ID` varchar(64) NOT NULL,
  `ACCOUNT` varchar(24) NOT NULL,
  `CURRENT_ID` varchar(36) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(24) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`,`SESSION_ID`),
  UNIQUE KEY `UK_1` (`ACCOUNT`,`SESSION_ID`),
  KEY `IDX_1` (`CURRENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sys_usess`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_sys_usess` WRITE;
/*!40000 ALTER TABLE `tb_sys_usess` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sys_usess` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;

--
-- Table structure for table `tb_user_role`
--

DROP TABLE IF EXISTS `tb_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_user_role` (
  `OID` char(36) NOT NULL,
  `ROLE` varchar(50) NOT NULL,
  `ACCOUNT` varchar(24) NOT NULL,
  `DESCRIPTION` varchar(500) NOT NULL,
  `CUSERID` varchar(24) NOT NULL,
  `CDATE` datetime NOT NULL,
  `UUSERID` varchar(50) DEFAULT NULL,
  `UDATE` datetime DEFAULT NULL,
  PRIMARY KEY (`OID`),
  UNIQUE KEY `UK_1` (`ROLE`,`ACCOUNT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_user_role`
--

SET @OLD_AUTOCOMMIT=@@AUTOCOMMIT, @@AUTOCOMMIT=0;
LOCK TABLES `tb_user_role` WRITE;
/*!40000 ALTER TABLE `tb_user_role` DISABLE KEYS */;
INSERT INTO `tb_user_role` VALUES
('1c62cf70-ca6b-4243-8aa9-49b555024c45','COMMON01','steven','','admin','2017-05-10 14:19:58',NULL,NULL),
('8f62e793-6601-11f1-ae4f-ab27d6ae0e59','COMMON01','peter','Auto-created for org member','admin','2026-06-12 09:53:51',NULL,NULL),
('9243c7de-43b1-46ef-ac4b-2620697f319e','admin','admin','Administrator','admin','2014-09-23 00:00:00',NULL,NULL),
('a3d8caa3-45a8-11ee-b979-e9dd94b50b2d','COMMON01','tiffany','','admin','2023-08-28 21:41:50',NULL,NULL),
('bd7bf78c-d84b-4524-8273-273f883d30b5','COMMON01','tester','','admin','2017-05-10 11:01:50',NULL,NULL);
/*!40000 ALTER TABLE `tb_user_role` ENABLE KEYS */;
UNLOCK TABLES;
COMMIT;
SET AUTOCOMMIT=@OLD_AUTOCOMMIT;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2026-06-24 14:24:30

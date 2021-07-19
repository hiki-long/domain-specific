-- MySQL dump 10.13  Distrib 5.7.34, for Linux (x86_64)
--
-- Host: localhost    Database: domain-specific
-- ------------------------------------------------------
-- Server version	5.7.34

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
-- Current Database: `domain-specific`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `domain-specific` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `domain-specific`;

--
-- Table structure for table `bill`
--

DROP TABLE IF EXISTS `bill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bill` (
  `uuid` varchar(100) CHARACTER SET utf8 NOT NULL,
  `orderuuid` varchar(100) CHARACTER SET utf8 NOT NULL,
  `price` float NOT NULL DEFAULT '0',
  `type` varchar(100) CHARACTER SET utf8 NOT NULL,
  `payment` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item` (
  `UUID` varchar(100) CHARACTER SET utf8 NOT NULL,
  `name` varchar(100) CHARACTER SET utf8 NOT NULL,
  `owner` varchar(100) CHARACTER SET utf8 NOT NULL,
  `remain` int(11) NOT NULL,
  `description` varchar(10000) CHARACTER SET utf8 NOT NULL DEFAULT 'There is no description',
  `type` varchar(100) CHARACTER SET utf8 DEFAULT '[others]',
  `onSale` tinyint(1) NOT NULL,
  `image` varchar(10000) CHARACTER SET utf8 DEFAULT NULL,
  `price` double NOT NULL DEFAULT '0.01',
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orderlist`
--

DROP TABLE IF EXISTS `orderlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orderlist` (
  `UUID` varchar(100) CHARACTER SET utf8 NOT NULL,
  `items` varchar(1000) CHARACTER SET utf8 NOT NULL,
  `seller` varchar(100) CHARACTER SET utf8 NOT NULL,
  `buyer` varchar(100) CHARACTER SET utf8 NOT NULL,
  `price` double NOT NULL,
  `bill` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `delivery` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `time` datetime NOT NULL,
  `paid` tinyint(1) NOT NULL DEFAULT '0',
  `finish` varchar(1000) NOT NULL DEFAULT '0',
  `comment` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `uuid` varchar(100) CHARACTER SET utf8 NOT NULL,
  `email` varchar(100) CHARACTER SET utf8 NOT NULL,
  `username` varchar(100) CHARACTER SET utf8 NOT NULL,
  `role` varchar(100) CHARACTER SET utf8 NOT NULL,
  `avatar` varchar(100) CHARACTER SET utf8 NOT NULL,
  `rank` float NOT NULL,
  `passwd` varchar(100) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wishlist`
--

DROP TABLE IF EXISTS `wishlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wishlist` (
  `owner` varchar(100) CHARACTER SET utf8 NOT NULL,
  `items` varchar(10000) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`owner`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-07-19 22:28:09

-- =============================================================================
-- Diagram Name: jamberoo
-- Created on: 25/11/2014 4:47:00 PM
-- Diagram Version: 1.0
-- =============================================================================
DROP DATABASE IF EXISTS `jamberoo`;

-- ------------------------------------------------------------
-- Description:
-- Chemistry structural database 
-- Version 1.0
-- ------------------------------------------------------------

-- ------------------------------------------------------------
-- Annotation:
-- Version 1.0
-- ------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS `jamberoo` 
CHARACTER SET utf8 
COLLATE utf8_general_ci;

USE `jamberoo`;

SET FOREIGN_KEY_CHECKS=0;

-- Drop table StructureTypes
DROP TABLE IF EXISTS `StructureTypes`;

CREATE TABLE `StructureTypes` (
  `type` char(25) NOT NULL DEFAULT 'Unknown',
  PRIMARY KEY(`type`)
)
ENGINE=INNODB;

insert into `StructureTypes` (`type`) values
('Minimum'),
('Local minimum'),
('Global minimum'),
('Transition state');

-- Drop table GeneralPropertiesTypes
DROP TABLE IF EXISTS `GeneralPropertiesTypes`;

CREATE TABLE `GeneralPropertiesTypes` (
  `propertyName` char(50) NOT NULL,
  `description` varchar(255),
  PRIMARY KEY(`propertyName`)
)
ENGINE=INNODB;

-- Drop table PropertyTypes
DROP TABLE IF EXISTS `PropertyTypes`;

CREATE TABLE `PropertyTypes` (
  `propertyName` char(50) NOT NULL,
  `description` varchar(255),
  PRIMARY KEY(`propertyName`)
)
ENGINE=INNODB;

-- Drop table Elements
DROP TABLE IF EXISTS `Elements`;

CREATE TABLE `Elements` (
  `name` char(50) NOT NULL,
  `symbol` char(10) NOT NULL,
  `atomicNumber` int(11) UNSIGNED NOT NULL,
  `standardAtomicWeight` double(15,3) UNSIGNED NOT NULL,
  PRIMARY KEY(`name`)
)
ENGINE=INNODB;

insert into `Elements` (`name`, `symbol`,`atomicNumber`,`standardAtomicWeight`) values
('Hydrogen','H',1,1.008),
('Helium','He',2,4.002602),
('Lithium','Li',3,6.94),
('Beryllium','Be',4,9.0121831),
('Boron','B',5,10.81),
('Carbon','C',6,12.011),
('Nitrogen','N',7,14.007),
('Oxygen','O',8,15.999),
('Fluorine','F',9,18.998403163),
('Neon','Ne',10,20.1797),
('Sodium','Na',11,22.98976928),
('Magnesium','Mg',12,24.305),
('Aluminium','Al',13,26.9815385),
('Silicon','Si',14,28.085),
('Phosphorus','P',15,30.973761998),
('Sulfur','S',16,32.06),
('Chlorine','Cl',17,35.45),
('Argon','Ar',18,39.948),
('Potassium','K',19,39.0983),
('Calcium','Ca',20,40.08),
('Scandium','Sc',21,44.9559),
('Titanium','Ti',22,47.9),
('Vanadium','V',23,50.9415),
('Chromium','Cr',24,51.996),
('Manganese','Mn',25,54.938),
('Iron','Fe',26,55.847),
('Cobalt','Co',27,58.9332),
('Nickel','Ni',28,58.71),
('Copper','Cu',29,63.546),
('Zinc','Zn',30,65.38),
('Gallium','Ga',31,65.735),
('Germanium','Ge',32,72.59),
('Arsenic','As',33,74.9216),
('Selenium','Se',34,78.96),
('Bromine','Br',35,79.904),
('Krypton','Kr',36,83.8),
('Rubidium','Rb',37,85.467),
('Strontium','Sr',38,87.62),
('Yttrium','Y',39,88.9059),
('Zirconium','Zr',40,91.22),
('Niobium','Nb',41,92.9064),
('Molybdenum','Mo',42,95.94),
('Technetium','Tc',43,98.9062),
('Ruthenium','Ru',44,101.07),
('Rhodium','Rh',45,102.9055),
('Palladium','Pd',46,106.4),
('Silver','Ag',47,107.868),
('Cadmium','Cd',48,112.41),
('Indium','In',49,114.82),
('Tin','Sn',50,118.69),
('Antimony','Sb',51,121.75),
('Tellurium','Te',52,127.6),
('Iodine','I',53,126.9045),
('Xenon','Xe',54,131.3),
('Cesium','Cs',55,132.9054),
('Barium','Ba',56,137.33),
('Lanthanum','La',57,138.9055),
('Cerium','Ce',58,140.12),
('Praseodymium','Pr',59,140.9077),
('Neodymium','Nd',60,144.24),
('Promethium','Pm',61,145.0),
('Samarium','Sm',62,150.4),
('Europium','Eu',63,151.96),
('Gadolinium','Gd',64,157.25),
('Terbium','Tb',65,158.9254),
('Dysprosium','Dy',66,162.5),
('Holmium','Ho',67,164.9304),
('Erbium','Er',68,167.26),
('Thulium','Tm',69,168.9342),
('Ytterbium','Yb',70,173.04),
('Lutetium','Lu',71,174.967),
('Hafnium','Hf',72,178.49),
('Tantalum','Ta',73,180.9479),
('Tungsten','W',74,183.85),
('Rhenium','Re',75,186.207),
('Osmium','Os',76,190.2),
('Iridium','Ir',77,192.22),
('Platinum','Pt',78,195.09),
('Gold','Au',79,196.9665),
('Mercury','Hg',80,200.59),
('Thallium','Tl',81,204.37),
('Lead','Pb',82,207.2),
('Bismuth','Bi',83,208.9804),
('Polonium','Po',84,209.0),
('Astatine','At',85,210.0),
('Radon','Rn',86,222.0),
('Francium','Fr',87,223.0),
('Radium','Ra',88,226.0254),
('Actinium','Ac',89,227.0),
('Thorium','Th',90,232.0381),
('Protactinium','Pa',91,231.0359),
('Uranium','U',92,238.029),
('Neptunium','Np',93,237.0482),
('Plutonium','Pu',94,244.0),
('Americium','Am',95,243.0),
('Curium','Cm',96,247.0),
('Berkelium','Bk',97,247.0),
('Californium','Cf',98,251.0),
('Einsteinium','Es',99,254.0),
('Fermium','Fm',100,257.0),
('Mendelevium','Md',101,258.0),
('Nobelium','No',102,259.0),
('Lawrencium','Lr',103,260.0),
('Rutherfordium','Rf',104,260.0),
('Dubnium','Db',105,260.0),
('Seaborgium','Sg',106,266.0),
('Bohrium','Bh',107,261.0),
('Hahnium','Hn',108,264.0),
('Meitnerium','Mt',109,266.0);

-- Drop table Compounds
DROP TABLE IF EXISTS `Compounds`;

CREATE TABLE `Compounds` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` char(255) NOT NULL,
  `description` varchar(1024) DEFAULT ' ',
  `genericWeight` float(9,3) UNSIGNED DEFAULT '0.0',
  `genericFormula` char(50) NOT NULL,
  `generic3dStructure` longtext,
  `numberAtoms` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY(`id`)
)
ENGINE=INNODB;

-- Drop table Methods
DROP TABLE IF EXISTS `Methods`;

CREATE TABLE `Methods` (
  `methodName` varchar(100) NOT NULL,
  `description` varchar(512) DEFAULT ' ',
  PRIMARY KEY(`methodName`)
)
ENGINE=INNODB;

insert into `Methods` (`methodName`, `description`) values
('HF/3-21G','Hartree-Fock method using 3-21G basis set'),
('HF/3-21G//IEFPCM=WATER','Hartree-Fock method using 3-21G basis set + default Gaussian PCM method (scrf=(solvent=water)) with water as a solvent'),
('HF/6-31G','Hartree-Fock method using 6-31G basis set'),
('HF/6-31G//IEFPCM=WATER','Hartree-Fock method using 6-31G basis set + default Gaussian PCM method (scrf=(solvent=water)) with water as a solvent'),
('B3LYP/6-31G(d,p)','DFT B3LYP method using 6-31G(d,p) basis set'),
('B3LYP/6-31G(d,p)//IEFPCM=WATER','DFT B3LYP method using 6-31G(d,p) basis set + default Gaussian PCM method (scrf=(solvent=water)) with water as a solvent'),
('B3LYP/aug-cc-pVDZ','DFT B3LYP method using aug-cc-pVDZ basis set'),
('B3LYP/aug-cc-pVDZ//IEFPCM=WATER','DFT B3LYP method using aug-cc-pVDZ basis set + default Gaussian PCM method (scrf=(solvent=water)) with water as a solvent'),
('B3LYP/aug-cc-pVTZ','DFT B3LYP method using aug-cc-pVTZ basis set'),
('B3LYP/aug-cc-pVTZ//IEFPCM=WATER','DFT B3LYP method using aug-cc-pVTZ basis set + default Gaussian PCM method (scrf=(solvent=water)) with water as a solvent'),
('B3LYP/aug-cc-pVQZ','DFT B3LYP method using aug-cc-pVQZ basis set'),
('B3LYP/aug-cc-pVQZ//IEFPCM=WATER','DFT B3LYP method using aug-cc-pVQZ basis set + default Gaussian PCM method (scrf=(solvent=water)) with water as a solvent'),
('CAM-B3LYP/aug-cc-pVTZ/Def2TZV','DFT CAM-B3LYP method using aug-cc-pVTZ basis set and Def2TZV as a fitting b.s.'),
('CAM-B3LYP/aug-cc-pVTZ/Def2TZV//IEFPCM=WATER','DFT CAM-B3LYP method using aug-cc-pVTZ basis set and Def2TZV as a fitting b.s. + default Gaussian PCM method (scrf=(solvent=water)) with water as a solvent'),
('MP2/aug-cc-pVDZ','MP2 method using aug-cc-pVDZ basis set'),
('MP2/aug-cc-pVDZ//IEFPCM=WATER','MP2 method using aug-cc-pVDZ basis set + default Gaussian PCM method (scrf=(solvent=water)) with water as a solvent'),
('MP2/aug-cc-pVTZ','MP2 method using aug-cc-pVTZ basis set'),
('MP2/aug-cc-pVTZ//IEFPCM=WATER','MP2 method using aug-cc-pVTZ basis set + default Gaussian PCM method (scrf=(solvent=water)) with water as a solvent'),
('Unknown','Unknown method');

-- Drop table Aliases
DROP TABLE IF EXISTS `Aliases`;

CREATE TABLE `Aliases` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `aliasName` varchar(50) NOT NULL,
  `description` varchar(255),
  `idCompound` int(11) UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY(`id`),
  CONSTRAINT `alias2compound` FOREIGN KEY (`idCompound`)
    REFERENCES `Compounds`(`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
ENGINE=INNODB;

-- Drop table ElementContents
DROP TABLE IF EXISTS `ElementContents`;

CREATE TABLE `ElementContents` (
  `int` int(11) UNSIGNED NOT NULL,
  `idCompound` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `element` char(50) NOT NULL,
  CONSTRAINT `elementContent2compounds` FOREIGN KEY (`idCompound`)
    REFERENCES `Compounds`(`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `elementContent2element` FOREIGN KEY (`element`)
    REFERENCES `Elements`(`name`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE=INNODB;

-- Drop table Conformers
DROP TABLE IF EXISTS `Conformers`;

CREATE TABLE `Conformers` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` char(50) NOT NULL,
  `description` varchar(255),
  `idCompoundFK` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `typeFK` char(25) NOT NULL,
  PRIMARY KEY(`id`),
  CONSTRAINT `conformer2compound` FOREIGN KEY (`idCompoundFK`)
    REFERENCES `Compounds`(`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `conformer2structureType` FOREIGN KEY (`typeFK`)
    REFERENCES `StructureTypes`(`type`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
ENGINE=INNODB;

-- Drop table GeneralProperties
DROP TABLE IF EXISTS `GeneralProperties`;

CREATE TABLE `GeneralProperties` (
  `propertyNameFK` char(50) NOT NULL,
  `idCompound` int(11) UNSIGNED NOT NULL DEFAULT '0',
  CONSTRAINT `property2type` FOREIGN KEY (`propertyNameFK`)
    REFERENCES `GeneralPropertiesTypes`(`propertyName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `generalProperty2compound` FOREIGN KEY (`idCompound`)
    REFERENCES `Compounds`(`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
ENGINE=INNODB;

-- Drop table Properties
DROP TABLE IF EXISTS `Properties`;

CREATE TABLE `Properties` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `property` longtext NOT NULL,
  `propertyNameFK` char(50) NOT NULL,
  `methodNameFK` varchar(100) NOT NULL,
  `idMethodFK` int(11) UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY(`id`),
  CONSTRAINT `property2propertyType` FOREIGN KEY (`propertyNameFK`)
    REFERENCES `PropertyTypes`(`propertyName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `property2method` FOREIGN KEY (`methodNameFK`)
    REFERENCES `Methods`(`methodName`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `property2conformer` FOREIGN KEY (`idMethodFK`)
    REFERENCES `Conformers`(`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
ENGINE=INNODB;

SET FOREIGN_KEY_CHECKS=1;

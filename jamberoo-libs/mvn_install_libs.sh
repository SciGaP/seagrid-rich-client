#!/usr/bin/env bash

mvn install:install-file -DgroupId=jamberoo -DartifactId=bsh-2.0b4 -Dversion=1.0 -Dpackaging=jar -Dfile=./bsh-2.0b4.jar
mvn install:install-file -DgroupId=jamberoo -DartifactId=cct-globus -Dversion=1.0 -Dpackaging=jar -Dfile=./cct-globus.jar
mvn install:install-file -DgroupId=jamberoo -DartifactId=Fragments -Dversion=1.0 -Dpackaging=jar -Dfile=./Fragments.jar
mvn install:install-file -DgroupId=jamberoo -DartifactId=j2ssh-common -Dversion=1.0 -Dpackaging=jar -Dfile=./j2ssh-common.jar
mvn install:install-file -DgroupId=jamberoo -DartifactId=j2ssh-core -Dversion=1.0 -Dpackaging=jar -Dfile=./j2ssh-core.jar
mvn install:install-file -DgroupId=jamberoo -DartifactId=Jamberoo-help -Dversion=1.0 -Dpackaging=jar -Dfile=./Jamberoo-help.jar
mvn install:install-file -DgroupId=jamberoo -DartifactId=jbzip2-0.9.1 -Dversion=1.0 -Dpackaging=jar -Dfile=./jbzip2-0.9.1.jar
mvn install:install-file -DgroupId=jamberoo -DartifactId=jcommon-1.0.12 -Dversion=1.0 -Dpackaging=jar -Dfile=./jcommon-1.0.12.jar
mvn install:install-file -DgroupId=jamberoo -DartifactId=jfreechart-1.0.9 -Dversion=1.0 -Dpackaging=jar -Dfile=./jfreechart-1.0.9.jar
mvn install:install-file -DgroupId=jamberoo -DartifactId=jhall -Dversion=1.0 -Dpackaging=jar -Dfile=./jhall.jar
mvn install:install-file -DgroupId=jamberoo -DartifactId=mysql-connector-java-5.1.33-bin -Dversion=1.0 -Dpackaging=jar -Dfile=./mysql-connector-java-5.1.33-bin.jar
mvn install:install-file -DgroupId=jamberoo -DartifactId=Solvents -Dversion=1.0 -Dpackaging=jar -Dfile=./Solvents.jar
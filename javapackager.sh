#!/usr/bin/env bash
$JAVA_HOME/bin/javapackager -deploy -native -outdir distribution -outfile SEAGridDesktop -srcdir builds/processed -srcfiles 1.jar\
 -appclass org.seagrid.desktop.SEAGridDesktop -name "SEAGridDesktop" -title "SEAGridDesktop"
#!/usr/bin/env bash
#$JAVA_HOME/bin/javapackager -deploy -native -outdir distribution -outfile SEAGridDesktop -srcdir builds/processed -srcfiles 1.jar\
# -appclass org.seagrid.desktop.SEAGridDesktop -name "SEAGridDesktop" -title "SEAGridDesktop"

$JAVA_HOME/bin/javapackager --type app-image -d distribution -i target/app --main-jar fxlauncher.jar\
 --main-class org.seagrid.desktop.SEAGridDesktop -n "SEAGridDesktop" -p  "$PATH_TO_FX" --add-modules javafx.controls,javafx.fxml  --mac-signing-key-user-name "Indiana University"



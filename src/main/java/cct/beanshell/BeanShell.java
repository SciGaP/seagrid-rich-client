/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.beanshell;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author vvv900
 */
public class BeanShell {

  public static void main(String args[]) throws IOException {

    if (args != null && args.length > 0 && args[0] != null && args[0].equals("testCommands")) {
      try {
        Interpreter i = new Interpreter();  // Construct an interpreter
        i.eval("importCommands(\"cct.beanshell.commands\")");
        //i.eval("import cct.interfaces.*");
        //i.eval("import cct.interfaces.*");
        // --- Testing sin
        i.eval("System.out.println(\"Testing sin:\")");
        i.eval("x = sin((double)20);");
        i.eval("System.out.println(\"Sin(double): \"+ x);");
        i.eval("x = sin((float)20);");
        i.eval("System.out.println(\"Sin(float): \"+ x);");
        i.eval("x = sin(20);");
        i.eval("System.out.println(\"Sin(int): \"+ x);");
        i.eval("x = sin(20l);");
        i.eval("System.out.println(\"Sin(long): \"+ x);");
        i.eval("x = sin(new Integer(20));");
        i.eval("System.out.println(\"Sin(Integer): \"+ x);");
        i.eval("x = sin(new Long(20));");
        i.eval("System.out.println(\"Sin(Long): \"+ x);");
        i.eval("x = sin(\"20 \");");
        i.eval("System.out.println(\"Sin(String): \"+ x);");
        // --- Testing cos
        i.eval("System.out.println(\"Testing cos:\")");
        i.eval("x = cos((double)20);");
        i.eval("System.out.println(\"cos(double): \"+ x);");
        i.eval("x = cos((float)20);");
        i.eval("System.out.println(\"cos(float): \"+ x);");
        i.eval("x = cos(20);");
        i.eval("System.out.println(\"cos(int): \"+ x);");
        i.eval("x = cos(20l);");
        i.eval("System.out.println(\"cos(long): \"+ x);");
        i.eval("x = cos(new Integer(20));");
        i.eval("System.out.println(\"cos(Integer): \"+ x);");
        i.eval("x = cos(new Long(20));");
        i.eval("System.out.println(\"cos(Long): \"+ x);");
        i.eval("x = cos(\"20 \");");
        i.eval("System.out.println(\"cos(String): \"+ x);");
        // --- Testing acos
        i.eval("System.out.println(\"Testing acos:\")");
        i.eval("x = acos((double)0.20);");
        i.eval("System.out.println(\"acos(double): \"+ x);");
        i.eval("x = acos((float)0.20);");
        i.eval("System.out.println(\"acos(float): \"+ x);");
        i.eval("x = acos(1);");
        i.eval("System.out.println(\"acos(int): \"+ x);");
        i.eval("x = acos(-1l);");
        i.eval("System.out.println(\"acos(long): \"+ x);");
        i.eval("x = acos(new Integer(1));");
        i.eval("System.out.println(\"acos(Integer): \"+ x);");
        i.eval("x = acos(new Long(-1));");
        i.eval("System.out.println(\"acos(Long): \"+ x);");
        i.eval("x = acos(\"0.999 \");");
        i.eval("System.out.println(\"acos(String): \"+ x+\" Ref: \"+Math.acos(0.999));");
        // --- Testing println
        i.eval("println(\"String: \"+\"0.999 \");");
        i.eval("println(\"Real: \"+0.999+\" Math.cos: \"+ Math.cos(20.0));");
      } catch (Exception ex) {
        Logger.getLogger(BeanShell.class.getName()).log(Level.SEVERE, null, ex);
      }
      System.exit(0);
    } else if (args != null && args.length > 0 && args[0] != null && args[0].equals("console")) {
      JConsole console = new JConsole();
      System.setIn(console.getInputStream());
      System.setOut(console.getOut());
      System.setErr(console.getErr());
      Interpreter interpreter = new Interpreter(console);

      try {
        interpreter.eval("importCommands(\"cct.beanshell.commands\")");
        interpreter.eval("importCommands(\"cct.beanshell.commands.jamberoo\")");
        interpreter.eval("import cct.gaussian.*");
        interpreter.eval("import cct.interfaces.*");
        interpreter.eval("import cct.modelling.*;");
        // ----
        if (args.length >= 2 && args[1].equals("jamberoo")) {
          interpreter.eval("global.jamberoo = new cct.JamberooMolecularEditor(null);");
          interpreter.eval("println(\"Variable jamberoo refers to the cct.JamberooMolecularEditor class\");");
        }
      } catch (EvalError ex) {
        Logger.getLogger(BeanShell.class.getName()).log(Level.SEVERE, null, ex);
      }
      new Thread(interpreter).start(); // start a thread to call the run() method
      try {
        Thread.sleep(1000);
      } catch (Exception ex) {
      }
      //interpreter.getErr();
      //interpreter.getOut();
      JFrame frame = new JFrame("Scripting");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(console);
      frame.pack();
      frame.setSize(800, 600);
      frame.setVisible(true);
      //try {
      //  Thread.sleep(1000000);
      //} catch (InterruptedException ex) {
      //  Logger.getLogger(BeanShell.class.getName()).log(Level.SEVERE, null, ex);
      //}

    } else if (args != null && args.length > 0 && args[0] != null && args[0].equals("gui")) {
      try {
        Interpreter i = new Interpreter();  // Construct an interpreter
        i.eval("importCommands(\"cct.beanshell.commands\")");
        i.eval("desktop()");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while (!(line = in.readLine()).equals("quit")) {

          line = line.trim();
        }

      } catch (Exception ex) {
        Logger.getLogger(BeanShell.class.getName()).log(Level.SEVERE, null, ex);
      }

      System.exit(0);
    } else {

      try {
        File f = new File("");
        f.getName().endsWith(null);
        Interpreter i = new Interpreter();  // Construct an interpreter
        //i.eval("import *;");
        i.eval("import cct.gaussian.*;");
        i.eval("import cct.interfaces.*;");
        i.eval("import cct.modelling.*;");
        i.eval("importCommands(\"cct.beanshell.commands\")");

        i.eval("log = new FileWriter(\"statistics.txt\");");
        i.eval("gauss = new Gaussian();");
        i.eval("gauss.setLoggerLevel(java.util.logging.Level.WARNING);");
        i.eval("int n = gauss.parseGJF(\"ferrocene-admp-20c.com\", 0);");
        i.eval("System.out.println(\"Number of molecules: \"+ n);");
        i.eval("MoleculeInterface mol = Molecule.getNewInstance();");
        i.eval("mol = gauss.getMolecule(mol, 0);");
        i.eval("System.out.println(\"Number of atoms: \"+ mol.getNumberOfAtoms());");
        i.eval("StructureManager sm = new StructureManager(mol)");
        //i.eval("sm.parseTrajectoryFile(\"/home/vvv900/fuji1/Fc/ferrocene-admp-20c.log\", TRAJECTORY_FILE_FORMAT.G03_TRAJECTORY)");
        //i.eval("sm.parseTrajectoryFile(\"/home/vvv900/fuji1/Fc/ferrocene-admp-minus-50c.log\", TRAJECTORY_FILE_FORMAT.G03_TRAJECTORY)");
        //i.eval("sm.parseTrajectoryFile(\"E:/_SCIENCE-PROJECTS/ferrocene-admp-minus-50c.log\", TRAJECTORY_FILE_FORMAT.G03_TRAJECTORY)");
        i.eval("sm.parseTrajectoryFile(\"Q:/TEMP/ferrocene-admp-minus-50c.log\", TRAJECTORY_FILE_FORMAT.G03_TRAJECTORY)");
        //i.eval("sm.parseTrajectoryFile(\"g09-md.out\", TRAJECTORY_FILE_FORMAT.G03_TRAJECTORY)");
        i.eval("snaps = sm.getNumberOfSnapshots();");
        i.eval("System.out.println(\"Number of snapshots: \"+ snaps);");
        i.eval("List list1 = new ArrayList();");
        i.eval("List list2 = new ArrayList();");
        i.eval("List list3 = new ArrayList();");
        i.eval("List list4 = new ArrayList();");
        i.eval("a72rad = toRadians(72.0);");
        i.eval("a36rad = toRadians(36.0);");
        i.eval("int i = 0;\n");
        //i.eval("while (i<snaps) {\n"
        i.eval("for(i=0; i<snaps; i+=100) {\n"
            + "  System.out.println(\"Current snapshot: \"+ (i+1));"
            + "  float [][] coords = sm.getStructure(i);\n"
            + "  for (j = 0; j < mol.getNumberOfAtoms(); j++) {\n"
            + "    atom = getAtom( mol, j+1 );\n"
            + "    atom.setXYZ(coords[j][0], coords[j][1], coords[j][2]);\n"
            + "  }\n"
            + "  Fe  = getAtom( mol, 21 );\n"
            + "  C_1 = getAtom( mol, 1 );\n"
            + "  C_2 = getAtom( mol, 6 );\n"
            + "  list = Molecule.getAtomListUsingAtomMaskSelection(mol, \"1-5\");\n"
            + "  System.out.println(\"List: \" + \"1-5\" + \" Size: \" + list.size());\n"
            + "  centroid_1 = Molecule.getCentroid(list);\n"
            + "  System.out.println(centroid_1.toString());\n"
            + "  list = Molecule.getAtomListUsingAtomMaskSelection(mol, \"6-10\");\n"
            + "  System.out.println(\"List: \" + \"6-10\" + \" Size: \" + list.size());\n"
            + "  centroid_2 = Molecule.getCentroid(list);\n"
            + "  System.out.println(centroid_2);\n"
            + "  Fe_1 = distance(Fe, C_1);\n"
            + "  list1.add(Fe_1);\n"
            + "  Fe_2 = distance(Fe, C_2);\n"
            + "  list2.add(Fe_2);\n"
            + "  dihed = dihedral(C_1, centroid_1, centroid_2, C_2);\n"
            + "  if ( dihed < 0 ) dihed = -dihed;\n"
            + "  dihed %= a72rad;\n"
            + "  if ( dihed > a36rad ) dihed = a72rad - dihed;\n"
            + "  dihed = toDegrees(dihed);\n"
            + "  list3.add(dihed);\n"
            + "  plane_1 = new cct.vecmath.Plane( getAtom( mol, 1), getAtom( mol, 3), getAtom( mol, 5));\n"
            + "  plane_2 = new cct.vecmath.Plane( getAtom( mol, 6), getAtom( mol, 8), getAtom( mol, 10));\n"
            + "  pp_angle = toDegrees( plane_1.angle(plane_2) );\n"
            + "  list4.add(pp_angle);\n"
            + "  log.write(Fe_1+\",\"+Fe_2+\",\"+dihed+\",\"+pp_angle+\"\\n\");\n"
            + "}\n"
            + "log.close();\n"
            + "data1 = list1.toArray();\n"
            + "mean = mean(data1);\n"
            + "System.out.println(\"Mean Fe - centroid_1 distance: \"+mean+\" Stn. dev: \"+cct.math.Statistics.standardDeviation(data1));"
            + "data2 = list2.toArray();\n"
            + "System.out.println(\"Mean Fe - centroid_2 distance: \"+mean(data2)+\" Stn. dev: \"+cct.math.Statistics.standardDeviation(data2));"
            + "data3 = list3.toArray();\n"
            + "data4 = list4.toArray();\n"
            + "System.out.println(data1.length + \" & \"+data2.length);"
            + "corr = cct.math.Statistics.linearCorrelationCoefficient(data1, data2);\n"
            + "System.out.println(\"Linear correlation coeff (dist-dist): \"+corr);"
            //+ "mean = mean(data3);\n"
            + "mean = mean(list3);\n"
            + "println(\"Mean dihedral value: \"+mean);"
            + "println(\"Median dihedral value: \"+median(list3));"
            + "println(\"Dihedral skew: \"+skew(list3));"
            + "println(\"Dihedral kurtosis: \"+kurtosis(list3));"
            + "mean = mean(data4);\n"
            + "System.out.println(\"Mean plane-plane angle: \"+mean);"
            + "System.out.println(\"Median plane-plane angle: \"+median(list4));"
            + "corr = cct.math.Statistics.linearCorrelationCoefficient(data3, data4);\n"
            + "System.out.println(\"Linear correlation coeff (twist-pp_angle): \"+corr);"
            + "\n"
        );

        i.eval("tcf = cct.math.Statistics.timeCorrelationFunction(list3, list3);\n"
            + "tcflog = new FileWriter(\"tcf.txt\");"
            + "for(i=0; i<tcf.length; i++) {\n"
            + "  tcflog.write(java.lang.String.valueOf(tcf[i])+\"\\n\");\n"
            + "}\n"
            + "tcflog.close();");

        i.eval("h = cct.math.Statistics.histogram( list3, 0, 36, 36 );\n"
            + "hlog = new FileWriter(\"histogram.txt\");"
            + "for(i=0; i<h.length; i++) {\n"
            + "  hlog.write(i+\",\"+java.lang.String.valueOf(h[i])+\"\\n\");\n"
            + "}\n"
            + "hlog.close();");

        //i.eval("JamberooMolecularEditor jamberoo = new JamberooMolecularEditor(null);");
        //i.eval("Set<JamberooFrame> frames = jamberoo.GetJamberooFrames();");
        //i.eval("Set<JamberooFrame> frames = jamberoo.GetJamberooFrames();");
      } catch (EvalError ex) {
        Logger.getLogger(BeanShell.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}

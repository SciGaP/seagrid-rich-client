/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.amber;

import cct.interfaces.MoleculeInterface;
import cct.modelling.Molecule;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vlad
 */
public class BuildRestraintMask {

  public static void main(String[] args) {
    String prmtop = null;
    String input = null;
    int count = 0, numResidues = -1;
    BufferedReader prmtopReader;
    AmberPrmtop amberPrmtop;
    MoleculeInterface mol = new Molecule();
    Set<Integer> residues = new LinkedHashSet<Integer>();

    if (args.length == 0) {
      System.out.println("Enter residues, one per line:");

      try {
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
          String s = bufferRead.readLine();

          System.out.println(s);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      while (count < args.length) {
        if (args[count].equals("-t")) {
          if ((count + 1 >= args.length)) {
            System.err.println("Expected topology file name");
            System.exit(1);
          }
          ++count;
          prmtop = args[count];
        } else if (args[count].equals("-i")) {
          if ((count + 1 >= args.length)) {
            System.err.println("Expected imput file name");
            System.exit(1);
          }
          ++count;
          input = args[count];
        }
        // --- Increment counter
        ++count;
      }

      // --- reading topology file, if any
      if (prmtop != null) {
        try {
          prmtopReader = new BufferedReader(new FileReader(prmtop));
          amberPrmtop = new AmberPrmtop();
          System.out.println("Reading prmtop file " + prmtop + "...");
          amberPrmtop.parsePrmtop(mol, prmtopReader);
          System.out.println("Number of atoms: " + mol.getNumberOfAtoms() + " Number of residues: " + mol.getNumberOfMonomers());
          if (numResidues != -1) {
            System.out.println("Getting number of residues from prmtop file...");
          }
          numResidues = mol.getNumberOfMonomers();
          prmtopReader.close();
        } catch (Exception ex) {
          Logger.getLogger(BuildRestraintMask.class.getName()).log(Level.SEVERE, null, ex);
          System.exit(1);
        }
      }

      // --- reading imput file, if any
      if (input != null) {
        int res = -1;
        try {
          prmtopReader = new BufferedReader(new FileReader(input));
          String line;
          while ((line = prmtopReader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("#") || line.startsWith(";") || line.length() < 1) {
              continue;
            }
            // ---
            res = -1;
            try {
              res = Integer.parseInt(line);
              residues.add(res);
              continue;
            } catch (Exception ex) {
            }

            if (line.matches("\\w{3}\\s*\\d+")) {
              String str = line.substring(3).trim();
              try {
                res = Integer.parseInt(str);
                residues.add(res);
                continue;
              } catch (Exception ex) {
                System.err.println("Cannot parse line " + line);
                System.exit(1);
              }
            }

            System.err.println("Cannot parse line " + line);
            System.exit(1);
          }
          prmtopReader.close();
          // ---

        } catch (Exception ex) {
          Logger.getLogger(BuildRestraintMask.class.getName()).log(Level.SEVERE, null, ex);
          System.exit(1);
        }
      }
    }

    Integer[] resArray = residues.toArray(new Integer[0]);
    Arrays.sort(resArray);
    System.out.println("Read " + residues.size() + " residues (in sorted order)");
    for (Integer r : resArray) {
      System.out.println(r);
    }
    // ---
    if (numResidues < resArray[resArray.length - 1]) {
      numResidues = resArray[resArray.length - 1];
      System.out.println("Total number of residues is set to " + numResidues);
    }
    // --- 
    int firstRestr = 1, lastRestr = 1;
    String restraintmask = "";

    for (Integer res : resArray) {
      if (res > firstRestr) {
        lastRestr = res - 1;
      } else if (res == firstRestr) {
        firstRestr = res + 1;
      }

      if (lastRestr >= firstRestr) {
        if (restraintmask.length() > 0) {
          restraintmask += ",";
        }
        restraintmask += String.valueOf(firstRestr);
        if (lastRestr > firstRestr) {
          restraintmask += "-" + String.valueOf(lastRestr);
        }

        firstRestr = lastRestr = res + 1;
      }
    }

    firstRestr = resArray[resArray.length - 1] + 1;
    lastRestr = numResidues;

    if (firstRestr <= numResidues) {
      if (restraintmask.length() > 0) {
        restraintmask += ",";
      }
      restraintmask += String.valueOf(firstRestr);
    }

    if (lastRestr > firstRestr) {
      restraintmask += "-" + String.valueOf(lastRestr);
    }

    System.out.println("restraintmask=':" + restraintmask + "'");
    System.exit(0);
  }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.amber;

import cct.interfaces.ChartDataProvider;
import cct.tools.DataSets;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Vlad
 */
public class AmberMden extends DataSets implements ChartDataProvider {

  public void parseData(BufferedReader in) throws Exception {
    String line;
    int lineCount = 0;
    // --- Reading in decriptors
    String theFirstLine = "L0";
    List<String> lines = new ArrayList<String>();
    clearDescriptors();
    //descriptors.clear();
    clearData();
    int count = 0;
    Map<String, List<String>> descrRef = new LinkedHashMap<String, List<String>>();

    in.mark(132);
    while ((line = in.readLine()) != null) {
      ++lineCount;
      String expectedLine = "L" + String.valueOf(count);
      if (line.startsWith(expectedLine)) {
        lines.add(expectedLine);
        String[] tokens = line.substring(expectedLine.length()).trim().split("\\s+");
        List<String> desc = new ArrayList<String>();
        descrRef.put(expectedLine, desc);
        for (int i = 0; i < tokens.length; i++) {
          desc.add(tokens[i]);
          if (hasDescriptor(tokens[i])) {
            System.err.println("???: Warning descriptor " + tokens[i] + " occurs second time. Ignored...");
          } else {
            this.addDescriptor(tokens[i]);
            //descriptors.add(tokens[i]);
            //List dt = new ArrayList();
            //data.put(tokens[i], dt);
          }
        }

      } // --- We finished to read in descriptors
      else if (count != 0 && line.startsWith(theFirstLine)) {
        in.reset();
        break;
      } else {

      }
      in.mark(200);
      ++count;
    }

    // --- Start to read data
    boolean loopInProgress = true;
    do {
      for (String key : descrRef.keySet()) {
        List<String> desc = descrRef.get(key);
        line = in.readLine();
        ++lineCount;
        if (line == null) {
          loopInProgress = false;
          break;
        }
        String[] tokens = line.substring(key.length()).trim().split("\\s+");
        for (int i = 0; i < Math.min(tokens.length, desc.size()); i++) {
          String label = desc.get(i);
          if (label.equals("Nsteps")) {
            try {
              int num = Integer.parseInt(tokens[i]);
              addData(label, num);
              //data.get(label).add(num);
            } catch (Exception ex) {
              System.err.println("Cannot parse integer value for " + label + " in line: " + line);
            }
          } else {
            try {
              double dbl = Double.parseDouble(tokens[i]);
              //data.get(label).add(dbl);
              addData(label, dbl);
            } catch (Exception ex) {
              System.err.println("Cannot parse real value for " + label + " in line: " + line);
            }
          }
        }
      }
    } while (loopInProgress);

  }

  public static void main(String[] args) {
    AmberMden amberMden = new AmberMden();
    try {
      amberMden.parseData("mden");
      System.out.println("Descriptors");
      for (String desc : amberMden.getDescriptors()) {
        System.out.println(desc);
      }
      System.out.println("Data size: " + amberMden.getDataSize());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public float isThisFormat(String filename) throws Exception {

    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(filename));
    } catch (Exception e) {
      throw new Exception("Error opening file " + filename + " : " + e.getMessage());
    }

    float score = isThisFormat(in);

    try {
      in.close();
    } catch (Exception ex) {
    }

    return score;
  }

  public float isThisFormat(BufferedReader in) throws Exception {
    float score = 0;
    String line;

    while ((line = in.readLine()) != null) {

      if (score >= 1.0) {
        score = 1.0f;
        return score;
      }

      // --- MD info
      if (line.contains("L0  Nsteps           time(ps)         Etot             EKinetic")) {
        score += 0.25;
        line = in.readLine();
        if (line == null) {
          return score;
        }
        if (line.contains("L1  Temp             ")) {
          score += 0.25;
        }
        line = in.readLine();
        if (line == null) {
          return score;
        }
        if (line.contains("L2  ")) {
          score += 0.25;
        }
        line = in.readLine();
        if (line == null) {
          return score;
        }
        if (line.contains("L3  ")) {
          score += 0.25;
        }
        if (score >= 0.5) {
          return score;
        }
      }
      // ---
      if (score >= 1.0) {
        score = 1.0f;
        return score;
      }
    }

    return score;
  }

  // --- Implementation of ChartDataProvider interface
  public String cdpGetDescription() {
    return "Amber mden file";
  }

  public void cdpParseData(String filename) throws Exception {
    parseData(filename);
  }

  public Set<String> cdpGetDescriptors() {
    return this.getDescriptors();
  }

  public double[] cdpGetDataAsDouble(String descriptor) {
    return this.getDataAsDouble(descriptor);
  }

  public float[] cdpGetDataAsFloat(String descriptor) {
    return this.getDataAsFloat(descriptor);
  }

  public int[] cdpGetDataAsInteger(String descriptor) {
    return this.getDataAsInteger(descriptor);
  }

  public String[] cdpGetDataAsString(String descriptor) {
    return this.getDataAsString(descriptor);
  }

  public Object[] cdpGetDataAsObject(String descriptor) {
    return this.getDataAsObject(descriptor);
  }

  public float cdpIsThisFormat(String filename) throws Exception {
    return isThisFormat(filename);
  }
}

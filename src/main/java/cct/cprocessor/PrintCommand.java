/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.cprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vvv900
 */
public class PrintCommand implements InbuiltCommandInterface {

  private BufferedWriter bufferedWriter;
  private String fileName;

  private CommandProcessor commandProcessor;

  public PrintCommand() {

  }

  public PrintCommand(CommandProcessor commandProcessor) {
    this.commandProcessor = commandProcessor;
  }

  public int getNewCommandCounter() {
    return -1;
  }

  public CommandInterface ciInstance() {
    return new PrintCommand(commandProcessor);
  }

  public Object ciExecuteCommand(Object[] args) throws Exception {
    if (args == null || args.length == 0) {
      return null;
    }

    if (args[0].toString().equals("setLog")) {
      try {
        BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
        setBufferedWriter(out);
        return null;
      } catch (Exception ex) {
        throw new Exception(this.getClass().getCanonicalName() + ": CreateLog: cannot create file "
            + fileName + " for writing: " + ex.getLocalizedMessage());
      }
    } else if (args[0].toString().equals("close")) {
      bufferedWriter.close();
      return null;
    }

    if (bufferedWriter != null) {
      for (Object obj : args) {
        if (obj instanceof Variable) {
          Variable var = (Variable) obj;
          if (var.getValue() == null) {
            bufferedWriter.write(var.getValue() + " ");
          } else {
            bufferedWriter.write(var.getValue().toString() + " ");
          }
        } else {
          bufferedWriter.write(obj.toString());
        }
      }
      bufferedWriter.newLine();
      return null;
    }

    for (Object obj : args) {
      if (obj instanceof Variable) {
        Variable var = (Variable) obj;
        if (var.getValue() == null) {
          System.out.print(var.getValue() + " ");
        } else {
          if (var.getValue().getClass().isArray()) {
            printArray(var.getValue());
            System.out.print(var.getValue().toString() + " ");
          } else {
            System.out.print(var.getValue().toString() + " ");
          }
        }
      } else {
        System.out.print(obj.toString());
      }
    }
    System.out.println();
    return null;
  }

  public Object[] ciParseCommand(String[] tokens) throws Exception {
    if (tokens == null || tokens.length <= 1) {
      return null;
    }

    if (tokens[0].equals("CreateLog")) {
      if (tokens.length != 1) {
        throw new Exception(this.getClass().getCanonicalName() + ": CreateLog requires no argument(s)");
      }
      return new Object[]{new PrintCommand(this.commandProcessor)};
      // ----
    } else if (tokens[1].equals("setLog")) {
      try {
        setFileName(tokens[2]);
        return new Object[]{"setLog", tokens[2]};
      } catch (Exception ex) {
        throw new Exception(this.getClass().getCanonicalName() + ": CreateLog: cannot create file "
            + tokens[1] + " for writing: " + ex.getLocalizedMessage());
      }
    } else if (tokens[1].equals("close")) {
      return new Object[]{"close"};
    }

    StringBuilder sb = new StringBuilder();
    boolean buildingStr = false;
    List list = new ArrayList();
    boolean oneTime = true;
    for (String token : tokens) {
      if (oneTime) {
        oneTime = false;
        continue;
      }
      if (commandProcessor.isHasVariable(token)) {
        if (buildingStr) {
          list.add(sb.toString());
          buildingStr = false;
          list.add(commandProcessor.getVariable(token));
        } else {
          list.add(commandProcessor.getVariable(token));
        }
      } else {
        if (buildingStr) {
          sb.append(token + " ");
        } else {
          if (sb.length() < 1) {
            sb.delete(0, sb.length());
          }
          buildingStr = true;
          sb.append(token + " ");
        }
      }
    }
    if (buildingStr) {
      list.add(sb.toString());
    }
    return list.toArray();
  }

  public void setCommandProcessor(CommandProcessor commandProcessor) {
    this.commandProcessor = commandProcessor;
  }

  public static void printArray(Object inputArray) {
    if (inputArray instanceof float[][]) {
      printArray((float[][]) inputArray);
    } else if (inputArray instanceof float[]) {
      printArray((float[]) inputArray);
    } else if (inputArray instanceof Object[]) {
      printArray((Object[]) inputArray);
    } else if (inputArray instanceof Object[][]) {
      if (inputArray != null) {
        System.out.printf("%s ", inputArray.toString());
        return;
      }
    }
    System.out.printf("%s ", inputArray.toString());
  }

  public static <E> void printArray(E[] inputArray) {
    for (E element : inputArray) {
      if (element.getClass().isArray()) {
        printArray(element);
      } else {
        System.out.printf("%s ", element);
      }
    }
    System.out.println();
  }

  public static void printArray(float[][] inputArray) {
    for (int i = 0; i < inputArray.length; i++) {
      System.out.printf("%5d: ", i);
      for (int j = 0; j < inputArray[i].length; j++) {
        System.out.printf("%10.4f ", inputArray[i][j]);
      }
      System.out.print("\n");
    }
  }

  public static void printArray(float[] inputArray) {
    for (int i = 0; i < inputArray.length; i++) {
      System.out.printf("%5d: %10.4f ", i, inputArray[i]);
      System.out.print("\n");
    }
  }

  public BufferedWriter getBufferedWriter() {
    return bufferedWriter;
  }

  public void setBufferedWriter(BufferedWriter bufferedWriter) {
    this.bufferedWriter = bufferedWriter;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

}

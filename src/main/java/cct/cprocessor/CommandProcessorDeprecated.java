/* ***** BEGIN LICENSE BLOCK *****
   Version: Apache 2.0/GPL 3.0/LGPL 3.0

   CCT - Computational Chemistry Tools
   Jamberoo - Java Molecules Editor

   Copyright 2008-2015 Dr. Vladislav Vasilyev

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Contributor(s):
     Dr. Vladislav Vasilyev <vvv900@gmail.com>       (original author)

  Alternatively, the contents of this file may be used under the terms of
  either the GNU General Public License Version 2 or later (the "GPL"), or
  the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  in which case the provisions of the GPL or the LGPL are applicable instead
  of those above. If you wish to allow use of your version of this file only
  under the terms of either the GPL or the LGPL, and not to allow others to
  use your version of this file under the terms of the Apache 2.0, indicate your
  decision by deleting the provisions above and replace them with the notice
  and other provisions required by the GPL or the LGPL. If you do not delete
  the provisions above, a recipient may use your version of this file under
  the terms of any one of the Apache 2.0, the GPL or the LGPL.

 ***** END LICENSE BLOCK *****/

package cct.cprocessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 * @Deprecated
 */


enum EraseCommandObject {
   A, ATOM, ATOMS, B, BOND, MOLECULE
}

enum SelectCommandObjects {
   ATOM("atom"), ATOMS("atoms");

   String comObject;
   SelectCommandObjects(String obj) {
      comObject = obj;
   }
}

/**
 * 
 * @author vvv900
 * @Deprecated 
 */
public class CommandProcessorDeprecated {

   List<Object[]> compiledCommands = new ArrayList<Object[]> ();
   MolProcessorInterface molProcessor = null;

   Commands currentCommand = null;
   Object commandObject = null;
   Object commandArguments = null;

   public CommandProcessorDeprecated(MolProcessorInterface mp) {
      molProcessor = mp;
   }

   public static void main(String[] args) {
      CommandProcessorDeprecated commandprocessor = new CommandProcessorDeprecated(null);
   }

   public void executeScript() throws Exception {
      executeScript(compiledCommands);
   }

   public void executeScript(List < Object[] > compiled_commands) throws Exception {
      for (int i = 0; i < compiled_commands.size(); i++) {
         Object[] commLine = compiled_commands.get(i);
         if (! (commLine[0] instanceof Commands)) {
            throw new Exception("Runtime Error: expecting command, got " +
                                commLine[0].getClass().getCanonicalName());
         }
         Commands command = (Commands) commLine[0];
         try {
            switch (command) {
               case A:
               case ADD:
                  AddCommandObjects obj = (AddCommandObjects) commLine[1];
                  executeAddCommand(obj, commLine[2]);
                  break;
               case S:
               case SEL:
               case SELECT:

                  //parseSelectCommand(st);
                  break;
               case D:
               case DEL:
               case DELETE:
               case E:
               case ERASE:
                  EraseCommandObject obj2 = (EraseCommandObject) commLine[1];
                  executeEraseCommand(obj2, commLine[2]);
                  break;
               case C:
               case CENTER:
                  executeCenterCommand(commLine[1], commLine[2]);
                  break;
               case SLEEP:
                  Long millis = (Long) commLine[2];
                  Thread.sleep(millis);
                  break;
               case RENDER:
                  RenderCommandObject obj3 = (RenderCommandObject) commLine[1];
                  executeRenderCommand(obj3, commLine[2]);
                  break;
            }
         }
         catch (Exception ex) {
            throw new Exception("Runtime Error: " + ex.getMessage());
         }
      }
   }

   void executeAddCommand(AddCommandObjects command_object, Object args) throws
       Exception {
      switch (command_object) {
         case ATOM:
            molProcessor.addAtom(args);
            break;
         case BOND:
            molProcessor.addBond(args);
            break;
      }
   }

   void executeEraseCommand(EraseCommandObject command_object, Object args) throws
       Exception {
      switch (command_object) {
         case A:
         case ATOM:
         case ATOMS:
            molProcessor.eraseAtoms(args);
            break;
         case BOND:
         case B:
            throw new Exception("Runtime Error: ERASE BOND is not implemented yet");
            //break;
         case MOLECULE:

            break;
      }
   }

   void executeCenterCommand(Object command_object, Object args) throws
       Exception {
      molProcessor.centerMolecule(args);
   }

   void executeRenderCommand(RenderCommandObject command_object, Object args) throws
       Exception {
      switch (command_object) {
         case WIREFRAME:
            molProcessor.setRenderingStyle("Wireframe");
            break;
         case STICKS:
            molProcessor.setRenderingStyle("Sticks");
            break;
         case BALLS:
            molProcessor.setRenderingStyle("Ball & Sticks");
            break;
         case SPACEFILL:
            molProcessor.setRenderingStyle("Spacefill");
            break;
         default:
            molProcessor.setRenderingStyle(command_object.toString());
      }
   }

   public void parseScript(String filename, int fileType) throws Exception {

      String line;
      BufferedReader in = null;
      StringWriter sWriter = new StringWriter();

      if (fileType == 0) { // Read from file
         try {
            in = new BufferedReader(new FileReader(filename));
         }
         catch (Exception ex) {
            throw ex;
         }
      }
      else if (fileType == 1) { // Read from String
         in = new BufferedReader(new StringReader(filename));
      }
      else {
         throw new Exception(
             "parseScript: INTERNAL ERROR: Unknown file type");
      }

      try {
         //BufferedReader in = new BufferedReader(new FileReader(filename));

         int lineNumber = 0;
         while ( (line = in.readLine()) != null) {
            line = line.trim();
            ++lineNumber;

            if (line.length() < 1) { // Blank line
               continue;
            }

            if (line.startsWith("!") || line.startsWith("#") ||
                line.startsWith(";")) {
               continue;
            }

            StringTokenizer st = new StringTokenizer(line, " \t,");

            // --- First token - command

            String com = st.nextToken().toUpperCase();
            Commands command;
            /*
                         try {
               //command = Commands.valueOf(com);
               command = this.getCommand(com);
                         }
                         catch (Exception ex) {
               sWriter.write("ERROR: line " + lineNumber +
                             ": Unrecognizable command: " + com + "\n");
               continue;
                         }
             */

            command = this.getCommand(com);
            if (command == null) {
               sWriter.write("ERROR: line " + lineNumber +
                             ": Unrecognizable command: " + com + "\n");
               continue;
            }
            currentCommand = command;

            try {
               parseCommand(command, st);
            }
            catch (Exception ex) {
               sWriter.write("ERROR: line " + lineNumber + ": " + ex.getMessage() +
                             "\n");
               continue;
            }

            // --- Record a compiled command

            Object[] commLine = {
                currentCommand, commandObject, commandArguments};
            compiledCommands.add(commLine);
         }

         in.close();

      }
      catch (Exception e) {
         throw e;
      }

      String errors = sWriter.toString();
      if (errors != null && errors.length() > 0) {
         throw new Exception(errors);
      }

   }

   void parseCommand(Commands command, StringTokenizer st) throws Exception {
      switch (command) {
         case A:
         case ADD:
            parseAddCommand(st);
            break;
         case S:
         case SEL:
         case SELECT:
            parseSelectCommand(st);
            break;
         case D:
         case DEL:
         case DELETE:
         case E:
         case ERASE:
            parseEraseCommand(st);
            break;
         case C:
         case CENTER:
            commandObject = null;
            commandArguments = null;
            break;
         case SLEEP:
            parseSleepCommand(st);
            break;
         case RENDER:
            parseRenderCommand(st);
            break;

      }

   }

   void parseAddCommand(StringTokenizer st) throws Exception {
      if (st.countTokens() < 1) {
         throw new Exception(
             "add command has to have an object to be applied to...");
      }

      String cobj = st.nextToken().toUpperCase();

      AddCommandObjects command_object = this.getAddCommandObject(cobj);

      if (command_object == null) {
         throw new Exception("add command has no object " + cobj);
      }
      /*
             try {
         command_object = AddCommandObjects.valueOf(cobj);
             }
             catch (Exception ex) {
         throw new Exception("add command has no object " + cobj);
             }
       */
      commandObject = command_object;

      switch (command_object) {
         case ATOM:
            parseAddAtomArg(st);
            break;
         case BOND:
            parseAddBondArg(st);
            break;

      }
   }

   void parseAddAtomArg(StringTokenizer st) throws Exception {
      if (st.countTokens() < 3) {
         throw new Exception(
             "add atom command has to have at least 3 arguments");
      }

      String token = "";
      float x, y, z;
      try {
         token = st.nextToken();
         x = Float.parseFloat(token);
         token = st.nextToken();
         y = Float.parseFloat(token);
         token = st.nextToken();
         z = Float.parseFloat(token);
      }
      catch (Exception ex) {
         throw new Exception(
             "add atom command: cannot parse coordinate " + token);
      }

      int element = 0;
      if (st.hasMoreTokens()) {
         try {
            token = st.nextToken();
            element = Integer.parseInt(token);
         }
         catch (Exception ex) {
            throw new Exception(
                "add atom command: cannot parse atomic element " + token);
         }
      }

      String name = "";
      if (st.hasMoreTokens()) {
         name = st.nextToken();
      }

      GenericAtom atom = new GenericAtom(x, y, z, element, name);
      commandArguments = atom;
   }

   void parseAddBondArg(StringTokenizer st) throws Exception {
      if (st.countTokens() < 2) {
         throw new Exception(
             "add bond command has to have 2 arguments");
      }

      String token = "";
      int i, j;
      try {
         token = st.nextToken();
         i = Integer.parseInt(token) - 1;
         token = st.nextToken();
         j = Integer.parseInt(token) - 1;
      }
      catch (Exception ex) {
         throw new Exception(
             "add bond command: cannot parse atom number " + token);
      }

      GenericBond bond = new GenericBond(i, j);
      commandArguments = bond;
   }

   void parseSelectCommand(StringTokenizer st) throws Exception {
      if (st.countTokens() < 1) {
         throw new Exception(
             "select command has to have an object to be applied to...");
      }

      String cobj = st.nextToken();

      SelectCommandObjects command_object;
      try {
         command_object = SelectCommandObjects.valueOf(cobj);
      }
      catch (Exception ex) {
         throw new Exception("select command has no object " + cobj);
      }

      commandObject = command_object;

      switch (command_object) {
         case ATOM:
         case ATOMS:
            parseSelectAtomArg(st);
            break;
      }
   }

   void parseSelectAtomArg(StringTokenizer st) throws Exception {
      if (st.countTokens() < 1) {
         return;
      }

      int[] atom_numbers = new int[st.countTokens()];

      String token = "";
      int count = 0;
      while (st.hasMoreTokens()) {
         try {
            token = st.nextToken();
            atom_numbers[count] = Integer.parseInt(token) - 1;
            ++count;
         }
         catch (Exception ex) {
            throw new Exception(
                "select atoms command: cannot parse atom number " + token);
         }

      }

      commandArguments = atom_numbers;
   }

   void parseEraseCommand(StringTokenizer st) throws Exception {
      if (st.countTokens() < 1) {
         throw new Exception(
             "erase command has to have an object to be applied to...");
      }

      String cobj = st.nextToken().toUpperCase();

      EraseCommandObject command_object = this.getEraseCommandObject(cobj);

      if (command_object == null) {
         throw new Exception("erase command has no object " + cobj);
      }
      commandObject = command_object;

      switch (command_object) {
         case A:
         case ATOM:
         case ATOMS:
            parseEraseAtomsArg(st);
            break;
         case BOND:
         case B:
            parseAddBondArg(st);
            break;
         case MOLECULE:
            parseAddBondArg(st);
            break;

      }
   }

   void parseEraseAtomsArg(StringTokenizer st) throws Exception {
      if (st.countTokens() < 1) {
         throw new Exception(
             "erase atom(s) command has to have at least 1 argument");
      }

      String token = "";
      int[] atom_numbers = new int[st.countTokens()];
      int count = 0;
      while (st.hasMoreTokens()) {
         try {
            token = st.nextToken();
            atom_numbers[count] = Integer.parseInt(token) - 1;
            ++count;
         }
         catch (Exception ex) {
            throw new Exception(
                "erase atom(s) command: cannot parse atom number " + token);
         }

      }

      commandArguments = atom_numbers;
   }

   void parseSleepCommand(StringTokenizer st) throws Exception {
      if (st.countTokens() < 1) {
         throw new Exception(
             "sleep command has to have an argument...");
      }

      commandObject = null;

      String token = st.nextToken();
      Long millis = 0L;
      try {
         millis = Long.parseLong(token);
      }
      catch (Exception ex) {
         throw new Exception("Error parsing milliseconds for sleep command: expecting integer number, got: " + token);
      }
      commandArguments = millis;
   }

   void parseRenderCommand(StringTokenizer st) throws Exception {
      if (st.countTokens() < 1) {
         throw new Exception(
             "render command has to have an object to be applied to...");
      }

      String cobj = st.nextToken().toUpperCase();

      RenderCommandObject command_object = this.getRenderCommandObject(cobj);

      if (command_object == null) {
         throw new Exception("render command has no object " + cobj);
      }
      commandObject = command_object;
      commandArguments = null;
   }

   Commands getCommand(String command) throws Exception {
      for (Commands c : Commands.values()) {
         if (command.equalsIgnoreCase(c.toString())) {
            return c;
         }
      }
      throw new Exception("No such command " + command);
   }

   AddCommandObjects getAddCommandObject(String command) throws Exception {
      for (AddCommandObjects c : AddCommandObjects.values()) {
         if (command.equalsIgnoreCase(c.toString())) {
            return c;
         }
      }
      throw new Exception("No such add command object " + command);
   }

   EraseCommandObject getEraseCommandObject(String command) throws Exception {
      for (EraseCommandObject c : EraseCommandObject.values()) {
         if (command.equalsIgnoreCase(c.toString())) {
            return c;
         }
      }
      throw new Exception("No such erase command object " + command);
   }

   RenderCommandObject getRenderCommandObject(String command) throws Exception {
      for (RenderCommandObject c : RenderCommandObject.values()) {
         if (command.equalsIgnoreCase(c.toString())) {
            return c;
         }
      }
      throw new Exception("No such render command object " + command);
   }

}

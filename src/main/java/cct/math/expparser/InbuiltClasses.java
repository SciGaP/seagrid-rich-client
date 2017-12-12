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
package cct.math.expparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author vvv900
 */
public class InbuiltClasses implements ClassInterface {

  enum CLASS {

    integer, real, bool, logical, List, Writer, Reader

  }

  private static Map<String, CLASS> allClasses = new HashMap<String, CLASS>();
  private static Map<CLASS, Set<Object>> classMethods = new HashMap<CLASS, Set<Object>>();

  enum LIST_METHOD {

    add, size, get, isEmpty, clear, contains, indexOf, iterator, toArray, lastIndexOf, remove, set
  }

  enum WRITER_METHOD {

    close, flush, newLine, write
  }

  enum READER_METHOD {

    close, mark, markSupported, read, readLine, ready, reset, skip
  }

  private String name;
  private CLASS classType;
  private List list;
  private BufferedWriter bufferedWriter;
  private BufferedReader bufferedReader;

  public InbuiltClasses() {
    if (allClasses.size() == 0) {
      allClasses.put("int", CLASS.integer);
      allClasses.put("INT", CLASS.integer);
      allClasses.put("integer", CLASS.integer);
      allClasses.put("INTEGER", CLASS.integer);
      allClasses.put("real", CLASS.real);
      allClasses.put("REAL", CLASS.real);
      allClasses.put("logical", CLASS.logical);
      allClasses.put("LOGICAL", CLASS.logical);
      allClasses.put("bool", CLASS.logical);
      allClasses.put("BOOL", CLASS.logical);
      allClasses.put("List", CLASS.List);
      allClasses.put("LIST", CLASS.List);
      allClasses.put(CLASS.Writer.name(), CLASS.Writer);
      allClasses.put(CLASS.Writer.name().toUpperCase(), CLASS.Writer);
      allClasses.put(CLASS.Reader.name(), CLASS.Reader);
      allClasses.put(CLASS.Reader.name().toUpperCase(), CLASS.Reader);
      // ---
      classMethods.put(CLASS.List, new HashSet<Object>());
      for (LIST_METHOD lm : LIST_METHOD.values()) {
        classMethods.get(CLASS.List).add(lm);
      }
      classMethods.put(CLASS.Writer, new HashSet<Object>());
      for (WRITER_METHOD wm : WRITER_METHOD.values()) {
        classMethods.get(CLASS.Writer).add(wm);
      }
      classMethods.put(CLASS.Reader, new HashSet<Object>());
      for (READER_METHOD wm : READER_METHOD.values()) {
        classMethods.get(CLASS.Reader).add(wm);
      }
    }
  }

  public InbuiltClasses(CLASS classType, Object[] args) throws Exception {
    this();
    this.classType = classType;
    name = classType.name();
    switch (classType) {
      case List:
        list = new ArrayList();
        return;
      case Writer:
        if (args == null || args.length == 0 || args.length > 2) {
          throw new Exception("Constructor of Class " + classType.name() + " must have either 1 or 2 arguments");
        }
        // ---
        if (args.length == 1) { // File name or something else
          if (args[0] == null) {
            throw new Exception("The first argument of a Constructor of Class " + classType.name()
                + " must be non-zero");
          }
        }
        if (args.length == 2 && args[1] == null) {
          throw new Exception("The second argument of a Constructor of Class " + classType.name()
              + " must be non-zero");
        }
        Boolean append = new Boolean(false);
        if (args.length == 2) {
          if (args[2] instanceof Boolean) {
            append = (Boolean) args[1];
          } else {
            try {
              append = Boolean.parseBoolean(args[1].toString());
            } catch (Exception ex) {
              throw new Exception("The second argument of a Constructor of Class " + classType.name()
                  + " must be of boolean type. Gor: " + args[1].getClass().getCanonicalName());
            }
          }
        }
        bufferedWriter = new BufferedWriter(new FileWriter(args[0].toString(), append));
        return;
      // --- 
      case Reader:
        if (args == null || args.length == 0 || args.length > 2) {
          throw new Exception("Constructor of Class " + classType.name() + " must have either 1 or 2 arguments");
        }
        // ---
        if (args.length == 1) { // File name or something else
          if (args[0] == null) {
            throw new Exception("The first argument of a Constructor of Class " + classType.name()
                + " must be non-zero");
          }
        }
        if (args.length == 2 && args[1] == null) {
          throw new Exception("The second argument of a Constructor of Class " + classType.name()
              + " must be non-zero");
        }
        Integer sz = new Integer(64 * 1024);
        if (args.length == 2) {
          if (args[2] instanceof Number) {
            sz = ((Number) args[1]).intValue();
            System.out.println("Using buffer size: " + sz);
          } else {
            try {
              sz = Integer.parseInt(args[1].toString());
            } catch (Exception ex) {
              throw new Exception("The second argument of a Constructor of Class " + classType.name()
                  + " must be of integer type. Got: " + args[1].getClass().getCanonicalName());
            }
          }
        }
        bufferedReader = new BufferedReader(new FileReader(args[0].toString()));
        return;
    }
    throw new Exception("Constructor for Class " + classType.name() + " is not implemented");
  }

  public InbuiltClasses(String className) throws Exception {
    this();
    ciSetName(className);
  }

  public ClassInterface ciAllocateClass(Object[] args) throws Exception {

    return new InbuiltClasses(classType, args);
  }

  public Object ciExecuteMethod(String method, Object[] args) throws Exception {
    switch (classType) {
      case List:
        if (!ciHasMethod(method)) {
          throw new Exception("Class of type" + classType.name() + " does not have method \"" + method + "\"");
        }
        if (method.equals(LIST_METHOD.add.name())) {
          if (args.length < 1 || args.length > 2) {
            throw new Exception("\"" + method + "\" of Class " + classType.name() + " has either 1 or 2 arguments");
          }
          if (args.length == 1) {
            return list.add(args[0]);
          } else if (args.length == 2) {
            list.add(((Number) args[0]).intValue(), args[1]);
            return Void.class;
          }
        } else if (method.equals(LIST_METHOD.set.name())) {
          if (args == null || args.length != 2) {
            throw new Exception("\"" + method + "\" of Class " + classType.name() + " has 2 arguments");
          } else if (args.length == 2) {
            return list.set(((Number) args[0]).intValue(), args[1]);

          }// --------
        } else if (method.equals(LIST_METHOD.size.name())) {
          if (args != null && args.length > 0) {
            throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
          }
          return list.size();
        } else if (method.equals(LIST_METHOD.isEmpty.name())) {
          if (args != null && args.length > 0) {
            throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
          }
          return list.isEmpty();
        } else if (method.equals(LIST_METHOD.iterator.name())) {
          if (args != null) {
            throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
          }
          return list.iterator();
        } else if (method.equals(LIST_METHOD.toArray.name())) {
          if (args != null) {
            throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
          }
          return list.toArray();
        } else if (method.equals(LIST_METHOD.clear.name())) {
          if (args != null && args.length > 0) {
            throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
          }
          list.clear();
          return Void.class;
        } else if (method.equals(LIST_METHOD.get.name())) {
          if (args == null || args.length != 1) {
            throw new Exception("\"" + method + "\" of Class " + classType.name() + " has only 1 argument");
          }
          return list.get(((Number) args[0]).intValue());

        } else if (method.equals(LIST_METHOD.contains.name())) {
          if (args == null || args.length != 1) {
            throw new Exception("\"" + method + "\" of Class " + classType.name() + " has only 1 argument");
          }
          return list.contains(args[0]);
        } else if (method.equals(LIST_METHOD.remove.name())) {
          if (args == null || args.length != 1) {
            throw new Exception("\"" + method + "\" of Class " + classType.name() + " has only 1 argument");
          }
          if (args[0] instanceof Integer || args[0] instanceof Long || args[0] instanceof Short) {
            return list.remove(((Number) args[0]).intValue());
          } else {
            return list.remove(args[0]);
          }
        } else if (method.equals(LIST_METHOD.lastIndexOf.name())) {
          if (args == null || args.length != 1) {
            throw new Exception("\"" + method + "\" of Class " + classType.name() + " has only 1 argument");
          }
          return list.lastIndexOf(args[0]);
        } else if (method.equals(LIST_METHOD.indexOf.name())) {
          if (args == null || args.length != 1) {
            throw new Exception("\"" + method + "\" of Class " + classType.name() + " has only 1 argument");
          }
          return list.indexOf(args[0]);
        }
        break;
      // ----
      case Writer:
        return executeWriterMethod(method, args);
      // ----
      case Reader:
        return executeReaderMethod(method, args);
    }
    throw new Exception("\"" + method + "\" of Class " + classType.name() + " is not implemented");
  }

  public String ciGetName() {
    return name;
  }

  public void ciSetName(String name) throws Exception {
    if (!allClasses.containsKey(name)) {
      throw new Exception("There is no inbuilt type " + name);
    }
    this.classType = allClasses.get(name);
    name = classType.name();
    switch (classType) {
      case List:
        list = new ArrayList();
        break;
    }
  }

  public String[] ciGetAllMethods() {
    Object[] obj;
    String[] methods;
    obj = classMethods.get(classType).toArray();
    methods = new String[obj.length];
    for (int i = 0; i < obj.length; i++) {
      methods[i] = obj[i].toString();
    }
    return methods;
  }

  public boolean ciHasMethod(String methodName) {
    try {
      switch (classType) {
        case List:
          LIST_METHOD.valueOf(methodName);
          return true;
        //return classMethods.get(classType).contains(methodName);
        case Writer:
          WRITER_METHOD.valueOf(methodName);
          return true;
        //return classMethods.get(classType).contains(methodName);
        case Reader:
          READER_METHOD.valueOf(methodName);
          return true;
      }
    } catch (Exception ex) {
    }
    return false;
  }

  public void ciSetExpressionParser(ExpressionParser expressionParser) {

  }

  public Object executeWriterMethod(String method, Object[] args) throws Exception {
    WRITER_METHOD wm = WRITER_METHOD.valueOf(method);
    switch (wm) {
      case close:
        if (args != null && args.length > 0) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
        }
        bufferedWriter.close();
        return Void.class;

      case flush:
        if (args != null && args.length > 0) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
        }
        bufferedWriter.flush();
        return Void.class;

      case newLine:
        if (args != null && args.length > 0) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
        }
        bufferedWriter.newLine();
        return Void.class;

      case write:
        if (args == null || args.length == 2) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " must have either 1 or 3 arguments");
        }
        if (args.length > 3) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " cannot have more than 3 arguments");
        }
        // --- 
        if (args.length == 1) {
          if (args[0] instanceof char[]) {
            bufferedWriter.write((char[]) args[0]);
          } else if (args[0] instanceof Integer || args[0] instanceof Long || args[0] instanceof Short) {
            bufferedWriter.write(((Number) args[0]).intValue());
          } else {
            bufferedWriter.write(args[0].toString());
          }
          return Void.class;
        }
    }
    throw new Exception("\"" + method + "\" of Class " + classType.name() + " is not implemented");
  }

  public Object executeReaderMethod(String method, Object[] args) throws Exception {
    READER_METHOD rm = READER_METHOD.valueOf(method); //mark, read, ready, reset, skip
    switch (rm) {
      case close:
        if (args != null && args.length > 0) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
        }
        bufferedReader.close();
        return Void.class;

      case markSupported:
        if (args != null && args.length > 0) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
        }
        return bufferedReader.markSupported();

      case ready:
        if (args != null && args.length > 0) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
        }
        return bufferedReader.ready();

      case reset:
        if (args != null && args.length > 0) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
        }
        bufferedReader.reset();
        return Void.class;

      case readLine:
        if (args != null && args.length > 0) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " has no argument(s)");
        }
        return bufferedReader.readLine();

      case mark:
        if (args != null || args.length != 1 || args[0] == null) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " must have one argument");
        }
        if (args[0] instanceof Number) {
          bufferedReader.mark(((Number) args[0]).intValue());
          return Void.class;
        }
        int readAheadLimit = Integer.parseInt(args[0].toString());
        bufferedReader.mark(readAheadLimit);
        return Void.class;

      case skip:
        if (args != null || args.length != 1 || args[0] == null) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " must have one argument");
        }
        if (args[0] instanceof Number) {
          return bufferedReader.skip(((Number) args[0]).longValue());
        }
        long n = Long.parseLong(args[0].toString());
        return bufferedReader.skip(n);

      case read:
        if (args == null || args.length == 0) {
          return bufferedReader.read();
        }

        if (args == null || args.length == 1) {
          if (args[0] == null) {
            throw new Exception("\"" + method + "\"(buffer) of Class " + classType.name() + ":  buffer must be non-zero");
          }
          if (args[0] instanceof char[]) {
            return bufferedReader.read((char[]) args[0]);
          } else if (args[0] instanceof CharBuffer) {
            return bufferedReader.read((CharBuffer) args[0]);
          }
          throw new Exception("\"" + method + "\"(buffer) of Class " + classType.name() + ":  buffer must be of type "
              + "char[] or CharBuffer. Got: " + args[0].getClass().getCanonicalName());
        }
        // ---
        if (args.length != 3) {
          throw new Exception("\"" + method + "\" of Class " + classType.name() + " cannot can have 0, 1 or 3 arguments");
        }
        // --- 
        int off = ((Number) args[1]).intValue();
        int len = ((Number) args[2]).intValue();

        if (args[0] instanceof char[]) {
          return bufferedReader.read((char[]) args[0], off, len);
        }
        throw new Exception("\"" + method + "\"(char[] cbuf, int off, int len) of Class " + classType.name()
            + " check parameters type");

    }
    throw new Exception("\"" + method + "\" of Class " + classType.name() + " is not implemented");
  }
}

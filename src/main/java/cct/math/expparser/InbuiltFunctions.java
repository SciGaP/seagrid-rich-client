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

import static cct.math.expparser.ExpressionParser.DEGREES_TO_RADIANS;

/**
 *
 * @author vvv900
 */
public class InbuiltFunctions implements FunctionInterface {

  enum INBUILT_FUNCTION {

    print, PRINT, println, PRINTLN,
    atan2, ATAN2, cosh, COSH, abs, ABS, cbrt, CBRT, ceil, CEIL, exp, EXP, expm1, EXPM1,
    SQRT, sqrt, SIN, sin, COS, cos, TAN, tan, ASIN, asin, ACOS, acos, ATAN, atan, LOG, log
  }

  private String name;
  private INBUILT_FUNCTION type;
  private ExpressionParser expressionParser;

  public InbuiltFunctions() {

  }

  public Object fiEvaluateFunction(Object[] args) throws Exception {
    switch (type) {
      case print:
      case PRINT:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        System.out.print(args[0] == null ? args[0] : args[0].toString());
        return Void.class;
      case println:
      case PRINTLN:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        System.out.println(args[0] == null ? args[0] : args[0].toString());
        return Void.class;
      case atan2:
      case ATAN2:
        if (args.length != 2) {
          throw new Exception("Function " + name + " requires 2 arguments");
        }
        return Math.atan2(((Number) args[0]).doubleValue(), ((Number) args[1]).doubleValue());
      case cosh:
      case COSH:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        return Math.cosh(((Number) args[0]).doubleValue());
      case abs:
      case ABS:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        return Math.abs(((Number) args[0]).doubleValue());
      case cbrt:
      case CBRT:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        return Math.cbrt(((Number) args[0]).doubleValue());
      case ceil:
      case CEIL:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        return Math.ceil(((Number) args[0]).doubleValue());
      case exp:
      case EXP:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        return Math.exp(((Number) args[0]).doubleValue());
      case expm1:
      case EXPM1:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        return Math.expm1(((Number) args[0]).doubleValue());
      case sqrt:
      case SQRT:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        return Math.sqrt(((Number) args[0]).doubleValue());
      case sin:
      case SIN:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        if (expressionParser.isInRadians()) {
          return Math.sin(((Number) args[0]).doubleValue());
        } else {
          return Math.sin(((Number) args[0]).doubleValue() * DEGREES_TO_RADIANS);
        }
      case cos:
      case COS:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        if (expressionParser.isInRadians()) {
          return Math.cos(((Number) args[0]).doubleValue());
        } else {
          return Math.cos(((Number) args[0]).doubleValue() * DEGREES_TO_RADIANS);
        }
      case tan:
      case TAN:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        return Math.tan(((Number) args[0]).doubleValue());

      case asin:
      case ASIN:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        return Math.asin(((Number) args[0]).doubleValue());

      case acos:
      case ACOS:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        return Math.acos(((Number) args[0]).doubleValue());

      case atan:
      case ATAN:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        return Math.atan(((Number) args[0]).doubleValue());

      case log:
      case LOG:
        if (args.length != 1) {
          throw new Exception("Function " + name + " requires 1 argument");
        }
        return Math.log(((Number) args[0]).doubleValue());

    }
    throw new Exception("Unknown error");
  }

  public String fiGetName() {
    return name;
  }

  public void fiSetName(String name) {
    type = INBUILT_FUNCTION.valueOf(name);
    this.name = name;
  }

  public void fiSetExpressionParser(ExpressionParser expressionParser) {
    this.expressionParser = expressionParser;
  }
}

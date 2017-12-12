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

import static cct.math.expparser.ExpressionParser.logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vvv900
 */
public class Function {

  private String Name;
  private String internalName;
  private Map<String, Object> locParams = new HashMap<String, Object>();
  private List localParams = new ArrayList();
  private List<Variable> functionTokens;
  private List<SYMBOL_TYPE> functionTokenTypes;
  private ExpressionParser expressionParser;

  public Function(String definition, ExpressionParser expressionParser) throws Exception {
    this.expressionParser = expressionParser;
    defineFunction(definition);
  }

  void defineFunction(String definition) throws Exception {
    List<Variable> Tokens = new ArrayList<Variable>();
    List<SYMBOL_TYPE> tokenTypes = new ArrayList<SYMBOL_TYPE>();
    expressionParser.resolveTokens(definition, Tokens, tokenTypes);

    if (tokenTypes.size() < 3) {
      throw new Exception("Minimal Function definition should be: function_name()");
    }

    if (tokenTypes.size() % 2 == 1) {
      throw new Exception("Wrong Function definition");
    }

    if (tokenTypes.get(0) != SYMBOL_TYPE.FUNCTION) {
      throw new Exception("Function definition should start from function_name(");
    }

    if (tokenTypes.get(1) != SYMBOL_TYPE.LEFT_PARANTHESIS) {
      throw new Exception("Left paranthesis should follow function name");
    }

    Name = Tokens.get(0).getName();

    int index = 2;
    while (tokenTypes.get(index) != SYMBOL_TYPE.RIGHT_PARANTHESIS) {
      if (index >= tokenTypes.size() - 1) {
        break;
      }
      if (tokenTypes.get(index) != SYMBOL_TYPE.VARIABLE) {
        throw new Exception("Expecting parameter in Function definition, got " + tokenTypes.get(index).toString());
      }

      if (tokenTypes.get(index + 1) != SYMBOL_TYPE.COMMA && tokenTypes.get(index + 1) != SYMBOL_TYPE.RIGHT_PARANTHESIS) {
        throw new Exception("Expecting comma or right paranthesis in Function definition, got "
            + tokenTypes.get(index).toString());
      }

      if (locParams.containsKey(Tokens.get(index).getName())) {
        throw new Exception("Duplicate definition of parameter " + Tokens.get(index).getName());
      }

      if (Tokens.get(index).isNumber()) {
        throw new Exception("Parameter should be symbolic variable. Got " + Tokens.get(index).getName());
      }

      Tokens.get(index).setType(SYMBOL_TYPE.LOCAL_VARIABLE);
      locParams.put(Tokens.get(index).getName(), null);
      localParams.add(Tokens.get(index).getName());

      index += 2;
      if (index >= tokenTypes.size() - 1) {
        break;
      }

    }

    internalName = Name + "_" + String.valueOf(localParams.size());
    logger.info("Fun name: " + Name + " Internal name: " + internalName + " N params: " + localParams.size());
    for (int i = 0; i < localParams.size(); i++) {
      logger.info((i + 1) + " : " + localParams.get(i).toString());

    }
  }

  public String getInternalName() {
    return internalName;
  }

  public void setInternalName(String internalName) {
    this.internalName = internalName;
  }

  void setFunctionBody(String body) throws Exception {
    List<Variable> Tokens = new ArrayList<Variable>();
    List<SYMBOL_TYPE> tokenTypes = new ArrayList<SYMBOL_TYPE>();
    expressionParser.resolveTokens(body, Tokens, tokenTypes);
    if (Tokens.size() < 1) {
      throw new Exception("Empty function body");
    }
    for (int i = 0; i < Tokens.size(); i++) {
      if (tokenTypes.get(i) == SYMBOL_TYPE.VARIABLE && locParams.containsKey(Tokens.get(i).getName())) {
        Tokens.get(i).setType(SYMBOL_TYPE.LOCAL_VARIABLE);
        tokenTypes.set(i, SYMBOL_TYPE.LOCAL_VARIABLE);
        // ---
      } else if (tokenTypes.get(i) == SYMBOL_TYPE.VARIABLE) {
        boolean isNumber = Tokens.get(i).isNumber();
        String varName = Tokens.get(i).getName();
        if (expressionParser == null) {
          throw new Exception("Function body: expressionParser == null");
        } else if ( (!expressionParser.hasSymbol(varName)) && (!isNumber) ) {
          throw new Exception("Function body: " + body + "\nVariable " + varName + " is not defined");
        } else if (expressionParser.hasSymbol(varName)) {
          Variable type = (Variable) expressionParser.getSymbol(varName);
          Tokens.set(i, type);
        }
      }
    }

    functionTokens = Tokens;
      functionTokenTypes = tokenTypes;
  }

  public Object getValue(List<Object> arguments) throws Exception {
    Object value;
    for (int i = 0; i < functionTokens.size(); i++) {
      if (functionTokenTypes.get(i) == SYMBOL_TYPE.LOCAL_VARIABLE) {
        int index = localParams.indexOf(functionTokens.get(i).getName());
        if (index == -1) {
          throw new Exception("Internal Error: index == -1");
        }
        functionTokens.get(i).setValue(arguments.get(index));
      }
    }

    List<Variable> Tokens = new ArrayList<Variable>(functionTokens);
    List<SYMBOL_TYPE> tokenTypes = new ArrayList<SYMBOL_TYPE>(functionTokenTypes);

    value = expressionParser.evaluateExpression(Tokens, tokenTypes);
    return value;
  }

  public int getArgsNumber() {
    return localParams.size();
  }

  public Object executeCommand(Object[] args) throws Exception {
    return null;
  }

  public String getName() {
    return this.Name;
  }

  public void setName(String name) {
    this.Name = name;
  }

  public void setExpressionParser(ExpressionParser expressionParser) {
    this.expressionParser = expressionParser;
  }
}

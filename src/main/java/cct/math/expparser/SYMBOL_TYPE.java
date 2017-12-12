/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.math.expparser;

/**
 *
 * @author vvv900
 */
public enum SYMBOL_TYPE {

  VARIABLE, CLASS, LOCAL_VARIABLE, FUNCTION, OPERATION_ADD, OPERATION_SUB, OPERATION_MUL, OPERATION_DIV, OPERATION_POWER,
  LEFT_PARANTHESIS, RIGHT_PARANTHESIS,
  COMMA,
  Bitwise_left_shift, Bitwise_right_shift_with_sign_extension, Bitwise_right_shift_with_zero_extension,
  Relational_less_than,
  LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET
}

package cct.math.expparser;

/**
 *
 * @author vvv900
 */
public enum OPERATION {

  Unary_logical_negation, // Precedence = 13
  MULTIPLY, DEVIDE, POWER, MODULUS, // Precedence = 12
  ADD, SUBSTRACT, // Precedence = 11
  Bitwise_left_shift, Bitwise_right_shift_with_sign_extension, // Precedence = 10
  Bitwise_right_shift_with_zero_extension, // Precedence = 10
  Relational_less_than, Relational_less_than_or_equal, Relational_greater_than, // Precedence = 9
  Relational_greater_than_or_equal, Type_comparison, // Precedence = 9
  Relational_is_equal_to, Relational_is_not_equal_to, // Precedence = 8
  Bitwise_AND, // Precedence = 7
  Bitwise_exclusive_OR, // Precedence = 6  
  Bitwise_inclusive_OR, // Precedence = 5
  Logical_AND, // Precedence = 4
  Logical_OR, // Precedence = 3
  Ternary_conditional                                                           // Precedence = 2
}

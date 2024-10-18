package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.common.ConstraintException;

import edu.citadel.cprl.Symbol;
import edu.citadel.cprl.Token;
import edu.citadel.cprl.Type;

/**
 * The abstract syntax tree node for a not expression.  A not expression is a
 * unary expression of the form "not expr" (logical negation) or "~x" (bitwise
 * negation or one's complement).  A simple example would be "not isEmpty()".
 */
public class NotExpr extends UnaryExpr
  {
    /**
     * Construct a not expression with the specified operator and operand.
     */
    public NotExpr(Token operator, Expression operand)
      {
        super(operator, operand);
        var symbol = operator.symbol();
        assert symbol == Symbol.notRW || symbol == Symbol.bitwiseNot;

        if (symbol == Symbol.notRW)
            setType(Type.Boolean);
        else
            setType(Type.Integer);
      }

    @Override
    public void checkConstraints()
      {
// ...
      }

    @Override
    public void emit() throws CodeGenException
      {
// ...
      }
  }

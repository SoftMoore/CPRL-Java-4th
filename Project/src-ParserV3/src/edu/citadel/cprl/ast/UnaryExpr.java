package edu.citadel.cprl.ast;

import edu.citadel.cprl.Token;

/**
 * Base class for all unary expressions.  A unary expression is an expression
 * of the form "operator expression".  The expression following the operator
 * is called the operand.  A simple example would be "-x".
 */
public abstract class UnaryExpr extends Expression
  {
    private Token      operator;
    private Expression operand;

    /**
     * Construct a unary expression with the specified operator and operand.
     */
    public UnaryExpr(Token operator, Expression operand)
      {
        super(operator.position());
        this.operator = operator;
        this.operand  = operand;
      }

    /**
     * Returns the operator for this unary expression.
     */
    public Token operator()
      {
        return operator;
      }

    /**
     * Returns the operand for this unary expression.
     */
    public Expression operand()
      {
        return operand;
      }
  }

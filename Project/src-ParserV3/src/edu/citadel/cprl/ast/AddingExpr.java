package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.common.ConstraintException;
import edu.citadel.common.InternalCompilerException;

import edu.citadel.cprl.Token;
import edu.citadel.cprl.Type;

/**
 * The abstract syntax tree node for an adding expression.  An adding expression
 * is a binary expression where the operator is an adding operator ("+" or "-") or
 * an adding bitwise operator ("|" or "^").  A simple example would be "x + 5".
 */
public class AddingExpr extends BinaryExpr
  {
    /**
     * Construct an adding expression with the operator and the two operands.
     */
    public AddingExpr(Expression leftOperand, Token operator, Expression rightOperand)
      {
        super(leftOperand, operator, rightOperand);
        setType(Type.Integer);   // initialize type of the expression to Integer
        var symbol = operator.symbol();
        assert symbol.isAddingOperator();
      }

    @Override
    public void checkConstraints()
      {
        try
          {
            leftOperand().checkConstraints();
            rightOperand().checkConstraints();

            // adding expression valid only for integers

            if (leftOperand().type() != Type.Integer)
              {
                var errorMsg = "Left operand should have type Integer.";
                throw error(leftOperand().position(), errorMsg);
              }

            if (rightOperand().type() != Type.Integer)
              {
                var errorMsg = "Right operand should have type Integer.";
                throw error(rightOperand().position(), errorMsg);
              }
          }
        catch (ConstraintException ex)
          {
            errorHandler().reportError(ex);
          }
      }

    @Override
    public void emit() throws CodeGenException
      {
        leftOperand().emit();
        rightOperand().emit();

        switch (operator().symbol())
          {
            case plus       -> emit("ADD");
            case minus      -> emit("SUB");
            case bitwiseOr  -> emit("BITOR");
            case bitwiseXor -> emit("BITXOR");
            default ->
              {
                var errorPos = operator().position();
                var errorMsg = "Invalid adding operator.";
                throw new InternalCompilerException(errorPos, errorMsg);
              }
          }
      }
  }

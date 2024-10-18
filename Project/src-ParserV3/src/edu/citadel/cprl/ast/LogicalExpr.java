package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.common.ConstraintException;

import edu.citadel.cprl.Symbol;
import edu.citadel.cprl.Token;
import edu.citadel.cprl.Type;

/**
 * The abstract syntax tree node for a logical expression.  A logical expression
 * is a binary expression where the operator is either "and" or "or".  A simple
 * example would be "(x &gt; 5) and (y &lt; 0)".
 */
public class LogicalExpr extends BinaryExpr
  {
    // labels used during code generation for short-circuit version
    private String L1 = newLabel();   // label at start of right operand
    private String L2 = newLabel();   // label at end of logical expression

    /**
     * Construct a logical expression with the operator ("and" or "or")
     * and the two operands.
     */
    public LogicalExpr(Expression leftOperand, Token operator, Expression rightOperand)
      {
        super(leftOperand, operator, rightOperand);
        setType(Type.Boolean);
        assert operator.symbol().isLogicalOperator();
      }

    @Override
    public void checkConstraints()
      {
        try
          {
            leftOperand().checkConstraints();
            rightOperand().checkConstraints();

            if (leftOperand().type() != Type.Boolean)
              {
                var errorMsg = "Left operand for a logical expression "
                             + "should have type Boolean.";
                throw error(leftOperand().position(), errorMsg);
              }

            if (rightOperand().type() != Type.Boolean)
              {
                var errorMsg = "Right operand for a logical expression "
                             + "should have type Boolean.";
                throw error(rightOperand().position(), errorMsg);
              }
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    @Override
    public void emit() throws CodeGenException
      {
        // Uses short-circuit evaluation for logical expressions.
        if (operator().symbol() == Symbol.andRW)
          {
            // if left operand evaluates to true, branch
            // to code that will evaluate right operand
            leftOperand().emitBranch(true, L1);

            // otherwise, place "false" back on top of stack as value
            // for the compound "and" expression
            emit("LDCB " + FALSE);
          }
        else   // operatorSym must be Symbol.orRW
          {
            // if left operand evaluates to false, branch
            // to code that will evaluate right operand
            leftOperand().emitBranch(false, L1);

            // otherwise, place "true" back on top of stack as value
            // for the compound "or" expression
            emit("LDCB " + TRUE);
          }

        // branch to code following the expression
        emit("BR " + L2);

        emitLabel(L1);

        // evaluate the right operand and leave result on
        // top of stack as value for compound expression
        rightOperand().emit();

        emitLabel(L2);
      }
  }

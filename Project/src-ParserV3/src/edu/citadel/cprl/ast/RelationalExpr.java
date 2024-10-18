package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.common.ConstraintException;

import edu.citadel.cprl.Token;
import edu.citadel.cprl.Type;

/**
 * The abstract syntax tree node for a relational expression.  A relational
 * expression is a binary expression where the operator is a relational
 * operator such as "&lt;=" or "&gt;".  A simple example would be "x &lt; 5".
 */
public class RelationalExpr extends BinaryExpr
  {
    // labels used during code generation
    private String L1 = newLabel();   // label at start of right operand
    private String L2 = newLabel();   // label at end of the relational expression

    /**
     * Construct a relational expression with the operator ("=", "&lt;=", etc.)
     * and the two operands.
     */
    public RelationalExpr(Expression leftOperand, Token operator, Expression rightOperand)
      {
        super(leftOperand, operator, rightOperand);
        setType(Type.Boolean);
        assert operator.symbol().isRelationalOperator();
      }

    @Override
    public void checkConstraints()
      {
// ...
      }

    @Override
    public void emit() throws CodeGenException
      {
        emitBranch(false, L1);
        emit("LDCB " + TRUE);    // push true back on the stack
        emit("BR " + L2);        // jump over code to emit false
        emitLabel(L1);
        emit("LDCB " + FALSE);   // push false onto the stack
        emitLabel(L2);
      }

    @Override
    public void emitBranch(boolean condition, String label) throws CodeGenException
      {
        emitOperands();

        switch (operator().symbol())
          {
            case equals         -> emit(condition ? "BE "  + label : "BNE " + label);
            case notEqual       -> emit(condition ? "BNE " + label : "BE "  + label);
            case lessThan       -> emit(condition ? "BL "  + label : "BGE " + label);
            case lessOrEqual    -> emit(condition ? "BLE " + label : "BG "  + label);
            case greaterThan    -> emit(condition ? "BG "  + label : "BLE " + label);
            case greaterOrEqual -> emit(condition ? "BGE " + label : "BL "  + label);
            default ->
              {
                var position = operator().position();
                var errorMsg = "Invalid relational operator.";
                throw new CodeGenException(position, errorMsg);
              }
          }
      }

    private void emitOperands() throws CodeGenException
      {
        // Relational operators compare integers only, so we need to make sure that
        // we have enough bytes on the stack.  Pad with null bytes if necessary.
        for (int n = 1; n <= (Type.Integer.size() - leftOperand().type().size()); ++n)
            emit("LDCB 0");

        leftOperand().emit();

        for (int n = 1; n <= (Type.Integer.size() - rightOperand().type().size()); ++n)
            emit("LDCB 0");

        rightOperand().emit();
      }
  }

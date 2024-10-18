package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.common.ConstraintException;

import edu.citadel.cprl.Type;

/**
 * The abstract syntax tree node for an exit statement.
 */
public class ExitStmt extends Statement
  {
    private Expression whenExpr;
    private LoopStmt   loopStmt;   // nonstructural reference

    /**
     * Construct an exit statement with its optional "when"
     * expression (which should be null if there is no "when"
     * expression) and a reference to the enclosing loop statement.
     */
    public ExitStmt(Expression whenExpr, LoopStmt loopStmt)
      {
        this.whenExpr = whenExpr;
        this.loopStmt = loopStmt;
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

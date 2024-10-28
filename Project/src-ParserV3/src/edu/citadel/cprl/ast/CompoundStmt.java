package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;

import java.util.List;

/**
 * The abstract syntax tree node for a compound statement.
 */
public class CompoundStmt extends Statement
  {
    // the list of statements in the compound statement
    private List<Statement> statements;

    /**
     * Returns the list of statements in this compound statement.
     */
    public CompoundStmt(List<Statement> statements)
      {
        this.statements = statements;
      }

    public List<Statement> statements()
      {
        return statements;
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

package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.common.ConstraintException;

import edu.citadel.cprl.Type;
import edu.citadel.cprl.StringType;

/**
 * The abstract syntax tree node for a read statement.
 */
public class ReadStmt extends Statement
  {
    private Variable variable;

    /**
     * Construct a read statement with the specified variable for storing the input.
     */
    public ReadStmt(Variable variable)
      {
        this.variable = variable;
      }

    @Override
    public void checkConstraints()
      {
        // input is limited to integers, characters, and strings
// ...
      }

    @Override
    public void emit() throws CodeGenException
      {
        variable.emit();

        if (variable.type() instanceof StringType strType)
            emit("GETSTR " + strType.capacity());
        else if (variable.type() == Type.Integer)
            emit("GETINT");
        else   // type must be Char
            emit("GETCH");
      }
  }

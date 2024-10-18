package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.common.ConstraintException;

import edu.citadel.cprl.Type;
import edu.citadel.cprl.StringType;

import java.util.List;

/**
 * This class implements the abstract syntax tree for both write and writeln statements.
 */
public class OutputStmt extends Statement
  {
    private List<Expression> expressions;
    private boolean isWriteln;

    /**
     * Construct an output statement with the list of expressions and isWriteln flag.
     */
    public OutputStmt(List<Expression> expressions, boolean isWriteln)
      {
        this.expressions = expressions;
        this.isWriteln   = isWriteln;
      }

    /**
     * Construct an output statement with the list of expressions.
     * The isWriteln flag is initialized to false.
     */
    public OutputStmt(List<Expression> expressions)
      {
        this(expressions, false);
      }

    /**
     * Returns the list of expressions for this output statement.
     */
    public List<Expression> expressions()
      {
        return expressions;
      }

    @Override
    public void checkConstraints()
      {
        for (Expression expr : expressions)
          {
            expr.checkConstraints();

            try
              {
                var type = expr.type();

                if (!type.isScalar() && !(type instanceof StringType))
                  {
                    var errorMsg = "Output supported only for scalar types and strings.";
                    throw error(expr.position(), errorMsg);
                  }
              }
            catch (ConstraintException e)
              {
                errorHandler().reportError(e);
              }
          }
      }

    @Override
    public void emit() throws CodeGenException
      {
        for (Expression expr : expressions)
          {
            expr.emit();

            var type = expr.type();

            if (type == Type.Integer)
                emit("PUTINT");
            else if (type == Type.Boolean)
                emit("PUTBYTE");
            else if (type == Type.Char)
                emit("PUTCH");
            else   // must be a string type
                emit("PUTSTR " + ((StringType) type).capacity());
          }

        if (isWriteln)
            emit("PUTEOL");
      }
  }

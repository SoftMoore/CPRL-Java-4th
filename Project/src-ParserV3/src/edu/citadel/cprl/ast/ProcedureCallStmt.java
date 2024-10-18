package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.common.ConstraintException;

import edu.citadel.cprl.Token;
import edu.citadel.cprl.StringType;

import java.util.List;

/**
 * The abstract syntax tree node for a procedure call statement.
 */
public class ProcedureCallStmt extends Statement
  {
    private Token procId;
    private List<Expression> actualParams;

    // declaration of the procedure being called
    private ProcedureDecl procDecl;   // nonstructural reference

    /*
     * Construct a procedure call statement with the procedure name
     * (an identifier token) and the list of actual parameters being
     * passed as part of the call.
     */
    public ProcedureCallStmt(Token procId, List<Expression> actualParams)
      {
        this.procId = procId;
        this.actualParams = actualParams;
      }

    @Override
    public void checkConstraints()
      {
// ...
      }

    /**
     * Add "synthetic" padding parameter for string literals if needed.
     */
    private void addPadding()
      {
        var paramDecls = procDecl.parameterDecls();

        // can't use a for-loop here since the number of actual parameters
        // can change with the insertion of padding for string types
        int i = 0;
        int j = 0;

        while (i < paramDecls.size())
          {
            var paramDecl = paramDecls.get(i);
            var expr = actualParams.get(j);

            if (paramDecl.type() instanceof StringType stringType
                && expr instanceof ConstValue constValue
                && stringType.size() > constValue.size())
              {
                 var padding = new Padding(stringType.size() - constValue.size());
                 actualParams.add(++j, padding);
              }

            ++i;
            ++j;
          }
      }

    @Override
    public void emit() throws CodeGenException
      {
// ...   call addPadding before emitting any code
      }
  }

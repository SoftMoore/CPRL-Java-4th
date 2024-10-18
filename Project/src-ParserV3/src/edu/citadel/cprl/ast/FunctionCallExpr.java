package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.common.ConstraintException;

import edu.citadel.cprl.Token;
import edu.citadel.cprl.ArrayType;
import edu.citadel.cprl.StringType;

import java.util.List;

/**
 * The abstract syntax tree node for a function call expression.
 */
public class FunctionCallExpr extends Expression
  {
    private Token funId;
    private List<Expression> actualParams;

    // declaration of the function being called
    private FunctionDecl funDecl;   // nonstructural reference

    /**
     * Construct a function call expression with the function name (an identifier
     * token) and the list of actual parameters being passed as part of the call.
     */
    public FunctionCallExpr(Token funId, List<Expression> actualParams)
      {
        super(funId.position());
        this.funId = funId;
        this.actualParams = actualParams;
      }

    @Override
    public void checkConstraints()
      {
        try
          {
            // get the declaration for this function call from the identifier table
            var decl = idTable().get(funId.text());

            if (decl == null)
              {
                var errorMsg = "Function \"" + funId + "\" has not been declared.";
                throw error(funId.position(), errorMsg);
              }
            else if (!(decl instanceof FunctionDecl))
              {
                var errorMsg = "Identifier \"" + funId + "\" was not declared as a function.";
                throw error(funId.position(), errorMsg);
              }
            else
                funDecl = (FunctionDecl) decl;

            // at this point funDecl should not be null
            setType(funDecl.type());

            var paramDecls = funDecl.parameterDecls();

            // check that numbers of parameters match
            if (actualParams.size() != paramDecls.size())
              {
                var errorMsg = "Incorrect number of actual parameters.";
                throw error(funId.position(), errorMsg);
              }

            // check constraints for each actual parameter
            for (Expression expr : actualParams)
                expr.checkConstraints();

            for (int i = 0; i < actualParams.size(); ++i)
              {
                var expr = actualParams.get(i);
                var paramDecl = paramDecls.get(i);

                // check that parameter types match
                if (!matchTypes(paramDecl.type(), expr))
                    throw error(expr.position(), "Parameter type mismatch.");

                // check that variable expressions are passed for var parameters
                // (recall that arrays are passed as var parameters; checked in FunctionDecl)
                if (paramDecl.isVarParam() && paramDecl.type() instanceof ArrayType)
                  {
                    if (expr instanceof VariableExpr variableExpr)
                      {
                        // replace a variable expression by a variable
                        expr = new Variable(variableExpr);
                        actualParams.set(i, expr);
                      }
                    else
                      {
                        var errorMsg = "Expression for an array (var) parameter must be a variable.";
                        throw error(expr.position(), errorMsg);
                      }
                  }
              }
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    /**
     * Add "synthetic" padding parameter for string literals if needed.
     */
    private void addPadding()
      {
        var paramDecls = funDecl.parameterDecls();

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
        addPadding();

        // allocate space on the stack for the return value
        emit("ALLOC " + funDecl.type().size());

        // emit code for actual parameters
        for (Expression expr : actualParams)
            expr.emit();

        emit("CALL " + funDecl.subprogramLabel());
      }
  }

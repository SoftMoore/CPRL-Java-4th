package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;
import edu.citadel.cprl.ArrayType;
import edu.citadel.cprl.Token;

/**
 * The abstract syntax tree node for a procedure declaration.
 */
public class ProcedureDecl extends SubprogramDecl
  {
    /**
     * Construct a procedure declaration with its name (an identifier).
     */
    public ProcedureDecl(Token procId)
      {
        super(procId);
      }

    @Override
    public void checkConstraints()
      {
        for (var paramDecl : parameterDecls())
          {
            paramDecl.checkConstraints();

            // arrays are always passed as var params.
            if (paramDecl.type() instanceof ArrayType)
                paramDecl.setVarParam(true);
          }

        for (var decl : initialDecls())
            decl.checkConstraints();

        for (var statement : statements())
            statement.checkConstraints();
      }

    @Override
    public void emit() throws CodeGenException
      {
        setRelativeAddresses();
        emitLabel(subprogramLabel());

        // no need to emit PROC instruction if varLength == 0
        if (varLength() > 0)
            emit("PROC " + varLength());

        for (var decl : initialDecls())
            decl.emit();

        for (var statement : statements())
            statement.emit();

        emit("RET " + paramLength());   // required for procedures
      }
  }

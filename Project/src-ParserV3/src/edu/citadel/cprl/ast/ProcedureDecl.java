package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;

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

    // inherited checkConstraints() is sufficient

    @Override
    public void emit() throws CodeGenException
      {
        setRelativeAddresses();
        emitLabel(subprogramLabel());

        // no need to emit PROC instruction if varLength == 0
        if (varLength() > 0)
            emit("PROC " + varLength());

        for (InitialDecl decl : initialDecls())
            decl.emit();

        for (Statement statement : statements())
            statement.emit();

        emit("RET " + paramLength());   // required for procedures
      }
  }

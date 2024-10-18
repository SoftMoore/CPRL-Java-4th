package edu.citadel.cprl.ast;

import edu.citadel.common.CodeGenException;

import edu.citadel.cprl.ScopeLevel;
import edu.citadel.cprl.Token;
import edu.citadel.cprl.Type;

import java.util.List;
import java.util.ArrayList;

/**
 * The abstract syntax tree node for a variable declaration.  Note that a variable
 * declaration is simply a container for a list of single variable declarations.
 */
public class VarDecl extends InitialDecl
  {
    // the list of single variable declarations for this variable declaration
    private List<SingleVarDecl> singleVarDecls;

    /**
     * Construct a variable declaration with its list of identifier tokens,
     * type, initializer, and scope level
     */
    public VarDecl(List<Token> identifiers, Type varType,
                   Initializer initializer, ScopeLevel scopeLevel)
      {
        super(new Token(), varType);
        singleVarDecls = new ArrayList<>(identifiers.size());

        for (Token id : identifiers)
            singleVarDecls.add(new SingleVarDecl(id, varType, initializer, scopeLevel));
      }

    /**
     * Returns the list of single variable declarations for this variable declaration.
     */
    public List<SingleVarDecl> singleVarDecls()
      {
        return singleVarDecls;
      }

    @Override
    public void checkConstraints()
      {
        for (SingleVarDecl singleVarDecl : singleVarDecls)
            singleVarDecl.checkConstraints();
      }

    @Override
    public void emit() throws CodeGenException
      {
        for (SingleVarDecl singleVarDecl : singleVarDecls)
            singleVarDecl.emit();
      }
  }

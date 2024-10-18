package edu.citadel.cprl.ast;

import edu.citadel.cprl.ScopeLevel;
import edu.citadel.cprl.Type;

/**
 * Interface for a variable declaration, which can be either a
 * "single" variable declaration or a parameter declaration.
 */
public sealed interface VariableDecl permits SingleVarDecl, ParameterDecl
  {
    /**
     * Returns the type of this declaration.
     */
    public Type type();

    /**
     * Returns the size (number of bytes) of the variable
     * declared with this declaration.
     */
    public int size();

    /**
     * Returns the scope level for this declaration.
     */
    public ScopeLevel scopeLevel();

    /**
     * Sets the relative address (offset) of the variable
     * declared with this declaration.
     */
    public void setRelAddr(int relAddr);

    /**
     * Returns the relative address (offset) of the variable
     * declared with this declaration.
     */
    public int relAddr();
  }

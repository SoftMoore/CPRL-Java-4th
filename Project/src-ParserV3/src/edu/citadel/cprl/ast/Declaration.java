package edu.citadel.cprl.ast;

import edu.citadel.common.Position;
import edu.citadel.common.CodeGenException;

import edu.citadel.cprl.Token;
import edu.citadel.cprl.Type;

/**
 * Base class for all CPRL declarations (constants, variables, procedures, etc.).
 */
public abstract class Declaration extends AST
  {
    private Token idToken;
    private Type  type;

    /**
     * Construct a declaration with its identifier token and type.
     */
    public Declaration(Token idToken, Type type)
      {
        this.idToken = idToken;
        this.type    = type;
      }

    /**
     * Construct a declaration with its identifier token.  The type for the
     * declaration is initialized to Type.none (e.g. for procedures).
     */
    public Declaration(Token idToken)
      {
        this(idToken, Type.none);
      }

    /**
     * Returns the identifier token for this declaration.
     */
    public Token idToken()
      {
        return idToken;
      }

    /**
     * Returns the type of this declaration.
     */
    public Type type()
      {
        return type;
      }

    /**
     * Sets the type for this declaration.
     */
    public void setType(Type type)
      {
        this.type = type;
      }

    /**
     * Returns the position of this declaration, which is simply
     * the position of the identifier token.
     */
    public Position position()
      {
        return idToken.position();
      }

    // Note: Many declarations do not require code generation.
    // A default implementation is provided for convenience.

    @Override
    public void emit() throws CodeGenException
      {
        // nothing to do for most declarations
      }
  }

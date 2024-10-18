package edu.citadel.cprl.ast;

import edu.citadel.common.ConstraintException;
import edu.citadel.common.util.IntUtil;

import edu.citadel.cprl.Symbol;
import edu.citadel.cprl.Token;
import edu.citadel.cprl.Type;

/**
 * The abstract syntax tree node for a constant declaration.
 */
public class ConstDecl extends InitialDecl
  {
    private Token literal;

    /**
     * Construct a constant declaration with its identifier, type, and literal.
     */
    public ConstDecl(Token identifier, Type constType, Token literal)
      {
        super(identifier, constType);
        this.literal = literal;
      }

    public Token literal()
      {
        return literal;
      }

    @Override
    public void checkConstraints()
      {
// ...
      }
  }

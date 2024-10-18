package edu.citadel.assembler;

import edu.citadel.common.Position;

/**
* This class encapsulates the properties of a language token.  A token
* consists of a symbol (a.k.a., the token type), a position, and a string
* that contains the text of the token.
*/
public final class Token
  {
    private Symbol   symbol;
    private Position position;
    private String   text;

    /**
     * Constructs a new token with the given symbol, position, and text.
     */
    public Token(Symbol symbol, Position position, String text)
      {
        this.symbol   = symbol;
        this.position = position;
        this.text     = text == null || text.length() == 0 ? symbol.toString() : text;
      }

    /**
     * Constructs a new Token with the specified symbol.  Position
     * and text are initialized to default values.  This constructor
     * is useful when replacing instructions during optimization.
     */
    public Token(Symbol symbol)
      {
        this(symbol, new Position(), "");
      }

    /**
     * Constructs a new Token with the specified symbol and text.
     * Position is initialized to the default value.  This constructor
     * is useful when replacing instructions during optimization.
     */
    public Token(Symbol symbol, String text)
      {
        this(symbol, new Position(), text);
      }

    /**
     * Returns the token's symbol.
     */
    public Symbol symbol()
      {
        return symbol;
      }

    /**
     * Returns the token's position within the source file.
     */
    public Position position()
      {
        return position;
      }

      /**
       * Returns the string representation for the token.
       */
      public String text()
        {
          return text;
        }

    /**
     * Set the string representation for the token.
     */
    public void setText(String text)
      {
        this.text = text;
      }

    @Override
    public String toString()
      {
        return text();
      }
  }

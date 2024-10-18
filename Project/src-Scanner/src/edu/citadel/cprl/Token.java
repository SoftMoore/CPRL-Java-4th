package edu.citadel.cprl;

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
     * Construct a new token with symbol = Symbol.unknown.
     * Position and text are initialized to default values.
     */
    public Token()
      {
        this(Symbol.unknown, new Position(), "");
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

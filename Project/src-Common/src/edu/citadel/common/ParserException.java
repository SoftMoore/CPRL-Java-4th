package edu.citadel.common;

/**
 * Class for exceptions encountered during parsing.
 */
public class ParserException extends CompilerException
  {
    private static final long serialVersionUID = -6997169373446585998L;

    /**
     * Construct a ParserException with the specified position and error message.
     *
     * @param position The position in the source file where the error was detected.
     * @param errorMsg A brief message about the nature of the error.
     */
    public ParserException(Position position, String errorMsg)
      {
        super("Syntax", position, errorMsg);
      }
  }

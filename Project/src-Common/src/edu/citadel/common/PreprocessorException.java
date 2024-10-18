package edu.citadel.common;

/**
 * Class for exceptions encountered during preprocessing phase of the assembler.
 */
public class PreprocessorException extends Exception
  {
    private static final long serialVersionUID = 5714933818029616070L;

    /**
     * Construct a preprocessor exception with the specified message and line number.
     *
     * @param errorMsg  A brief message about the nature of the error.
     * @param lineNum   The line number in the source file where the error was detected.
     */
    public PreprocessorException(String errorMsg, int lineNum)
      {
        super("*** Preprocessor error detected on line " + lineNum + ":\n    " + errorMsg);
      }
  }

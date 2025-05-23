package edu.citadel.common;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * This class handles the reporting of error messages.
 */
public class ErrorHandler
  {
    // Maximum number of errors to be reported.
    private static final int MAX_ERRORS = 15;

    private PrintWriter err = new PrintWriter(System.err, true, StandardCharsets.UTF_8);
    private int errorCount  = 0;
    private String lastMessage = "";   // remember last error message
    private Set<String> undeclaredIds = new HashSet<>();

    /**
     * Returns true if errors have been reported by the error handler.
     */
    public boolean errorsExist()
      {
        return errorCount > 0;
      }

    /**
     * Reports the error.
     *
     * @throws FatalException Thrown if the number of errors exceeds the maximum.
     */
    public void reportError(CompilerException e)
      {
        var message = e.getMessage() != null ? e.getMessage() : "";
        if (errorCount <= MAX_ERRORS)
          {
            if (shouldPrint(message))
              {
                printMessage(message);
                ++errorCount;
                lastMessage = message;
              }
          }
        else
            throw new FatalException("Max errors exceeded.");
      }

    /**
     * Reports the fatal error.
     */
    public void reportFatalError(FatalException e)
      {
        err.println(e.getMessage());
      }

    /**
     * Prints the specified message and continues compilation.
     */
    public void printMessage(String message)
      {
        err.println(message);
      }

    /*
     * Checks for repeated error messages and error messages of
     * the form "Identifier \"x\" has not been declared.".
     * Returns true if this error message should be printed.
     */
    private boolean shouldPrint(String message)
      {
        if (message.isEmpty() || message.equals(lastMessage))
            return false;

        lastMessage = message;

        // check for messages of the form "Identifier \"x\" has not been declared."
        int endIndex = message.indexOf("\" has not been declared.");
        if (endIndex < 0)
            return true;

        int beginIndex = message.indexOf('\"') + 1;
        if (beginIndex < endIndex)
          {
            String idName = message.substring(beginIndex, endIndex);
            if (undeclaredIds.contains(idName))
                return false;
            else
              {
                undeclaredIds.add(idName);
                return true;
              }
          }

        return true;
      }
  }

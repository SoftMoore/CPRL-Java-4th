package edu.citadel.common.util;

/**
 * Utilities for recognizing several character classes.
 */
public class CharUtil
  {
    /**
     * Returns true only if the specified character is a letter.<br>
     * <code>'A'..'Z' + 'a'..'z' (r.e. char class: [A-Za-z])</code>
     */
    public static boolean isLetter(int ch)
      {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
      }

    /**
     * Returns true only if the specified character is a digit.<br>
     * <code>'0'..'9' (r.e. char class: [0-9])</code>
     */
    public static boolean isDigit(int ch)
      {
        return ch >= '0' && ch <= '9';
      }

    /**
     * Returns true only if the specified character is a letter or a digit.<br>
     * <code>'A'..'Z' + 'a'..'z + '0'..'9' (r.e. char class: [A-Za-z0-9])</code>
     */
    public static boolean isLetterOrDigit(int ch)
      {
        return isLetter(ch) || isDigit(ch);
      }

    /**
     * Returns true only if the specified character is a binary digit.<br>
     * <code>'0' or '1'</code>
     */
    public static boolean isBinaryDigit(int ch)
      {
        return ch == '0' || ch == '1';
      }

    /**
     * Returns true only if the specified character is a hex digit.<br>
     * <code>'0'..'9' + 'A'..'F' + 'a'..'f'</code>
     */
    public static boolean isHexDigit(int ch)
      {
        return (ch >= '0' && ch <= '9')
            || (ch >= 'a' && ch <= 'f')
            || (ch >= 'A' && ch <= 'F');
      }

    /*
     * Returns true if the specified character is an escaped character.
     */
    public static boolean isEscapeChar(int ch)
      {
        return ch == '\t' || ch == '\n' || ch == '\r'
            || ch == '\"' || ch == '\'' || ch == '\\';
      }

    /**
     * Returns the uppercase equivalent of the character, if any;
     * otherwise, returns the character itself.
     */
    public static char toUpperCase(int ch)
      {
        return ch >= 'a' && ch <= 'z' ? (char)(ch - 32) : (char) ch;
      }

    /**
     * Unescapes characters.  For example, if parameter ch is a tab,
     * this method will return "\t"
     *
     * @return The string for an escaped character.
     */
    public static String unescapedChar(int ch)
      {
        return switch (ch)
          {
            case '\t' -> "\\t";    // tab
            case '\n' -> "\\n";    // newline
            case '\r' -> "\\r";    // carriage return
            case '\"' -> "\\\"";   // double quote
            case '\'' -> "\\\'";   // single quote
            case '\\' -> "\\\\";   // backslash
            default   -> Character.toString(ch);
          };
      }
  }

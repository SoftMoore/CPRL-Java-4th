package edu.citadel.cprl;

import edu.citadel.common.*;
import edu.citadel.common.util.CharUtil;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

/**
 * Performs lexical analysis for the CPRL programming language.
 * Implements k tokens of lookahead.
 */
public final class Scanner
  {
    private Source source;
    private ErrorHandler errorHandler;

    // buffer to hold lookahead tokens
    private BoundedBuffer<Token> tokenBuffer;

    // buffer to hold identifiers and literals
    private StringBuilder scanBuffer = new StringBuilder(100);

    /**
     * Construct a scanner with its associated source file, number of
     * lookahead tokens, and error handler.
     */
    public Scanner(File sourceFile, int k, ErrorHandler errorHandler) throws IOException
      {
        var fileReader = new FileReader(sourceFile, StandardCharsets.UTF_8);
        var reader = new BufferedReader(fileReader);
        source = new Source(reader);
        tokenBuffer  = new BoundedBuffer<>(k);
        this.errorHandler = errorHandler;

        // fill token buffer with k tokens
        for (int i = 0; i < k; ++i)
            advance();
      }

    /**
     * The current token; equivalent to lookahead(1).
     */
    public Token token()
      {
        return lookahead(1);
      }

    /**
     * The current symbol; equivalent to lookahead(1).symbol().
     */
    public Symbol symbol()
      {
        return lookahead(1).symbol();
      }

    /**
     * The current text; equivalent to lookahead(1).text().
     */
    public String text()
      {
        return lookahead(1).text();
      }

    /**
     * The current position; equivalent to lookahead(1).position().
     */
    public Position position()
      {
        return lookahead(1).position();
      }

    /**
     * Returns the ith lookahead token.  Valid parameter values are in the
     * range 1..k; i.e., the first (current) lookahead token is lookahead(1).
     */
    public Token lookahead(int i)
      {
        assert i >= 1 && i <= tokenBuffer.capacity() : "Range check for lookahead token index";
        return tokenBuffer.get(i - 1);
      }

    /**
     * Advance the scanner one token.
     */
    public void advance() throws IOException
      {
        tokenBuffer.add(nextToken());
      }

    /**
     * Advance until the current symbol matches the symbol specified
     * in the parameter or until end of file is encountered.
     */
    public void advanceTo(Symbol symbol) throws IOException
      {
        while (symbol() != symbol && symbol() != Symbol.EOF)
            advance();
      }

    /**
     * Advance until the current symbol matches one of the symbols
     * in the given set or until end of file is encountered.
     */
    public void advanceTo(Set<Symbol> symbols) throws IOException
      {
        while (!symbols.contains(symbol()) && symbol() != Symbol.EOF)
            advance();
      }

    /**
     * Returns the next token in the source file.
     */
    private Token nextToken() throws IOException
      {
        var symbol   = Symbol.unknown;
        var position = new Position();
        var text     = "";

        try
          {
            skipWhiteSpace();

            // currently at starting character of next token
            position = source.charPosition();

            if (source.currentChar() == Source.EOF)
              {
                // set symbol but don't advance source
                symbol = Symbol.EOF;
              }
            else if (CharUtil.isLetter(source.currentChar()))
              {
                var idString = scanIdentifier();
                symbol = getIdentifierSymbol(idString);

                if (symbol == Symbol.identifier)
                    text = idString;
              }
            else if (CharUtil.isDigit(source.currentChar()))
              {
                symbol = Symbol.intLiteral;
                text   = scanIntLiteral();
              }
            else
              {
                switch (source.currentChar())
                  {
                    case '+' ->
                      {
                        symbol = Symbol.plus;
                        source.advance();
                      }
// ...
                    case '/' ->
                      {
                        source.advance();
                        if (source.currentChar() == '/')
                          {
                            skipComment();
                            return nextToken();   // continue scanning for next token
                          }
                        else
                            symbol = Symbol.divide;
                      }
// ...
                    case '<' ->
                      {
                        source.advance();
                        if (source.currentChar() == '=')
                          {
                            symbol = Symbol.lessOrEqual;
                            source.advance();
                          }
                        else if (source.currentChar() == '<')
                          {
                            symbol = Symbol.leftShift;
                            source.advance();
                          }
                        else
                            symbol = Symbol.lessThan;
                      }
// ...
                    default ->
                      {
                        var errorMsg = "Invalid character \'" + ((char) source.currentChar()) + "\'";
                        source.advance();
                        throw error(errorMsg);
                      }
                  }
              }
          }
        catch (ScannerException e)
          {
            errorHandler.reportError(e);
            // set symbol to either EOF or unknown
            symbol = source.currentChar() == Source.EOF ? Symbol.EOF : Symbol.unknown;
          }

        return new Token(symbol, position, text);
      }

    /**
     * Returns the symbol associated with an identifier
     * (Symbol.arrayRW, Symbol.ifRW, Symbol.identifier, etc.)
     */
    private Symbol getIdentifierSymbol(String idString)
      {
// ...  Hint: Need an efficient search based on the text of the identifier (parameter idString)
      }

    /**
     * Skip over a comment.
     */
    private void skipComment() throws ScannerException, IOException
      {
        // assumes that source.currentChar() is the second '/' of the comment
        assert source.currentChar() == '/';

        while (source.currentChar() != '\n' && source.currentChar() != Source.EOF)
            source.advance();

        // continue scanning
        source.advance();
      }

    /**
     * Clear the scan buffer (makes it empty).
     */
    private void clearScanBuffer()
      {
        scanBuffer.delete(0, scanBuffer.length());
      }

    /**
     * Scans characters in the source file for a valid identifier using the lexical rule<br>
     * <code>identifier = letter { letter | digit } .</code>
     *
     * @return the string of letters and digits for the identifier.
     */
    private String scanIdentifier() throws IOException
      {
        // assumes that source.currentChar() is the first letter of the identifier
        assert CharUtil.isLetter(source.currentChar());
// ...
      }

    /**
     * Scans characters in the source file for a valid integer literal.
     * Supports binary and hex notation for integer literals.  Assumes that
     * source.currentChar() is the first character of the Integer literal.
     *
     * @return the string of digits for the integer literal.
     */
    private String scanIntLiteral() throws ScannerException, IOException
      {
        // assumes that source.currentChar() is the first digit of the integer literal
        assert CharUtil.isDigit(source.currentChar());

        clearScanBuffer();

        // append the leading digit character
        char firstChar = (char) source.currentChar();
        scanBuffer.append(firstChar);
        source.advance();

        if (firstChar == '0')
          {
            char secondChar = (char) source.currentChar();

            if (secondChar == 'x' || secondChar == 'X')
              {
                scanBuffer.append(CharUtil.toUpperCase(secondChar));
                source.advance();
                scanHexLiteral();
              }
            else if (secondChar == 'b' || secondChar == 'B')
              {
                scanBuffer.append(CharUtil.toUpperCase(secondChar));
                source.advance();
                scanBinaryLiteral();
              }
            else
                scanDecimalLiteral();
          }
        else
            scanDecimalLiteral();

        return scanBuffer.toString();
      }

    /**
     * Scans characters in the source file for a valid decimal (base 10)
     * integer literal using the rules:<br>
     * <code>decimalLiteral = digit { digit } .<br>
     * digit = '0'..'9' .</code>
     */
    private void scanDecimalLiteral() throws IOException
      {
        // assumes that scanBuffer starts with a digit
        assert CharUtil.isDigit(scanBuffer.charAt(0));

        while (CharUtil.isDigit(source.currentChar()))
          {
            scanBuffer.append((char) source.currentChar());
            source.advance();
          }
      }

    /**
     * Scans characters in the source file for a valid binary (base 2)
     * integer literal using the rules:<br>
     * <code>binaryLiteral = [ "0b" | "0B ] binaryDigit { binaryDigit } .<br>
     * binaryDigit = '0'..'1' .</code>
     */
    private void scanBinaryLiteral() throws ScannerException, IOException
      {
        // assumes that scanBuffer contains "0B"
        assert scanBuffer.charAt(0) == '0' && scanBuffer.charAt(1) == 'B';

        // check that the next character is a binary digit
        if (!CharUtil.isBinaryDigit(source.currentChar()))
            throw error("Improperly formed binary literal.");

        do
          {
            scanBuffer.append((char) source.currentChar());
            source.advance();
          }
        while (CharUtil.isBinaryDigit(source.currentChar()));
      }

    /**
     * Scans characters in the source file for a valid hexadecimal (base 16)
     * integer literal using the rules:<br>
     * <code>hexLiteral = [ "0x" | "0X" ] hexDigit { hexDigit} .<br>
     * hexDigit = '0'..'9' + 'A'..'F' + 'a'..'f' .</code>
     */
    private void scanHexLiteral() throws ScannerException, IOException
      {
// ...
      }

    /**
     * Scan characters in the source file for a String literal.  Escaped
     * characters are not converted; e.g., '\t' is not converted to the tab
     * character since the assembler performs the conversion.  Assumes that
     * source.currentChar() is the opening double quote (") of the String literal.
     *
     * @return the string of characters for the string literal, including
     *         opening and closing quotes
     */
    private String scanStringLiteral() throws ScannerException, IOException
      {
        // assumes that source.currentChar() is the opening double quote for the string literal
        assert source.currentChar() == '\"';
// ...
      }

    /**
     * Scan characters in the source file for a Char literal.  Escaped
     * characters are not converted; e.g., '\t' is not converted to the tab
     * character since the assembler performs that conversion.  Assumes that
     * source.currentChar() is the opening single quote (') of the Char literal.
     *
     * @return the string of characters for the char literal, including
     *         opening and closing single quotes.
     */
    private String scanCharLiteral() throws ScannerException, IOException
      {
        // assumes that source.currentChar() is the opening single quote for the char literal
        assert source.currentChar() == '\'';

        var errorMsg = "Invalid Char literal.";
        clearScanBuffer();

        // append the opening single quote
        char c = (char) source.currentChar();
        scanBuffer.append(c);
        source.advance();

        checkGraphicChar(source.currentChar());
        c = (char) source.currentChar();

        if (c == '\\')   // escaped character
            scanBuffer.append(scanEscapedChar());
        else if (c == '\'')
          {
            // either '' (empty) or '''; both are invalid
            source.advance();
            c = (char) source.currentChar();

            if (c == '\'')   // three single quotes in a row
                source.advance();

            throw error(errorMsg);
          }
        else
          {
            scanBuffer.append(c);
            source.advance();
          }

        c = (char) source.currentChar();   // should be the closing single quote
        checkGraphicChar(c);

        if (c == '\'')
          {
            scanBuffer.append(c);      // append the closing quote
            source.advance();
          }
        else
            throw error(errorMsg);

        return scanBuffer.toString();
      }

    /**
     * Scans characters in the source file for an escaped character; i.e.,
     * a character preceded by a backslash.  This method checks escape
     * characters \t, \n, \r, \", \', and \\.  If the character following
     * a backslash is anything other than one of these characters, then
     * an error is reported.  Note that the escaped character sequence is
     * returned unmodified; i.e., \t returns "\t", not the tab character.
     * Assumes that source.currentChar() is the escape character (\).
     *
     * @return the escaped character sequence unmodified.
     */
    private String scanEscapedChar() throws ScannerException, IOException
      {
        // assumes that source.currentChar() is the backslash for the escaped char
        assert source.currentChar() == '\\';

        // Need to save current position for error reporting.
         var backslashPosition = source.charPosition();

        source.advance();
        checkGraphicChar(source.currentChar());
        char c = (char) source.currentChar();

        source.advance();   // leave source at second character following backslash

        switch (c)
          {
            case 't':  return "\\t";    // tab
            case 'n':  return "\\n";    // newline
            case 'r':  return "\\r";    // carriage return
            case '\"': return "\\\"";   // double quote
            case '\'': return "\\\'";   // single quote
            case '\\': return "\\\\";   // backslash
            default:
              {
                // report error but return the invalid character
                var ex = error(backslashPosition, "Illegal escape character.");
                errorHandler.reportError(ex);
                return "\\" + c;
              }
          }
      }

    /**
     * Fast skip over white space.
     */
    private void skipWhiteSpace() throws IOException
      {
        while (Character.isWhitespace((char) source.currentChar()))
            source.advance();
      }

    /**
     * Checks that the integer represents a graphic character in the
     * Unicode Basic Multilingual Plane (BMP).
     *
     * @throws ScannerException if the integer does not represent a
     *         BMP graphic character.
     */
    private void checkGraphicChar(int n) throws ScannerException
      {
        if (n == Source.EOF)
            throw error("End of file reached before closing quote for Char or String literal.");
        else if (n > 0xffff)
            throw error("Character not in Unicode Basic Multilingual Pane (BMP)");
        else
          {
            if (n == '\r' || n == '\n')           // special check for end of line
                throw error("Char and String literals can not extend past end of line.");
            else if (Character.isISOControl(n))   // Sorry. No ISO control characters.
                throw error("Control characters not allowed in Char or String literal.");
          }
      }

    /**
     * Returns a scanner exception with the specified error message
     * and current token position.
     */
    private ScannerException error(String errorMsg)
      {
        return error(source.charPosition(), errorMsg);
      }

    /**
     * Returns a scanner exception with the specified error message
     * and token position.
     */
    private ScannerException error(Position position, String errorMsg)
      {
        return new ScannerException(position, errorMsg);
      }
  }

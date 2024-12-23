package edu.citadel.assembler;

import edu.citadel.common.ErrorHandler;
import edu.citadel.common.Position;
import edu.citadel.common.ScannerException;
import edu.citadel.common.Source;
import edu.citadel.common.util.CharUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;

/**
 * Performs lexical analysis for CVM assembly language.
 */
public final class Scanner
  {
    private Source source;
    private ErrorHandler errorHandler;

    private StringBuilder scanBuffer;

    // maps opcode names to opcode symbols
    private HashMap<String, Symbol> opcodeMap;

    //  The current token in the source file.
    private Token token;

    /**
     * Initializes the scanner with its associated source file and error handler.
     */
    public Scanner(File sourceFile, ErrorHandler errorHandler) throws IOException
      {
        var fileReader   = new FileReader(sourceFile, StandardCharsets.UTF_8);
        var reader  = new BufferedReader(fileReader);
        this.source = new Source(reader);
        this.errorHandler = errorHandler;
        scanBuffer = new StringBuilder(100);

        // initialize opcodeMap with reserved word symbols
        opcodeMap = new HashMap<>(100);
        for (Symbol symbol : Symbol.values())
          {
            if (symbol.isOpcode())
                opcodeMap.put(symbol.toString(), symbol);
          }

        advance();   // advance to the first token
      }

    /**
     * Returns the current token in the source file.
     */
    public Token token()
      {
        return token;
      }

    /**  Short version for token().symbol(), the next lookahead symbol. */
    public Symbol symbol()
      {
        return token.symbol();
      }

    /**  Short version for token().text(), the next lookahead text. */
    public String text()
      {
        return token.text();
      }

    /**  Short version for token().position(), the next lookahead position. */
    public Position position()
      {
        return token.position();
      }

    /**
     * Advance scanner to the next token.
     */
    public void advance() throws IOException
      {
        token = nextToken();
      }

    /**
     * Advance until the lookahead symbol matches one of the symbols
     * in the given set or until end of file is encountered.
     */
    public void advanceTo(Set<Symbol> symbols) throws IOException
      {
        while (!symbols.contains(token.symbol()) && token.symbol() != Symbol.EOF)
            advance();
      }

    /**
     * Returns the next token in the source file.
     */
    public Token nextToken() throws IOException
      {
        var symbol   = Symbol.UNKNOWN;
        var position = source.charPosition();
        var text     = "";

        try
          {
            skipWhiteSpace();

            // currently at starting character of next token
            position = source.charPosition();

            if (source.currentChar() == Source.EOF)
              {
                // set symbol but don't advance
                symbol = Symbol.EOF;
              }
            else if (CharUtil.isLetter((char) source.currentChar())
                  || source.currentChar() == '_')
              {
                // opcode symbol, identifier, or label
                String idString = scanIdentifier();
                symbol = getIdentifierSymbol(idString);

                if (symbol == Symbol.identifier)
                  {
                    // check to see if we have a label
                    if (source.currentChar() == ':')
                      {
                        symbol = Symbol.labelId;
                        text   = idString + ":";
                        source.advance();
                      }
                    else
                        text = idString;
                  }
              }
            else if (CharUtil.isDigit((char) source.currentChar()))
              {
                text   = scanIntegerLiteral();
                symbol = Symbol.intLiteral;
              }
            else
                switch ((char) source.currentChar())
                  {
                    case ';' ->
                      {
                        skipComment();
                        return nextToken();   // continue scanning for next token
                      }
                    case '\'' ->
                      {
                        symbol = Symbol.charLiteral;
                        text   = scanCharLiteral();
                      }
                    case '\"' ->
                      {
                        symbol = Symbol.stringLiteral;
                        text   = scanStringLiteral();
                      }
                    case '-' ->
                      {
                        // should be a negative integer literal
                        source.advance();
                        if (Character.isDigit((char) source.currentChar()))
                          {
                            symbol = Symbol.intLiteral;
                            text   = "-" + scanIntegerLiteral();
                          }
                        else
                            throw error("Expecting an integer literal");
                      }
                    default ->
                      {
                        throw error(position, "Invalid Token");
                      }
                  }
          }
        catch (ScannerException e)
          {
            // stop on first error -- no error recovery
            errorHandler.reportError(e);
            System.exit(1);
          }

        return new Token(symbol, position, text);
      }

    /**
     * Clear the scan buffer (makes it empty).
     */
    private void clearScanBuffer()
      {
        scanBuffer.delete(0, scanBuffer.length());
      }

    /**
     * Scans characters in the source file for a valid identifier using the
     * lexical rule: identifier = ( letter | "_" ) { letter | digit } .
     */
    private String scanIdentifier() throws IOException
      {
        // assumes that source.currentChar() is the first character of the identifier
        assert CharUtil.isLetter((char) source.currentChar()) || source.currentChar() == '_';

        clearScanBuffer();

        do
          {
            scanBuffer.append((char) source.currentChar());
            source.advance();
          }
        while (CharUtil.isLetterOrDigit((char) source.currentChar()));

        return scanBuffer.toString();
      }

    /**
     * Scans characters in the source file for a valid integer literal. Assumes
     * that source.currentChar() is the first character of the Integer literal.
     *
     * @return the string of digits for the integer literal.
     */
    private String scanIntegerLiteral() throws ScannerException, IOException
      {
        // assumes that source.currentChar() is the first digit of the integer literal
        assert CharUtil.isDigit((char) source.currentChar());

        clearScanBuffer();

        do
          {
            scanBuffer.append((char) source.currentChar());
            source.advance();
          }
        while (CharUtil.isDigit((char) source.currentChar()));

        return scanBuffer.toString();
      }

    private void skipComment() throws ScannerException, IOException
      {
        // assumes that source.currentChar() is the leading ';'
        assert (char) source.currentChar() == ';';

        skipToEndOfLine();
        source.advance();
      }

    /**
     * Scans characters in the source file for a String literal.
     * Escaped characters are converted; e.g., '\t' is converted to
     * the tab character.  Assumes that source.currentChar() is the
     * opening quote (") of the String literal.
     *
     * @return the string of characters for the string literal, including
     *         opening and closing quotes
     */
    private String scanStringLiteral() throws ScannerException, IOException
      {
        // assumes that source.currentChar() is the opening double quote for the string literal
        assert (char) source.currentChar() == '\"';

        clearScanBuffer();

        do
          {
            checkGraphicChar(source.currentChar());
            char c = (char) source.currentChar();

            if (c == '\\')
                scanBuffer.append(scanEscapedChar());   // call to scanEscapedChar()
                                                        // advances source
            else {
                scanBuffer.append(c);
                source.advance();
            }
          }
        while ((char) source.currentChar() != '\"');

        scanBuffer.append('\"');    // append closing quote
        source.advance();

        return scanBuffer.toString();
      }

    /**
     * Scans characters in the source file for a valid char literal.
     * Escaped characters are converted; e.g., '\t' is converted to
     * the tab character.  Assumes that source.currentChar() is the
     * opening single quote (') of the Char literal.
     *
     * @return the string of characters for the char literal, including
     *         opening and closing single quotes.
     */
    private String scanCharLiteral() throws ScannerException, IOException
      {
        // assumes that source.currentChar() is the opening
        // single quote for the char literal
        assert (char) source.currentChar() == '\'';

        clearScanBuffer();

        char c = (char) source.currentChar();           // opening quote
        scanBuffer.append(c);                       // append the opening quote

        source.advance();
        checkGraphicChar(source.currentChar());
        c = (char) source.currentChar();                // the character literal

        if (c == '\\')                              // escaped character
            scanBuffer.append(scanEscapedChar());   // call to scanEscapedChar() advances source
        else if (c == '\'')                         // check for empty char literal
          {
            source.advance();
            throw error("Char literal must contain exactly one character.");
          }
        else
          {
            scanBuffer.append(c);                   // append the character literal
            source.advance();
          }

        checkGraphicChar(source.currentChar());
        c = (char) source.currentChar();                // should be the closing quote

        if (c == '\'')
          {
            scanBuffer.append(c);                   // append the closing quote
            source.advance();
          }
        else
            throw error("Char literal not closed properly.");

        return scanBuffer.toString();
      }

    /**
     * Scans characters in the source file for an escaped character; i.e.,
     * a character preceded by a backslash.  This method handles escape
     * characters \t, \n, \r, \", \', and \\.  If the character following
     * a backslash is anything other than one of these characters, then an
     * exception is thrown.  Assumes that source.currentChar() is the escape
     * character (\).
     *
     * @return the value for an escaped character.
     */
    private char scanEscapedChar() throws ScannerException, IOException
      {
        // assumes that source.currentChar() is a backslash character
        assert (char) source.currentChar() == '\\';

        // Need to save current position for error reporting.
        var backslashPosition = source.charPosition();

        source.advance();
        checkGraphicChar(source.currentChar());
        char c = (char) source.currentChar();

        source.advance();   // leave source at second character following the backslash

        return switch (c)
          {
            case 't'  -> '\t';   // tab
            case 'n'  -> '\n';   // newline
            case 'r'  -> '\r';   // carriage return
            case '\"' -> '\"';   // double quote
            case '\'' -> '\'';   // single quote
            case '\\' -> '\\';   // backslash
            default   -> throw new ScannerException(backslashPosition, "Illegal escape character.");
          };
      }

    /**
     * Returns the symbol associated with an identifier
     * (Symbol.ADD, Symbol.AND, Symbol.identifier, etc.)
     */
    private Symbol getIdentifierSymbol(String idString)
      {
        return opcodeMap.getOrDefault(idString.toUpperCase(), Symbol.identifier);
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
     * Advances over source characters to the end of the current line.
     */
    private void skipToEndOfLine() throws ScannerException, IOException
      {
        while ((char) source.currentChar() != '\n')
          {
            source.advance();
            checkEOF();
          }
      }

    /**
     * Checks that the integer represents a graphic character in the Unicode
     * Basic Multilingual Plane (BMP).
     *
     * @throws ScannerException if the integer does not represent a BMP graphic character.
     */
    private void checkGraphicChar(int n) throws ScannerException
      {
        if (n == Source.EOF)
            throw error("End of file reached before closing quote.");
        else if (n > 0xffff)
            throw error("Character not in Unicode Basic Multilingual Pane (BMP)");
        else
          {
            char c = (char) n;
            if (c == '\r' || c == '\n')   // special check for end of line
                throw error("Char and String literals can not extend past end of line.");
            else if (Character.isISOControl(c))
                // Sorry. No ISO control characters.
                throw new ScannerException(source.charPosition(),
                    "Control characters not allowed in Char or String literal.");
          }
      }

    /**
     * Returns a ScannerException with the specified error message.
     */
    private ScannerException error(String errorMsg)
      {
        return error(position(), errorMsg);
      }

    /**
     * Returns a ScannerException with the specified position and error message.
     */
    private ScannerException error(Position errorPos, String errorMsg)
      {
        return new ScannerException(errorPos, errorMsg);
      }

    /**
     * Used to check for EOF in the middle of scanning tokens that
     * require closing characters such as strings and comments.
     *
     * @throws ScannerException if source is at end of file.
     */
    private void checkEOF() throws ScannerException
      {
        if (source.currentChar() == Source.EOF)
            throw error("Unexpected end of file");
      }
  }

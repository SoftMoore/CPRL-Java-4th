package test.cprl;

import edu.citadel.common.ErrorHandler;
import edu.citadel.cprl.Scanner;
import edu.citadel.cprl.Symbol;
import edu.citadel.cprl.Token;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class TestScanner
  {
    private static PrintStream out
            = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    public static void main(String[] args)
      {
        try
          {
            // check arguments
            if (args.length != 1)
                printUsageAndExit();

            out.println("initializing...");

            var fileName = args[0];
            var sourceFile = new File(fileName);
            var errorHandler = new ErrorHandler();
            var scanner = new Scanner(sourceFile, 4, errorHandler);   // 4 lookahead tokens
            Token token;

            out.println("starting main loop...");
            out.println();

            do
              {
                token = scanner.token();
                printToken(token);
                scanner.advance();
              }
            while (token.symbol() != Symbol.EOF);

            out.println();
            out.println("...done");
          }
        catch (Exception e)
          {
            e.printStackTrace();
          }
      }

    private static void printToken(Token token)
      {
        out.printf("line: %2d   char: %2d   token: ",
            token.position().lineNumber(),
            token.position().charNumber());

        var symbol = token.symbol();
        if (symbol.isReservedWord())
            out.print("Reserved Word -> ");
        else if (symbol == Symbol.identifier    || symbol == Symbol.intLiteral
              || symbol == Symbol.stringLiteral || symbol == Symbol.charLiteral)
            out.print(token.symbol().toString() + " -> ");

        out.println(token.text());
      }

    private static void printUsageAndExit()
      {
        out.println("Usage: java test.cprl.TestScanner <test file>");
        out.println();
        System.exit(0);
      }
  }

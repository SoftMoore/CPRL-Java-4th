package test.common;

import edu.citadel.common.Source;

import java.io.FileReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class TestSource
  {
    public static void main(String[] args)
      {
        if (args.length != 1)
            printUsageAndExit();

        try
          {
            var fileName = args[0];
            var reader   = new FileReader(fileName, StandardCharsets.UTF_8);
            var source   = new Source(reader);
            var out      = new PrintStream(System.out, true, StandardCharsets.UTF_8);

            while (source.currentChar() != Source.EOF)
              {
                int c = source.currentChar();

                if (c == '\n')
                    out.println("\\n\t" + source.charPosition());
                else if (c != '\r')
                    out.println((char) c + "\t" + source.charPosition());

                source.advance();
              }
          }
        catch (Exception e)
          {
            e.printStackTrace();
          }
      }

    private static void printUsageAndExit()
      {
        System.err.println("Usage: testSource filename");
        System.err.println();
        System.exit(0);
      }
  }

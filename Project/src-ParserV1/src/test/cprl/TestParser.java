package test.cprl;

import edu.citadel.common.ErrorHandler;
import edu.citadel.common.FatalException;

import edu.citadel.cprl.Scanner;
import edu.citadel.cprl.IdTable;
import edu.citadel.cprl.Parser;

import java.io.File;

/**
 * Test the Parser for the CPRL programming language.
 */
public class TestParser
  {
    private static final String SUFFIX = ".cprl";

    public static void main(String[] args) throws Exception
      {
        if (args.length == 0)
            printUsageAndExit();

        for (String fileName : args)
          {
            var errorHandler = new ErrorHandler();

            try
              {
                var sourceFile = new File(fileName);

                if (!sourceFile.isFile())
                  {
                    // see if we can find the file by appending the suffix
                    int index = fileName.lastIndexOf('.');

                    if (index < 0 || !fileName.substring(index).equals(SUFFIX))
                      {
                        fileName   += SUFFIX;
                        sourceFile  = new File(fileName);

                        if (!sourceFile.isFile())
                            throw new FatalException("File \"" + fileName + "\" not found");
                      }
                    else
                      {
                        // don't try to append the suffix
                        throw new FatalException("File \"" + fileName + "\" not found");
                      }
                  }

                printProgressMessage("Parsing " + fileName + "...");

                var scanner = new Scanner(sourceFile, 4, errorHandler);   // 4 lookahead tokens
                var idTable = new IdTable();
                var parser  = new Parser(scanner, idTable, errorHandler);

                parser.parseProgram();

                if (errorHandler.errorsExist())
                    errorHandler.printMessage("Errors detected in " + fileName + " -- parsing terminated.");
                else
                    printProgressMessage("Parsing complete.");

              }
            catch (FatalException e)
              {
                // report error and continue testing parser
                errorHandler.reportFatalError(e);
              }

            System.out.println();
          }
      }

    private static void printProgressMessage(String message)
      {
        System.out.println(message);
      }

    private static void printUsageAndExit()
      {
        System.out.println("Usage: testParser file1 file2 ...");
        System.out.println();
        System.exit(0);
      }
  }

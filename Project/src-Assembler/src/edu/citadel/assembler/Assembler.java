package edu.citadel.assembler;

import edu.citadel.assembler.ast.AST;
import edu.citadel.assembler.ast.Instruction;
import edu.citadel.assembler.ast.Program;
import edu.citadel.common.ErrorHandler;
import edu.citadel.common.FatalException;

import java.io.*;
import java.util.List;

/**
 * Assembler for the CPRL Virtual Machine.
 */
public class Assembler
  {
    private static final boolean DEBUG = false;

    private static final String SUFFIX  = ".asm";
    private static final int    FAILURE = -1;

    private static boolean optimize = true;

    /**
     * Translates the assembly source files named in args to CVM machine
     * code.  Object files have the same name but with a ".obj" suffix.
     */
    public static void main(String[] args) throws Exception
      {
        if (args.length == 0)
            printUsageAndExit();

        int startIndex = 0;

        if (args[0].startsWith("-opt:"))
          {
            processOption(args[0]);
            startIndex = 1;
          }

        for (int i = startIndex; i < args.length; ++i)
          {
            try
              {
                var fileName   = args[i];
                var sourceFile = new File(fileName);

                if (!sourceFile.isFile())
                  {
                    // see if we can find the file by appending the suffix
                    int index = fileName.lastIndexOf('.');

                    if (index < 0 || !fileName.substring(index).equals(SUFFIX))
                      {
                        fileName  += SUFFIX;
                        sourceFile = new File(fileName);

                        if (!sourceFile.isFile())
                            throw new FatalException("File " + fileName + " not found");
                      }
                    else
                      {
                        // don't try to append the suffix
                        throw new FatalException("File " + fileName + " not found");
                      }
                  }

                var assembler = new Assembler();
                assembler.assemble(sourceFile);
              }
            catch (FatalException e)
              {
                // report error and continue compiling
                var errorHandler = new ErrorHandler();
                errorHandler.reportFatalError(e);
              }

            System.out.println();
          }
      }

    /**
     * Assembles the source file.  If there are no errors in the source
     * file, the object code is placed in a file with the same base file
     * name as the source file but with a ".obj" suffix.
     *
     * @throws IOException Thrown if there are problems reading the source
     *                     file or writing to the target file.
     */
    public void assemble(File sourceFile) throws IOException
      {
        var errorHandler = new ErrorHandler();
        var scanner = new Scanner(sourceFile, errorHandler);
        var parser  = new Parser(scanner, errorHandler);
        AST.reset(errorHandler);

        printProgressMessage("Starting assembly for " + sourceFile.getName());
        printProgressMessage("...parsing");

        // parse source file
        Program program = parser.parseProgram();

        if (DEBUG)
          {
            System.out.println("...program after parsing");
            printInstructions(program.instructions());
          }

        // optimize
        if (!errorHandler.errorsExist() && optimize)
          {
            printProgressMessage("...performing optimizations");
            program.optimize();
          }

        if (DEBUG)
          {
            if (optimize)
              {
                System.out.println("...program after performing optimizations");
                printInstructions(program.instructions());
              }
          }

        // set addresses
        if (!errorHandler.errorsExist())
          {
            printProgressMessage("...setting memory addresses");
            program.setAddresses();
          }

        // check constraints
        if (!errorHandler.errorsExist())
          {
            printProgressMessage("...checking constraints");
            program.checkConstraints();
          }

        // generate code
        if (!errorHandler.errorsExist())
          {
            printProgressMessage("...generating code");
            AST.setOutputStream(getTargetOutputStream(sourceFile));

            // no error recovery from errors detected during code generation
            program.emit();
          }

        if (errorHandler.errorsExist())
            errorHandler.printMessage("Errors detected in " + sourceFile.getName()
                                    + " -- assembly terminated.");
        else
            printProgressMessage("Assembly complete.");
      }

    /**
     * This method is useful for debugging.
     *
     * @param instructions The list of instructions to print.
     */
    private static void printInstructions(List<Instruction> instructions)
      {
        System.out.println("There are " + instructions.size() + " instructions.");
        for (var instruction : instructions)
            System.out.println(instruction);
        System.out.println();
      }

    private static void printProgressMessage(String message)
      {
        System.out.println(message);
      }

    private static void printUsageAndExit()
      {
        System.err.println("Usage: assemble [option] file1 file2 ...");
        System.err.println("where the option is omitted or is one of the following:");
        System.err.println("-opt:off  Turns off all assembler optimizations");
        System.err.println("-opt:on   Turns on all assembler optimizations (default)");
        System.err.println();
        System.exit(0);
      }

    private static void processOption(String option)
      {
        switch (option)
          {
            case "-opt:off" -> optimize = false;
            case "-opt:on"  -> optimize = true;
            default         -> printUsageAndExit();
          }
      }

    private static OutputStream getTargetOutputStream(File sourceFile)
      {
        // get source file name minus the suffix
        var baseName = sourceFile.getName();
        int suffixIndex = baseName.lastIndexOf(SUFFIX);
        if (suffixIndex > 0)
            baseName = sourceFile.getName().substring(0, suffixIndex);

        var targetFileName = baseName + ".obj";

        try
          {
            File targetFile = new File(sourceFile.getParent(), targetFileName);
            return new FileOutputStream(targetFile);
          }
        catch (IOException e)
          {
            e.printStackTrace();
            System.exit(FAILURE);
            return null;   // satisfies Java compiler but shouldn't be necessary
          }
      }
  }

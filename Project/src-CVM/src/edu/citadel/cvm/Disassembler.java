package edu.citadel.cvm;

import java.io.*;
import java.nio.charset.StandardCharsets;

import edu.citadel.common.util.ByteUtil;
import edu.citadel.common.util.CharUtil;

/**
 * Translates CVM machine code into CVM assembly language.
 */
public class Disassembler
  {
    private static final String SUFFIX = ".obj";

    // exit return value for failure
    private static final int FAILURE = -1;

    /**
     * Translates CVM machine code into CVM assembly language
     * for each object code file named in args.
     */
    public static void main(String[] args) throws IOException
      {
        if (args.length == 0)
            printUsageAndExit();

        for (var fileName : args)
          {
            // get object code file name minus the suffix
            int suffixIndex = fileName.lastIndexOf(SUFFIX);
            if (suffixIndex < 0)
              {
                System.err.println("*** Invalid file name suffix: " + fileName + " ***");
                System.exit(FAILURE);
              }

            var codeFile = new FileInputStream(fileName);

            var baseName = fileName.substring(0, suffixIndex);
            var outputFileName = baseName + ".dis.txt";
            var writer = new FileWriter(outputFileName, StandardCharsets.UTF_8);
            var out    = new PrintWriter(writer, true);

            System.out.println("Disassembling " + fileName + " to " + outputFileName);

            int  opcodeAddr = 0;   // first opcode is at address 0
            char c;

            int inByte = codeFile.read();
            while (inByte != -1)
              {
                var opcode = Opcode.toOpcode(inByte);
                var opcodeAddrStr = String.format("%4s", opcodeAddr);

                if (opcode == null)
                    System.err.println("*** Unknown opcode " + inByte
                                     + " in file " + fileName + " ***");
                else if (opcode.isZeroOperandOpcode())
                  {
                    out.println(opcodeAddrStr + ":  " + opcode);
                    opcodeAddr = opcodeAddr + 1;   // 1 byte for opcode
                  }
                else if (opcode.isByteOperandOpcode())
                  {
                    out.print(opcodeAddrStr + ":  " + opcode);
                    int n = readByte(codeFile);
                    if (n < 0)
                        n = n + 256;
                    out.println(" " + n);
                    opcodeAddr = opcodeAddr + 2;   // byte for opcode plus byte for operand
                  }
                else if (opcode.isIntOperandOpcode())
                  {
                    out.print(opcodeAddrStr + ":  " + opcode);
                    out.println(" " + readInt(codeFile));
                    opcodeAddr = opcodeAddr + 1 + Constants.BYTES_PER_INTEGER;
                  }
                else if (opcode == Opcode.LDCCH)
                  {
                    // special case LDCCH
                    out.print(opcodeAddrStr + ":  " + opcode);
                    out.print(" \'");

                    c = readChar(codeFile);
                    if (CharUtil.isEscapeChar(c))
                        out.print(CharUtil.unescapeChar(c));
                    else
                        out.print(c);

                    out.println("\'");
                    opcodeAddr = opcodeAddr + 1 + Constants.BYTES_PER_CHAR;
                  }
                else if (opcode == Opcode.LDCSTR)
                  {
                    // special case LDCSTR
                    out.print(opcodeAddrStr + ":  " + opcode);
                    // now print the string
                    out.print("  \"");
                    var strLength = readInt(codeFile);
                    for (int i = 0; i < strLength; ++i)
                      {
                        c = readChar(codeFile);
                        if (CharUtil.isEscapeChar(c))
                            out.print(CharUtil.unescapeChar(c));
                        else
                            out.print(c);
                      }
                    out.println("\"");
                    opcodeAddr = opcodeAddr + 1 + Constants.BYTES_PER_INTEGER
                               + strLength*Constants.BYTES_PER_CHAR;
                  }
                else
                    System.err.println("*** Unknown opcode " + inByte
                                     + " in file " + fileName + " ***");

                inByte = codeFile.read();
            }

            out.close();
          }
      }

    /**
     * Reads an integer argument from the stream.
     */
    private static int readInt(InputStream in) throws IOException
      {
        byte b0 = (byte) in.read();
        byte b1 = (byte) in.read();
        byte b2 = (byte) in.read();
        byte b3 = (byte) in.read();
        return ByteUtil.bytesToInt(b0, b1, b2, b3);
      }

    /**
     * Reads a char argument from the stream.
     */
    private static char readChar(InputStream in) throws IOException
      {
        byte b0 = (byte) in.read();
        byte b1 = (byte) in.read();
        return ByteUtil.bytesToChar(b0, b1);
      }

    /**
     * Reads a byte argument from the stream.
     */
    private static byte readByte(InputStream in) throws IOException
      {
        return (byte) in.read();
      }

    private static void printUsageAndExit()
      {
        System.err.println("Usage: disassemble file1 file2 ...");
        System.err.println();
        System.exit(0);
      }
  }

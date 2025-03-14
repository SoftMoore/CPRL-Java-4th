package edu.citadel.assembler.ast;

import edu.citadel.common.ConstraintException;
import edu.citadel.common.ErrorHandler;
import edu.citadel.common.Position;
import edu.citadel.common.util.ByteUtil;
import edu.citadel.cvm.Opcode;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Base class for all abstract syntax trees.
 */
public abstract class AST
  {
    private static OutputStream out;
    private static ErrorHandler errorHandler;

    /**
     * Initializes static members that are shared with all instructions.
     * The members must be re-initialized each time that the assembler is
     * run on a different file; e.g., via a command like assemble *.asm.
     */
    public static void reset(ErrorHandler errorHandler)
      {
        AST.errorHandler = errorHandler;
        Instruction.resetMaps();
      }

    /**
     * Set the output stream to be used for code generation.
     */
    public static void setOutputStream(OutputStream out)
      {
        AST.out = out;
      }

    /**
     * Get the error handler used during code generation.
     */
    protected static ErrorHandler errorHandler()
      {
        return AST.errorHandler;
      }

    /**
     * Creates a constraint exception with the specified position and message.
     */
    protected ConstraintException error(Position errorPos, String errorMsg)
      {
        return new ConstraintException(errorPos, errorMsg);
      }

    /**
     * Emit the instruction opcode.
     */
    protected void emit(Opcode opcode) throws IOException
      {
        out.write(opcode.toByte());
      }

    /**
     * Emit a byte argument for the instruction.
     */
    protected void emit(byte arg) throws IOException
      {
        out.write(arg);
      }

    /**
     * Emit an integer argument for the instruction.
     */
    protected void emit(int arg) throws IOException
      {
        out.write(ByteUtil.intToBytes(arg));
      }

    /**
     * Emit a character argument for the instruction.
     */
    protected void emit(char arg) throws IOException
      {
        out.write(ByteUtil.charToBytes(arg));
      }

    /**
     * Check semantic/contextual constraints.
     */
    public abstract void checkConstraints();

    /**
     * Emit the object code for the AST.
     */
    public abstract void emit() throws IOException;
  }

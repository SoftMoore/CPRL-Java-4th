package edu.citadel.assembler.ast;

import edu.citadel.cvm.Constants;
import edu.citadel.cvm.Opcode;
import edu.citadel.assembler.Symbol;
import edu.citadel.assembler.Token;
import edu.citadel.common.ConstraintException;

import java.util.List;
import java.io.IOException;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction PUTSTR.
 */
public class InstructionPUTSTR extends InstructionOneArg
  {
    public InstructionPUTSTR(List<Token> labels, Token opcode, Token arg)
      {
        super(labels, opcode, arg);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.PUTSTR);
      }

    @Override
    public void checkArgType() throws ConstraintException
      {
        checkArgType(Symbol.intLiteral);
      }

    @Override
    protected int argSize()
      {
        return Constants.BYTES_PER_INTEGER;
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.PUTSTR);
        emit(argToInt());
      }
  }

package edu.citadel.assembler.ast;

import edu.citadel.cvm.Opcode;
import edu.citadel.assembler.Symbol;
import edu.citadel.assembler.Token;

import java.util.List;
import java.io.IOException;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction ADD.
 */
public class InstructionADD extends InstructionNoArgs
  {
    public InstructionADD(List<Token> labels, Token opcode)
      {
        super(labels, opcode);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.ADD);
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.ADD);
      }
  }

package edu.citadel.assembler.ast;

import edu.citadel.cvm.Opcode;
import edu.citadel.assembler.Symbol;
import edu.citadel.assembler.Token;
import edu.citadel.common.ConstraintException;

import java.util.List;
import java.io.IOException;

/**
 * This class implements the abstract syntax tree for the assembly
 * language instruction LDCB.
 */
public class InstructionLDCB extends InstructionOneArg
  {
    public InstructionLDCB(List<Token> labels, Token opcode, Token arg)
      {
        super(labels, opcode, arg);
      }

    @Override
    public void assertOpcode()
      {
        assertOpcode(Symbol.LDCB);
      }

    @Override
    public void checkArgType() throws ConstraintException
      {
        checkArgType(Symbol.intLiteral);
        var intValue = argToInt();
        if (intValue < 0 || intValue > 255)    // check unsigned byte value
          {
            var errorMsg = "Can't convert argument " + arg() + " to Byte.";
            throw error(arg().position(), errorMsg);
          }
      }
    
    private byte argToByte()
      {
        return (byte) argToInt();
      }

    @Override
    protected int argSize()
      {
        return 1;
      }

    @Override
    public void emit() throws IOException
      {
        emit(Opcode.LDCB);
        emit(argToByte());
      }
  }

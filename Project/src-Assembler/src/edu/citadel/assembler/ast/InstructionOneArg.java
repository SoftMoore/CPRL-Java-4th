package edu.citadel.assembler.ast;

import edu.citadel.assembler.Symbol;
import edu.citadel.assembler.Token;
import edu.citadel.common.ConstraintException;
import edu.citadel.common.util.IntUtil;

import java.util.List;

/**
 * This class serves as a base class for the abstract syntax
 * tree for an assembly language instruction with one argument.
 */
public abstract class InstructionOneArg extends Instruction
  {
    protected Token arg;

    /**
     * Construct a one-argument instruction with a list of labels, an opcode,
     * and an argument.
     */
    public InstructionOneArg(List<Token> labels, Token opcode, Token arg)
      {
        super(labels, opcode);
        this.arg = arg;
        assert arg != null : "Argument should never be null for this instruction.";
      }

    public Token arg()
      {
        return arg;
      }

    /**
     * check semantic/contextual constraints
     */
    @Override
    public void checkConstraints()
      {
        try
          {
            assertOpcode();
            checkLabels();
            checkArgType();
          }
        catch (ConstraintException e)
          {
            errorHandler().reportError(e);
          }
      }

    /**
     * This method is called by instructions that have an argument that
     * references a label.  It verifies that the referenced label exists.
     */
    protected void checkLabelArgDefined() throws ConstraintException
      {
        if (arg.symbol() != Symbol.identifier)
          {
            var errorMsg = "Expecting a label identifier but found " + arg.symbol();
            throw error(arg.position(), errorMsg);
          }

        String label = arg.text() + ":";
        if (!labelMap.containsKey(label))
          {
            var errorMsg = "Label \"" + arg.text() + "\" has not been defined.";
            throw error(arg.position(), errorMsg);
          }
      }

    /**
     * This method is called by instructions to verify the type of its argument.
     */
    protected void checkArgType(Symbol argType) throws ConstraintException
      {
        if (arg.symbol() != argType)
          {
            var errorMsg = "Invalid type for argument -- should be " + argType + ".";
            throw error(arg.position(), errorMsg);
          }

        if (arg.symbol() == Symbol.intLiteral)
          {
            try
              {
                IntUtil.toInt(arg.text());
              }
            catch (NumberFormatException e)
              {
                var errorMsg = "The number \"" + arg.text()
                             + "\" cannot be converted to an integer in CPRL.";
                arg.setText("0");   // to prevent additional error messages
                throw error(arg.position(), errorMsg);
              }
          }
      }

    /**
     * Returns the argument as converted to an integer.  Valid
     * only for instructions with arguments of type intLiteral.
     */
    public int argToInt()
      {
        assert arg().symbol() == Symbol.intLiteral :
            "Can't convert argument " + arg() + " to an integer.";
        return IntUtil.toInt(arg().text());
      }

    @Override
    public String toString()
      {
        return super.toString() + " " + arg.text();
      }

    /**
     * Checks that the argument of the instruction has
     * the correct type.  Implemented in each instruction
     * by calling the method checkArgType(Symbol).
     */
    protected abstract void checkArgType() throws ConstraintException;
  }

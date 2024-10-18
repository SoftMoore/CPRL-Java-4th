package edu.citadel.assembler.ast;

import edu.citadel.cvm.Constants;
import edu.citadel.assembler.Symbol;
import edu.citadel.assembler.Token;
import edu.citadel.common.ConstraintException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This abstract class implements common methods for the abstract
 * syntax tree of a single assembly language instruction.
 */
public abstract class Instruction extends AST
  {
    // Maps label text (type String) to an address (type Integer).
    // Note that the label text always includes the colon (:) at the end.
    protected static Map<String, Integer> labelMap = new HashMap<>();

    // Maps identifier text (type String) to a stack address (type Integer).
    protected static Map<String, Integer> idMap = new HashMap<>();

    private List<Token> labels;
    private Token opcode;

    private int address;

    /**
     * Construct an instruction with a list of labels and an opcode.
     */
    public Instruction(List<Token> labels, Token opcode)
      {
        this.labels = labels;
        this.opcode = opcode;
      }

    /**
     * Initialize static maps.  These maps are shared with all instructions,
     * but they must be re-initialized if the assembler is run on more than
     * one file; e.g., via a command like assemble *.asm.
     */
    public static void resetMaps()
      {
        labelMap = new HashMap<>();
        idMap    = new HashMap<>();
      }

    public List<Token> labels()
      {
        return labels;
      }

    public Token opcode()
      {
        return opcode;
      }

    /**
     * Sets the memory address and defines label values for this instruction.
     */
    public void setAddress(int address) throws ConstraintException
      {
        this.address = address;

        // define addresses for labels
        for (Token label : labels)
          {
            if (labelMap.containsKey(label.text()))
              {
                var errorMsg = "Label \"" + label + "\" has already been defined.";
                throw error(label.position(), errorMsg);
              }
            else
                labelMap.put(label.text(), Integer.valueOf(address));
          }
      }

    /**
     * Returns the address of this instruction.
     */
    public int address()
      {
        return address;
      }

    /**
     * Returns the number of bytes in memory occupied by the argument.
     */
    protected abstract int argSize();

    /**
     * Returns the number of bytes in memory occupied by the instruction,
     * computed as 1 (for the opcode) plus the size of the argument.
     */
    public int size()
      {
        return Constants.BYTES_PER_OPCODE + argSize();
      }

    /**
     * Checks that each label has a value defined in the label map.  This method
     * should not be called for an instruction before method setAddress().
     *
     * @throws ConstraintException  if the instruction has a label that
     *                              is not defined in the label map.
     */
    protected void checkLabels() throws ConstraintException
      {
        for (Token label : labels)
          {
            if (!labelMap.containsKey(label.text()))
              {
                var errorMsg = "label \"" + label.text() + "\" has not been defined.";
                throw error(label.position(), errorMsg);
              }
          }
      }

    /**
     * Calculates the displacement between an instruction's address and
     * a label (computed as label's address - instruction's address).
     * This method is used by branching and call instructions.
     */
    protected int getDisplacement(Token labelArg)
      {
        var labelId = labelArg.text() + ":";

        assert labelMap.containsKey(labelId);

        var labelAddress = (Integer) labelMap.get(labelId);
        return labelAddress - (address + size());
      }

    /**
     * Asserts that the opcode token of the instruction has the
     * correct Symbol.  Implemented in each instruction by calling
     * the method assertOpcode(Symbol).
     */
    protected abstract void assertOpcode();

    protected void assertOpcode(Symbol opcode)
      {
        assert this.opcode != null;
        assert this.opcode.symbol() == opcode;
      }

    @Override
    public String toString()
      {
        var buffer = new StringBuffer(100);

        // for now simply print the instruction
        if (labels != null)
          {
            for (Token label : labels)
                buffer.append(label.text() + "\n");
          }

        buffer.append("   " + opcode.text());
        return buffer.toString();
      }
  }

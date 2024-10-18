package edu.citadel.cprl.ast;

import edu.citadel.common.Position;
import edu.citadel.common.CodeGenException;

import edu.citadel.cprl.Type;

/**
 * This class is used to pad composite actual parameters and initializations
 * with additional bytes in order to force alignment of string constants with
 * their corresponding types.
 */
public final class Padding extends Expression implements Initializer
  {
    private int numBytes;

    /**
     * Construct a padding with the number of bytes required.
     */
    public Padding(int numBytes)
      {
        super(Type.none, new Position());
        this.numBytes = numBytes;
      }

    @Override
    public int size()
      {
        return numBytes;
      }

    @Override
    public void checkConstraints()
      {
        assert numBytes > 0 : "Number of bytes for padding should be positive.";
      }

    @Override
    public void emit() throws CodeGenException
      {
        emit("ALLOC " + numBytes);
      }

    @Override
    public String toString()
      {
        return "Padding[" + numBytes + "]";
      }
  }

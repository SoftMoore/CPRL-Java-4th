package edu.citadel.cprl.ast;

import edu.citadel.common.Position;
import edu.citadel.common.CodeGenException;

import java.util.List;
import java.util.LinkedList;

/**
 * An initializer for composite types.  Composite initializers are enclosed
 * in braces, which can be nested for initialization of nested composite types.
 * Implements the composite pattern.
 */
public final class CompositeInitializer extends AST implements Initializer
  {
    private Position position;

    // Use linked list since we could be inserting padding in the middle.
    private List<Initializer> initializers = new LinkedList<>();

    public CompositeInitializer(Position position)
      {
        this.position = position;
      }

    /**
     * Add the specified initializer to this composite.
     */
    public void add(Initializer initializer)
      {
        this.initializers.add(initializer);
      }

    /**
     * Return the list of initializers contained in this composite.
     */
    public List<Initializer> initializers()
      {
        return initializers;
      }

    /**
     * Return the number of bytes for this composite initializer.
     */
    @Override
    public int size()
      {
        return initializers.stream().mapToInt(x -> x.size()).sum();
      }

    @Override
    public Position position()
      {
        return position;
      }

    @Override
    public void checkConstraints()
      {
        for (Initializer initializer : initializers)
            initializer.checkConstraints();
      }

    @Override
    public void emit() throws CodeGenException
      {
        for (Initializer initializer : initializers)
            initializer.emit();
      }

    @Override
    public String toString()
      {
        return "CompositeInitializer[" + initializers + "]";
      }
  }

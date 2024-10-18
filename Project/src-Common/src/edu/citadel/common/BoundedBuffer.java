package edu.citadel.common;

/**
 * Bounded circular buffer.
 */
public class BoundedBuffer<E>
  {
    private int capacity;
    private Object[] buffer;
    private int index = 0;   // circular index

    /**
     * Construct buffer with the specified capacity.
     */
    public BoundedBuffer(int capacity)
      {
        this.capacity = capacity;
        buffer = new Object[capacity];
      }

    /**
     * Returns the capacity of this bounded buffer.
     */
    public int capacity()
      {
        return capacity;
      }

    /**
     * Return the element at index i.  Does not remove the element from the buffer.
     */
    @SuppressWarnings("unchecked")
    public E get(int i)
      {
        return (E) buffer[(index + i) % capacity];
      }

    /**
     * Add an element to the buffer.  Overwrites if the buffer is full.
     */
    public void add(E e)
      {
        buffer[index] = e;
        index = (index + 1) % capacity;
      }
  }

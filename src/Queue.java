import java.util.ArrayList;

/**
 * My own implementation of a queue. This queue is based off of an array list database.
 */
public class Queue {
    /**
     * The actual private queue value. This is an array list. I chose this as
     * it was simpler to implement than an array.
     */
    private ArrayList<Object> queue;

    /**
     * Constructor for the queue. Sets the private attribute to a new
     * array list.
     */
    public Queue() {
        this.queue = new ArrayList<>();
    }

    /**
     * Enqueue takes a given object and adds it to the back of the queue.
     * @param object to be added to the queue
     */
    public void enqueue(Object object) {
        this.queue.add(object);
    }

    /**
     * Dequeue removed the first value of the queue and returns it.
     * @return the first object in the queue
     */
    public Object dequeue() {
        Object val = this.queue.get(0);
        this.queue.remove(val);
        return val;
    }

    /**
     * Simple getter which returns the current size of the queue.
     * @return
     */
    public int getSize() {
        return this.queue.size();
    }

    /**
     * Is empty returns a boolean value. True if the size is equal to
     * zero, which means empty, false otherwise.
     * @return boolean value for if the queue is empty
     */
    public boolean isEmpty() {
        return getSize() == 0;
    }

    /**
     * Clear sets the queue to a new empty array list.
     */
    public void clear() {
        this.queue = new ArrayList<>();
    }
}

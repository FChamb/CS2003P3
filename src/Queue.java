import java.util.ArrayList;

public class Queue {
    private ArrayList<Object> queue = new ArrayList<>();

    public Queue() {
        this.queue = new ArrayList<>();
    }

    public void enqueue(Object object) {
        this.queue.add(object);
    }

    public Object dequeue() {
        Object val = this.queue.get(0);
        this.queue.remove(val);
        return val;
    }

    public int getSize() {
        return this.queue.size();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public void clear() {
        this.queue = new ArrayList<>();
    }
}

package queue;

public class ArrayQueue extends AbstractQueue {
    private Object[] elements = new Object[2];
    private int start = 0;
    private int end = 0;

    private void ensureCapacity() {
        if (size == elements.length) {
            Object[] buffer = new Object[elements.length * 2];
            System.arraycopy(elements, 0, buffer, elements.length - start, start);
            System.arraycopy(elements, start, buffer, 0, elements.length - start);
            start = 0;
            end = elements.length;
            elements = buffer;
        }
    }
    
    @Override
    public void enqueueImpl(final Object e) {
        ensureCapacity();

        elements[end % elements.length] = e; // fixed, was: `elements[end] = e;`
        end = (end + 1) % elements.length;
    }

    @Override
    public Object elementImpl() {
        return elements[start];
    }

    @Override
    public Object dequeueImpl() {
        Object r = elements[start];
        elements[start] = null;
        start = (start + 1) % elements.length;
        return r;
    }

    @Override
    public void clearImpl() {
        start = end = 0;
        elements = new Object[elements.length];
    }

    @Override
    public void pushImpl(Object e) {
        ensureCapacity();
        start = (elements.length + start - 1) % elements.length;
        elements[start] = e;
    }

    @Override
    public Object peekImpl() {
        return elements[(elements.length + end - 1) % elements.length];
    }

    @Override
    public Object removeImpl() {
        end = (elements.length + end - 1) % elements.length;
        Object r = elements[end];
        elements[end] = null;
        return r;
    }

    @Override
    public Object getImpl(int i) {
        return elements[(i + start) % elements.length];
    }

    @Override
    public void setImpl(int i, Object e) {
        elements[(i + start) % elements.length] = e;
    }



    @Override
    public Queue getNthImpl(int n) {
        var q = new ArrayQueue();
        int i = n - 1;
        while (i < size) {
            q.enqueue(elements[(i + start) % elements.length]);
            i += n;
        } 
        return q;
    }

    @Override
    public void dropNthImpl(int n) {
        Object[] buffer = new Object[elements.length];
        int ptr = 0;
        for (int i = 0; i < size; i++) {
            Object x = elements[(i + start) % elements.length];
            if ((i + 1) % n != 0) {
                buffer[ptr] = x;
                ptr++;
            }
        }
        start = 0;
        end = ptr;
        size = ptr;
        elements = buffer;
    }
}

package queue;

import java.util.Objects;

abstract class AbstractQueue implements Queue {
    protected int size = 0;


    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    abstract protected void enqueueImpl(final Object e);
    public void enqueue(final Object e) {
        Objects.requireNonNull(e);
        enqueueImpl(e);
        size++;
    }

    abstract protected Object elementImpl();
    public Object element() {
        assert size > 0;
        return elementImpl();
    };

    abstract protected Object dequeueImpl();
    public Object dequeue() {
        assert size > 0;
        Object r = dequeueImpl();
        size--;
        return r;
    }

    abstract protected void clearImpl();
    public void clear() {
        clearImpl();
        size = 0;
    }

    abstract protected void pushImpl(final Object e);
    public void push(final Object e) {
        Objects.requireNonNull(e);
        pushImpl(e);
        size++;
    }
    
    abstract protected Object peekImpl();
    public Object peek() {
        assert size > 0;
        return peekImpl();
    }

    abstract protected Object removeImpl();
    public Object remove() {
        assert size > 0;
        Object r = removeImpl();
        size--;
        return r;
    }

    abstract protected Object getImpl(int i);
    public Object get(int i) {
        assert 0 <= i && i < size;
        return getImpl(i);
    }

    abstract protected void setImpl(int i, Object e);
    public void set(int i, Object e) {
        assert 0 <= i && i < size;
        Objects.requireNonNull(e);
        setImpl(i, e);
    }



    abstract protected Queue getNthImpl(int n);
    public Queue getNth(int n) {
        assert n > 0;
        return getNthImpl(n);
    }

    abstract protected void dropNthImpl(int n);
    public void dropNth(int n) {
        assert n > 0;
        dropNthImpl(n);
    }

    public Queue removeNth(int n) {
        assert n > 0;
        var q = getNthImpl(n);
        dropNthImpl(n);
        return q;
    }
}

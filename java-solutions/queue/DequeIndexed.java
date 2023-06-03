package queue;
import java.util.Objects;

/**
 *  Model: a[0] .. a[n - 1]
 *         n >= 0
 *  Invariant: for i = 0 to n - 1: a[i] != null

    Let Statement(n): for i = 0 to n - 1: a'[i] == a[i]

    Pred: e != null
    Post: n' == n + 1 & a'[n] == e && Statement(n)
 *  enqueue(e)

    Pred: n > 0
    Post: n' == n && R == a[0] && Statement(n)
 *  element

    Pred: n > 0 
    Post: n' = n - 1 && R = a[0] && for i = 0 to n - 1: a'[i] == a[i + 1]
 *  dequeue

    Pred: True
    Post: R == n && n' == n && Statement(n)
 *  size

    Pred: True
    Post: R == (n == 0) && n' == n && Statement(n)
    isEmpty

    Pred: True
    Post: n' = 0
  * clear


    
    Pred: e != null
    Post: n' == n + 1 & a'[0] == e && for i = 1 to n: a'[i] == a[i - 1]
  * push – добавить элемент в начало очереди;


    Pred: n > 0
    Post: n' == n && R == a[n - 1] && Statement(n)
  * peek – вернуть последний элемент в очереди;

    Pred: n > 0
    Post: n' == n - 1 && R == a[n - 1] && Statement(n)
  * remove – вернуть и удалить последний элемент из очереди.


    Pred: 0 <= i < n
    Post: n' == n && R == a[i] && Statement(n)
  * get – получить элемент по индексу, отсчитываемому с головы;

    Pred: 0 <= i < n && e != null
    Post: n' == n && e == a[i] && for j in [0 .. n - 1]\i: a'[j] == a[j]
  * set – заменить элемент по индексу, отсчитываемому с головы.
*/

public class DequeIndexed {
    private Object[] elements;
    private int n;
    private int size;
    private int start;
    private int end;

    private void ensureCapacity() {
        if (n == size) {
            Object[] buffer = new Object[size * 2];
            for (int i = 0; i < size; i++) {
                buffer[i] = elements[(start + i) % size];
            }
            start = 0;
            end = size;
            size *= 2;
            elements = buffer;
        }
    }

    public DequeIndexed() {
        elements = new Object[2];
        n = start = end = 0;
        size = 2;
    }

    public static DequeIndexed create() {
        return new DequeIndexed();
    }
   
 //    Pred: e != null
 //    Post: n' == n + 1 & a'[n] == e && Statement(n)
    public void enqueue(final Object e) {
        Objects.requireNonNull(e);
        ensureCapacity();
        elements[end] = e;
        end = (end + 1) % size;
        n++;
    }

 //    Pred: n > 0
 //    Post: n' == n && R == a[0] && Statement(n)
    public Object element() {
        assert n > 0;
        return elements[start];
    }

 //    Pred: n > 0
 //    Post: n' = n - 1 && R = a[0] && for i = 0 to n - 1: a'[i] == a[i + 1]
    public Object dequeue() {
        assert n > 0;
        Object r = elements[start];
        elements[start] = null;
        start = (start + 1) % size;
        n--;
        return r;
    }

 //    Pred: True
 //    Post: R == n && n' == n && Statement(n)
    public int size() {
        return n;
    }

 //    Pred: True
 //    Post: R == (n == 0) && n' == n && Statement(n)
    public boolean isEmpty() {
        return n == 0;
    }

 //    Pred: True
 //    Post: n' = 0
    public void clear() {
        n = start = end = 0;
        size = 2;
        elements = new Object[size];
    }




    // Pred: e != null
    // Post: n' == n + 1 & a'[0] == e && for i = 1 to n: a'[i] == a[i - 1]
    public void push(Object e) {
        Objects.requireNonNull(e);
        ensureCapacity();
        start = (size + start - 1) % size;
        elements[start] = e;
        n++;
    }


    // Pred: n > 0
    // Post: n' == n && R == a[n - 1] && Statement(n)
    public Object peek() {
  	    assert n > 0;
        return elements[end - 1];
    }

    // Pred: n > 0
    // Post: n' == n - 1 && R == a[n - 1] && Statement(n)
    public Object remove() {
        assert n > 0;
        Object r = elements[end - 1];
        elements[end - 1] = null;
        end = (size + end - 1) % size;
        n--;
        return r;
    }

    // Pred: 0 <= i < n
    // Post: n' == n && R == a[i] && Statement(n)
    public Object get(int i) {
    	return elements[(i + start) % size];
    }

    // Pred: 0 <= i < n && e != null
    // Post: n' == n && e == a[i] && for j in [0 .. n - 1]\i: a'[j] == a[j]
    public void set(int i, Object e) {
        Objects.requireNonNull(e);
    	elements[(i + start) % size] = e;
    }

}

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



public class ArrayQueueModule {
    private static Object[] elements = new Object[2];
    private static int size = 0;
    private static int start = 0;
    private static int end = 0;


    private static void ensureCapacity() {
        if (size == elements.length) {
            Object[] buffer = new Object[elements.length * 2];
            System.arraycopy(elements, 0, buffer, elements.length - start, start);
            System.arraycopy(elements, start, buffer, 0, elements.length - start);
            start = 0;
            end = elements.length;
            elements = buffer;
        }
    }
	
 //    Pred: e != null
 //    Post: n' == n + 1 & a'[n] == e && Statement(n)
    public static void enqueue(final Object e) {
        Objects.requireNonNull(e);
        ensureCapacity();

        elements[end] = e;
        end = (end + 1) % elements.length;
        size++;
    }

 //    Pred: n > 0
 //    Post: n' == n && R == a[0] && Statement(n)
    public static Object element() {
        assert size > 0;
        return elements[start];
    }

 //    Pred: n > 0 
 //    Post: n' = n - 1 && R = a[0] && for i = 0 to n - 1: a'[i] == a[i + 1]
    public static Object dequeue() {
        assert size > 0;
        Object r = elements[start];
        elements[start] = null;
        start = (start + 1) % elements.length;
        size--;
        return r;
    }

 //    Pred: True
 //    Post: R == n && n' == n && Statement(n)
    public static int size() {
        return size;
    }

 //    Pred: True
 //    Post: R == (n == 0) && n' == n && Statement(n)
    public static boolean isEmpty() {
        return size == 0;
    }

 //    Pred: True
 //    Post: n' = 0
    public static void clear() {
        size = start = end = 0;
        elements = new Object[elements.length];
    }





    // Pred: e != null
    // Post: n' == n + 1 & a'[0] == e && for i = 1 to n: a'[i] == a[i - 1]
    public static void push(Object e) {
        Objects.requireNonNull(e);
        ensureCapacity();
        start = (elements.length + start - 1) % elements.length;
        elements[start] = e;
        size++;
    }


    // Pred: n > 0
    // Post: n' == n && R == a[n - 1] && Statement(n)
    public static Object peek() {
        assert size > 0;
        return elements[(elements.length + end - 1) % elements.length];
    }

    // Pred: n > 0
    // Post: n' == n - 1 && R == a[n - 1] && Statement(n)
    public static Object remove() {
        assert size > 0;
        end = (elements.length + end - 1) % elements.length;
        Object r = elements[end];
        elements[end] = null;
        size--;
        return r;
    }

    // Pred: 0 <= i < n
    // Post: n' == n && R == a[i] && Statement(n)
    public static Object get(int i) {
        assert 0 <= i && i < size;
        return elements[(i + start) % elements.length];
    }

    // Pred: 0 <= i < n && e != null
    // Post: n' == n && e == a[i] && for j in [0 .. n - 1]\i: a'[j] == a[j]
    public static void set(int i, Object e) {
        assert 0 <= i && i < size;
        Objects.requireNonNull(e);
        elements[(i + start) % elements.length] = e;
    }
}

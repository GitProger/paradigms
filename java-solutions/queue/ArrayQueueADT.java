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



public class ArrayQueueADT {
    private Object[] elements;
    private int size;
    private int start;
    private int end;

    public ArrayQueueADT() {
        elements = new Object[2];
        size = start = end = 0;
    }

    public static ArrayQueueADT create() {
        final ArrayQueueADT q = new ArrayQueueADT();
        q.elements = new Object[2];
        q.size = q.start = q.end = 0;
        return q;
    }

    private static void ensureCapacity(final ArrayQueueADT que) {
        if (que.size == que.elements.length) {
            Object[] buffer = new Object[que.elements.length * 2];
            System.arraycopy(que.elements, 0, buffer, que.elements.length - que.start, que.start);
            System.arraycopy(que.elements, que.start, buffer, 0, que.elements.length - que.start);
            que.start = 0;
            que.end = que.elements.length;
            que.elements = buffer;
        }        
    }
	
 //    Pred: e != null && que != null
 //    Post: n' == n + 1 & a'[n] == e && Statement(n)
    public static void enqueue(final ArrayQueueADT que, final Object e) {
        Objects.requireNonNull(e);
        ensureCapacity(que);

        que.elements[que.end] = e;
        que.end = (que.end + 1) % que.elements.length;
        que.size++;
    }

 //    Pred: n > 0 && que != null
 //    Post: n' == n && R == a[0] && Statement(n)
    public static Object element(final ArrayQueueADT que) {
        assert que.size > 0;
        return que.elements[que.start];
    }

 //    Pred: n > 0 && que != null
 //    Post: n' = n - 1 && R = a[0] && for i = 0 to n - 1: a'[i] == a[i + 1]
    public static Object dequeue(final ArrayQueueADT que) {
    	assert que.size > 0;
        Object r = que.elements[que.start];
        que.elements[que.start] = null;
        que.start = (que.start + 1) % que.elements.length;
        que.size--;
        return r;
    }

 //    Pred: que != null
 //    Post: R == n && n' == n && Statement(n)
    public static int size(final ArrayQueueADT que) {
        return que.size;
    }

 //    Pred: que != null
 //    Post: R == (n == 0) && n' == n && Statement(n)
    public static boolean isEmpty(ArrayQueueADT que) {
        return que.size == 0;
    }

 //    Pred: que != null
 //    Post: n' = 0
    public static void clear(final ArrayQueueADT que) {
        que.size = que.start = que.end = 0;
        que.elements = new Object[que.elements.length];
    }




    // Pred: e != null
    // Post: n' == n + 1 & a'[0] == e && for i = 1 to n: a'[i] == a[i - 1]
    public static void push(final ArrayQueueADT que, Object e) {
        Objects.requireNonNull(e);
        ensureCapacity(que);
        que.start = (que.elements.length + que.start - 1) % que.elements.length;
        que.elements[que.start] = e;
        que.size++;
    }


    // Pred: n > 0
    // Post: n' == n && R == a[n - 1] && Statement(n)
    public static Object peek(final ArrayQueueADT que) {
        assert que.size > 0;
        return que.elements[(que.elements.length + que.end - 1) % que.elements.length];
    }

    // Pred: n > 0
    // Post: n' == n - 1 && R == a[n - 1] && Statement(n)
    public static Object remove(final ArrayQueueADT que) {
        assert que.size > 0;
        que.end = (que.elements.length + que.end - 1) % que.elements.length;
        Object r = que.elements[que.end];
        que.elements[que.end] = null;
        que.size--;
        return r;
    }

    // Pred: 0 <= i < n
    // Post: n' == n && R == a[i] && Statement(n)
    public static Object get(final ArrayQueueADT que, int i) {
        assert 0 <= i && i < que.size;
        return que.elements[(i + que.start) % que.elements.length];
    }

    // Pred: 0 <= i < n && e != null
    // Post: n' == n && e == a[i] && for j in [0 .. n - 1]\i: a'[j] == a[j]
    public static void set(final ArrayQueueADT que, int i, Object e) {
        assert 0 <= i && i < que.size;
        Objects.requireNonNull(e);
        que.elements[(i + que.start) % que.elements.length] = e;
    }
}

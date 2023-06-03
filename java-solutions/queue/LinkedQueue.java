package queue;

public class LinkedQueue extends AbstractQueue {
    private static class Node {
        public Node prev = null;
        public Node next = null;
        public Object contents;
        public Node(Object c) {
            contents = c;
        }
        public Node(Object c, Node p, Node n) {
            contents = c;
            prev = p; 
            next = n;
        }
    }

    private Node head = null;
    private Node tail = null;
    
    @Override
    protected void enqueueImpl(final Object e) {
        Node newTail = new Node(e, tail, null);
        if (tail != null) {
            tail.next = newTail;
        }
        tail = newTail;
        if (head == null) {
            head = tail;
        }
    }

    @Override
    public Object elementImpl() {
        return head.contents;
    }

    @Override
    public Object dequeueImpl() {
        Object r = head.contents;
        if (head.next != null) {
            head.next.prev = null;
        }
        head = head.next;
        return r;
    }

    @Override
    public void clearImpl() {
        tail = null;
        head = null;
    }

    @Override
    public void pushImpl(Object e) {
        Node newHead = new Node(e, null, head);
        if (head != null) {
            head.prev = newHead;
        }
        head = newHead;
        if (tail == null) {
            tail = head;
        }
    }

    @Override
    public Object peekImpl() {
        return tail.contents;
    }

    @Override
    public Object removeImpl() {
        Object r = tail.contents;
        if (tail.prev != null) {
            tail.prev.next = null;
        }
        tail = tail.prev;
        return r;
    }

    private Node getDirectLink(int i) {
        // can be optimized with binary lifting
        Node x = (i < size / 2 ? head : tail);
        if (i < size / 2) {
            while (i-- > 0) {
                x = x.next;
            }
        } else {
            i = size - i - 1; // fixed, was: `i = size - i;`
            while (i-- > 0) {
                x = x.prev;
            }
        }
        return x;        
    }

    @Override
    public Object getImpl(int i) {
        return getDirectLink(i).contents;
    }

    @Override
    public void setImpl(int i, Object e) {
        getDirectLink(i).contents = e;
    }


    @Override
    public Queue getNthImpl(int n) {
        Node cur = head;
        var q = new LinkedQueue();
        for (int i = 0; i < size; i++) {
            if ((i + 1) % n == 0) {
                q.enqueue(cur.contents);
            }
            cur = cur.next;
        }
        return q;
    }

    @Override
    public void dropNthImpl(int n) {
        Node cur = head;
        int removed = 0;
        for (int i = 0; i < size; i++) {
            if (cur == null) {
                break;
            }
            Node l = cur.prev;
            Node r = cur.next;
            if ((i + 1) % n == 0) {
                if (cur == head) { // l == null -> err
                    head = head.next;
                    if (head != null) head.prev = null;
                } else if (cur == tail) { // r == null -> err
                    tail = tail.prev;
                    if (tail != null) tail.next = null;
                } else {
                    if (l != null) l.next = r;
                    if (r != null) r.prev = l;
                }
                removed++;
            }
            cur = r;
        }
        size -= removed;
    }
}

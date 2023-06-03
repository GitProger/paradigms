package queue;

import java.util.ArrayDeque;

public class QueueTestCustom {
    private static void testQueue(final Queue q, final ArrayDeque qx, int test) {
        boolean ok = true;
        if (test == 1) {
            while (!q.isEmpty()) {
                ok &= q.isEmpty() == qx.isEmpty();
                ok &= q.size() == qx.size();
                ok &= q.element().equals(qx.getFirst());
                ok &= q.dequeue().equals(qx.pollFirst());
                if (!ok) break;
            }
        }
        if (test == 2) {
            while (!q.isEmpty()) {
                ok &= q.isEmpty() == qx.isEmpty();
                ok &= q.size() == qx.size();
                ok &= q.peek().equals(qx.getLast());
                ok &= q.remove().equals(qx.pollLast());
                if (!ok) break;
            }
        }
        if (test == 3) {
            for (int i = 0; i < q.size(); i++) {
                ok &= q.get(i).equals(qx.getFirst());
                qx.pollFirst();
                q.set(i, "125" + i);
                ok &= q.get(i).equals("125" + i);
            }
        }
        if (!ok) {
            System.out.println("Test #" + test + " failed");
        } else {
            System.out.println("Test #" + test + " ok");
        }
    }

    public static String dumped(Queue q) {
        StringBuilder sb = new StringBuilder().append("[");
        for (int i = 0; i < q.size(); i++) {
            sb.append(q.get(i));
            if (i != q.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.append("]").toString();
    }

    public static void main(String[] args) {
        for (int test = 1; test <= 6; test++) {
            final Queue q = (test <= 3 ? new ArrayQueue() : new LinkedQueue());
            final ArrayDeque<Object> qx = new ArrayDeque<>();
            for (int i = 0; i < 70; i++) {
                q.enqueue("q_" + i);
                qx.addLast("q_" + i);
            }
            testQueue(q, qx, test % 3);
        }
    }
}

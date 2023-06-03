package queue;

import java.util.ArrayDeque;

public class ArrayQueueADTTestCustom {
    private static void testQueue(final ArrayQueueADT q, final ArrayDeque qx, int test) {
        boolean ok = true;
        if (test == 1) {
            while (!ArrayQueueADT.isEmpty(q)) {
                ok &= ArrayQueueADT.isEmpty(q) == qx.isEmpty();
                ok &= ArrayQueueADT.size(q) == qx.size();
                ok &= ArrayQueueADT.element(q).equals(qx.getFirst());
                ok &= ArrayQueueADT.dequeue(q).equals(qx.pollFirst());
                if (!ok) break;
            }
        }
        if (test == 2) {
            while (!ArrayQueueADT.isEmpty(q)) {
                ok &= ArrayQueueADT.isEmpty(q) == qx.isEmpty();
                ok &= ArrayQueueADT.size(q) == qx.size();
                ok &= ArrayQueueADT.peek(q).equals(qx.getLast());
                ok &= ArrayQueueADT.remove(q).equals(qx.pollLast());
                if (!ok) break;
            }
        }
        if (test == 3) {
            for (int i = 0; i < ArrayQueueADT.size(q); i++) {
                ok &= ArrayQueueADT.get(q, i).equals(qx.getFirst());
                qx.pollFirst();
                ArrayQueueADT.set(q, i, "125" + i);
                ok &= ArrayQueueADT.get(q, i).equals("125" + i);
            }
        }
        if (!ok) {
            System.out.println("Test #" + test + " failed");
        } else {
            System.out.println("Test #" + test + " ok");
        }
    }

    public static void main(String[] args) {
        for (int test = 1; test <= 3; test++) {
            final ArrayQueueADT q = ArrayQueueADT.create();
            final ArrayDeque<Object> qx = new ArrayDeque<>();
            for (int i = 0; i < 70; i++) {
                ArrayQueueADT.enqueue(q, "q_" + i);
                qx.addLast("q_" + i);
            }
            testQueue(q, qx, test);
        }
    }
}

package queue;

public class ArrayQueueModuleTestCustom {
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			ArrayQueueModule.enqueue("q_" + i);
		}
		while (!ArrayQueueModule.isEmpty()) {
			System.out.println(ArrayQueueModule.size() + " -> " + ArrayQueueModule.remove());
		}
	}
}

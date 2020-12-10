package mac.hack.utils;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map.Entry;

public class MacQueue {

	/* cum queue */
	private static HashMap<String, Deque<MutablePair<Runnable, Integer>>> queues = new HashMap<>();

	public static void add(Runnable runnable) {
		add("", runnable);
	}

	public static void add(String id, Runnable runnable) {
		add(id, runnable, 0);
	}

	public static void add(String id, Runnable runnable, int inTicks) {
		if (!queues.containsKey(id)) {
			Deque<MutablePair<Runnable, Integer>> newQueue = new ArrayDeque<>();
			newQueue.add(MutablePair.of(runnable, inTicks));

			queues.put(id, newQueue);
		}

		queues.get(id).add(MutablePair.of(runnable, inTicks));
	}

	public static void cancelQueue(String id) {
		queues.remove(id);
	}

	public static boolean isEmpty(String id) {
		return !queues.containsKey(id);
	}

	public static void nextQueue() {
		for (Entry<String, Deque<MutablePair<Runnable, Integer>>> e : new HashMap<>(queues).entrySet()) {
			Deque<MutablePair<Runnable, Integer>> deque = e.getValue();

			MutablePair<Runnable, Integer> first = deque.peek();

			if (first.right > 0) {
				first.right--;
			} else {
				first.left.run();
				deque.removeFirst();
			}

			if (deque.isEmpty()) {
				queues.remove(e.getKey());
			}
		}
	}
}
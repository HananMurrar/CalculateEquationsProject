package application;

public class CursorArrayStack<T extends Comparable<T>> {
	CursorArray<T> x;
	int list;

	public CursorArrayStack(int size) {
		x = new CursorArray<>(size);
		list = x.createList();
	}

	public void push(T data) {
		if (!x.insertAtFirst(data, list))
			System.out.println("Satck Overflow");
	}

	public T pop() {
		return (T) x.deleteAtFirst(list);
	}

	public T peek() {
		return (T) x.getFirst(list);
	}

	public boolean isEmpty() {
		return x.isEmpty(list);
	}

	public void clear() {
		x.clear(list);
	}
}

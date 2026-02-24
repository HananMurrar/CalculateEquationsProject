package application;

public class CursorArray<T extends Comparable<T>> {
	Node<T>[] cursorArray;

	public CursorArray(int size) {
		cursorArray = new Node[size];
		initialization();
	}

	private void initialization() {
		for (int i = 0; i < cursorArray.length - 1; i++)
			cursorArray[i] = new Node<>(null, i + 1);
		cursorArray[cursorArray.length - 1] = new Node(null, 0);
	}

	private int malloc() {
		int p = cursorArray[0].next;
		cursorArray[0].next = cursorArray[p].next;
		return p;
	}

	private boolean isNull(int list) {
		return cursorArray[list] == null;
	}

	private boolean isLast(int p) {
		return cursorArray[p].next == 0;
	}

	private boolean hasFree() {
		return cursorArray[0].next != 0;
	}

	public boolean isEmpty(int list) {
		return cursorArray[list].next == 0;
	}

	public void clear(int list) {
		while (!isEmpty(list))
			deleteAtFirst(list);
	}

	public int createList() {
		int list = malloc();
		if (list == 0)
			System.out.println("Out Of Space");
		else
			cursorArray[list] = new Node(null, 0);
		return list;
	}

	public boolean insertAtFirst(T data, int list) {
		if (isNull(list))
			return false;
		int p = malloc();
		if (p != 0) {
			cursorArray[p] = new Node(data, cursorArray[list].next);
			cursorArray[list].next = p;
		} else {
			return false;
		}
		return true;
	}

	public Object deleteAtFirst(int list) {
		if (!isNull(list) && !isEmpty(list)) {
			int p = cursorArray[list].next;
			cursorArray[list].next = cursorArray[cursorArray[list].next].next;
			Node temp = cursorArray[p];
			return temp.data;
		}
		return null;
	}

	public Object getFirst(int list) {
		if (!isNull(list) && !isEmpty(list))
			return cursorArray[cursorArray[list].next].data;
		return null;
	}

	public String toString(int list) {
		String s = "Head -> ";
		while (!isNull(list) && !isEmpty(list)) {
			list = cursorArray[list].next;
			s += cursorArray[list].toString() + " -> ";
		}
		s += "NULL";
		return s;
	}
}

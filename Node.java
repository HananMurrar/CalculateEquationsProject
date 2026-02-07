package application;

public class Node<T extends Comparable<T>> {
	T data;
	int next;

	public Node(T data, int next) {
		this.data = data;
		this.next = next;
	}

	public String toString() {
		return data + " , " + next;
	}
}

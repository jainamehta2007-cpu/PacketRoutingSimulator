// Custom FIFO queue using linked list (with optional capacity)
public class MyQueue<T> {
    private static class Node<T> { T val; Node<T> next; Node(T v) { val = v; } }
    private Node<T> front, rear;
    private int size, capacity;

    MyQueue(int capacity) { this.capacity = capacity; }

    boolean enqueue(T v) {                 // O(1), false if buffer full
        if (size >= capacity) return false;
        Node<T> n = new Node<>(v);
        if (rear == null) front = rear = n; else { rear.next = n; rear = n; }
        size++;
        return true;
    }
    T dequeue() {                          // O(1)
        if (front == null) return null;
        T v = front.val;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return v;
    }
    boolean isEmpty() { return size == 0; }
    int size() { return size; }
    T peek() { return front == null ? null : front.val; }
}

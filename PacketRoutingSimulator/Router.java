public class Router {
    static class Link { int to, weight; Link next; Link(int t, int w, Link n) { to = t; weight = w; next = n; } }

    String name;
    boolean exists = true;
    Link head;                              // adjacency list (linked list of links)
    MyQueue<Packet> buffer;

    Router(String name, int bufferSize) { this.name = name; buffer = new MyQueue<>(bufferSize); }
}

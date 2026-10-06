import java.util.Arrays;

// Custom min-heap (priority queue) of (node, distance) pairs, used by Dijkstra
public class MinHeap {
    private int[] node = new int[16], dist = new int[16];
    private int size = 0;

    boolean isEmpty() { return size == 0; }

    void insert(int n, int d) {            // O(log n)
        if (size == node.length) { node = Arrays.copyOf(node, size * 2); dist = Arrays.copyOf(dist, size * 2); }
        node[size] = n; dist[size] = d;
        int i = size++;
        while (i > 0 && dist[(i - 1) / 2] > dist[i]) { swap(i, (i - 1) / 2); i = (i - 1) / 2; }
    }
    int[] extractMin() {                   // O(log n) -> {node, dist}
        int[] top = { node[0], dist[0] };
        size--;
        node[0] = node[size]; dist[0] = dist[size];
        int i = 0;
        while (true) {
            int l = 2 * i + 1, r = l + 1, s = i;
            if (l < size && dist[l] < dist[s]) s = l;
            if (r < size && dist[r] < dist[s]) s = r;
            if (s == i) break;
            swap(i, s); i = s;
        }
        return top;
    }
    private void swap(int a, int b) {
        int t = node[a]; node[a] = node[b]; node[b] = t;
        t = dist[a]; dist[a] = dist[b]; dist[b] = t;
    }
}

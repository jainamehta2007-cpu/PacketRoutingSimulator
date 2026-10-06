public class Graph {
    static final int MAX = 50;
    Router[] routers = new Router[MAX];
    int count = 0;
    int bufferSize = 3;
    int lastCost = 0;                       // cost of last path found

    // ---------- router / link management ----------
    int indexOf(String name) {
        for (int i = 0; i < count; i++)
            if (routers[i].exists && routers[i].name.equalsIgnoreCase(name)) return i;
        return -1;
    }
    boolean addRouter(String name) {
        if (count >= MAX || indexOf(name) != -1) return false;
        routers[count++] = new Router(name, bufferSize);
        return true;
    }
    boolean removeRouter(String name) {
        int r = indexOf(name);
        if (r == -1) return false;
        for (int i = 0; i < count; i++) if (routers[i].exists) removeEdge(i, r);
        routers[r].exists = false;
        routers[r].head = null;
        return true;
    }
    boolean hasLink(int a, int b) {
        for (Router.Link l = routers[a].head; l != null; l = l.next) if (l.to == b) return true;
        return false;
    }
    boolean addLink(String a, String b, int w) {
        int x = indexOf(a), y = indexOf(b);
        if (x == -1 || y == -1 || x == y || w <= 0 || hasLink(x, y)) return false;
        routers[x].head = new Router.Link(y, w, routers[x].head);
        routers[y].head = new Router.Link(x, w, routers[y].head);
        return true;
    }
    boolean removeLink(String a, String b) {
        int x = indexOf(a), y = indexOf(b);
        if (x == -1 || y == -1 || !hasLink(x, y)) return false;
        removeEdge(x, y); removeEdge(y, x);
        return true;
    }
    private void removeEdge(int from, int to) {
        Router.Link cur = routers[from].head, prev = null;
        while (cur != null) {
            if (cur.to == to) {
                if (prev == null) routers[from].head = cur.next; else prev.next = cur.next;
                return;
            }
            prev = cur; cur = cur.next;
        }
    }

    void display() {
        System.out.println("\n--- Network Topology ---");
        for (int i = 0; i < count; i++) {
            if (!routers[i].exists) continue;
            System.out.print(routers[i].name + " -> ");
            for (Router.Link l = routers[i].head; l != null; l = l.next)
                System.out.print(routers[l.to].name + "(" + l.weight + ") ");
            System.out.println();
        }
    }

    // ---------- BFS : fewest hops, O(V+E) ----------
    int[] bfs(int src, int dst) {
        boolean[] visited = new boolean[count];
        int[] parent = new int[count];
        MyQueue<Integer> q = new MyQueue<>(Integer.MAX_VALUE);
        visited[src] = true; parent[src] = -1; q.enqueue(src);
        while (!q.isEmpty()) {
            int u = q.dequeue();
            if (u == dst) break;
            for (Router.Link l = routers[u].head; l != null; l = l.next)
                if (!visited[l.to]) { visited[l.to] = true; parent[l.to] = u; q.enqueue(l.to); }
        }
        if (!visited[dst]) return null;
        int[] p = buildPath(parent, dst);
        lastCost = 0;
        for (int i = 0; i + 1 < p.length; i++) lastCost += weight(p[i], p[i + 1]);
        return p;
    }

    // ---------- Dijkstra : least delay, O((V+E) log V) ----------
    int[] dijkstra(int src, int dst) {
        int[] dist = new int[count], parent = new int[count];
        boolean[] done = new boolean[count];
        for (int i = 0; i < count; i++) dist[i] = Integer.MAX_VALUE;
        dist[src] = 0; parent[src] = -1;
        MinHeap h = new MinHeap();
        h.insert(src, 0);
        while (!h.isEmpty()) {
            int[] top = h.extractMin();
            int u = top[0];
            if (done[u]) continue;
            done[u] = true;
            for (Router.Link l = routers[u].head; l != null; l = l.next) {
                if (dist[u] + l.weight < dist[l.to]) {
                    dist[l.to] = dist[u] + l.weight;
                    parent[l.to] = u;
                    h.insert(l.to, dist[l.to]);
                }
            }
        }
        if (dist[dst] == Integer.MAX_VALUE) return null;
        lastCost = dist[dst];
        return buildPath(parent, dst);
    }

    // ---------- DFS : all simple paths, O(V!) worst case ----------
    int allPaths(int src, int dst) {
        boolean[] visited = new boolean[count];
        int[] path = new int[count];
        return dfs(src, dst, visited, path, 0, 0);
    }
    private int dfs(int u, int dst, boolean[] visited, int[] path, int depth, int cost) {
        visited[u] = true; path[depth] = u;
        int found = 0;
        if (u == dst) {
            printPath(path, depth + 1);
            System.out.println(" (cost: " + cost + ")");
            found = 1;
        } else {
            for (Router.Link l = routers[u].head; l != null; l = l.next)
                if (!visited[l.to]) found += dfs(l.to, dst, visited, path, depth + 1, cost + l.weight);
        }
        visited[u] = false;                 // backtrack
        return found;
    }

    // ---------- helpers ----------
    private int weight(int a, int b) {
        for (Router.Link l = routers[a].head; l != null; l = l.next) if (l.to == b) return l.weight;
        return 0;
    }
    private int[] buildPath(int[] parent, int dst) {
        int len = 0;
        for (int v = dst; v != -1; v = parent[v]) len++;
        int[] p = new int[len];
        int v = dst;
        for (int i = len - 1; i >= 0; i--) { p[i] = v; v = parent[v]; }
        return p;
    }
    void printPath(int[] p, int len) {
        for (int i = 0; i < len; i++) System.out.print(routers[p[i]].name + (i < len - 1 ? " -> " : ""));
    }
}

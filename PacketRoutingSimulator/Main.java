import java.util.Scanner;

public class Main {
    static Graph g = new Graph();
    static Scanner sc = new Scanner(System.in);
    static int packetId = 1, step = 0;
    static final int TTL = 10;

    public static void main(String[] args) {
        loadSample();
        while (true) {
            System.out.println("\n===== Network Packet Routing Simulator =====");
            System.out.println("1. Add router        2. Remove router");
            System.out.println("3. Add link          4. Remove link (link failure)");
            System.out.println("5. Display network   6. Send packet");
            System.out.println("7. All paths (DFS)   8. Run simulation step");
            System.out.println("9. Router queue status  10. Load sample network");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            int c = readInt();
            switch (c) {
                case 1: System.out.print("Name: ");
                    System.out.println(g.addRouter(sc.next()) ? "Router added." : "Failed (duplicate/limit)."); break;
                case 2: System.out.print("Name: ");
                    System.out.println(g.removeRouter(sc.next()) ? "Router removed." : "Not found."); break;
                case 3: System.out.print("Router1 Router2 Weight: ");
                    System.out.println(g.addLink(sc.next(), sc.next(), readInt()) ? "Link added." : "Failed (invalid/duplicate)."); break;
                case 4: System.out.print("Router1 Router2: ");
                    System.out.println(g.removeLink(sc.next(), sc.next()) ? "Link removed." : "Link not found."); break;
                case 5: g.display(); break;
                case 6: sendPacket(); break;
                case 7: showAllPaths(); break;
                case 8: runStep(); break;
                case 9: queueStatus(); break;
                case 10: g = new Graph(); loadSample(); System.out.println("Sample loaded."); break;
                case 0: System.out.println("Bye!"); return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    static int readInt() {
        while (!sc.hasNextInt()) { sc.next(); System.out.print("Enter a number: "); }
        return sc.nextInt();
    }

    static void loadSample() {
        String[] r = { "A", "B", "C", "D", "E" };
        for (String s : r) g.addRouter(s);
        g.addLink("A", "B", 4); g.addLink("A", "C", 2); g.addLink("B", "D", 5);
        g.addLink("C", "D", 8); g.addLink("C", "E", 3); g.addLink("E", "D", 1);
    }

    static void sendPacket() {
        System.out.print("Source Destination: ");
        int s = g.indexOf(sc.next()), d = g.indexOf(sc.next());
        if (s == -1 || d == -1) { System.out.println("Invalid router."); return; }
        if (s == d) { System.out.println("Source and destination are same. Delivered instantly."); return; }
        System.out.print("Algorithm (1 = BFS fewest hops, 2 = Dijkstra least delay): ");
        int a = readInt();
        int[] path = (a == 1) ? g.bfs(s, d) : g.dijkstra(s, d);
        if (path == null) { System.out.println("Destination unreachable!"); return; }
        System.out.print("Route: "); g.printPath(path, path.length);
        System.out.println(" (cost: " + g.lastCost + ", hops: " + (path.length - 1) + ")");
        System.out.print("Data: ");
        String data = sc.next();
        Packet p = new Packet(packetId++, s, d, data, path, TTL);
        if (!g.routers[s].buffer.enqueue(p)) System.out.println("Buffer full at source. Packet " + p.id + " DROPPED.");
        else System.out.println("Packet " + p.id + " queued at " + g.routers[s].name + ". Use option 8 to run simulation.");
    }

    static void showAllPaths() {
        System.out.print("Source Destination: ");
        int s = g.indexOf(sc.next()), d = g.indexOf(sc.next());
        if (s == -1 || d == -1) { System.out.println("Invalid router."); return; }
        int n = g.allPaths(s, d);
        System.out.println(n == 0 ? "No path exists." : "Total paths: " + n);
    }

    // Each router forwards ONE packet per step (FIFO). Packets move one hop per step.
    static void runStep() {
        step++;
        System.out.println("\n--- Step " + step + " ---");
        boolean[] active = new boolean[g.count];
        boolean any = false;
        for (int i = 0; i < g.count; i++) { active[i] = g.routers[i].exists && !g.routers[i].buffer.isEmpty(); any |= active[i]; }
        if (!any) { System.out.println("No packets in network."); return; }

        for (int i = 0; i < g.count; i++) {
            if (!active[i]) continue;
            Packet p = g.routers[i].buffer.dequeue();
            if (i == p.dest) { System.out.println("Packet " + p.id + " DELIVERED at " + g.routers[i].name + " [" + p.data + "]"); continue; }
            if (--p.ttl <= 0) { System.out.println("Packet " + p.id + " DROPPED (TTL expired) at " + g.routers[i].name); continue; }

            int next = p.route[p.pos + 1];
            if (!g.routers[next].exists || !g.hasLink(i, next)) {          // link failure -> reroute
                int[] np = g.dijkstra(i, p.dest);
                if (np == null) { System.out.println("Packet " + p.id + " DROPPED (no route) at " + g.routers[i].name); continue; }
                System.out.print("Packet " + p.id + " REROUTED: "); g.printPath(np, np.length); System.out.println();
                p.route = np; p.pos = 0; next = np[1];
            }
            p.pos++;
            if (g.routers[next].buffer.enqueue(p))
                System.out.println("Packet " + p.id + ": " + g.routers[i].name + " -> " + g.routers[next].name);
            else System.out.println("Packet " + p.id + " DROPPED (buffer full) at " + g.routers[next].name);
        }
    }

    static void queueStatus() {
        System.out.println("\n--- Router Queues ---");
        for (int i = 0; i < g.count; i++)
            if (g.routers[i].exists)
                System.out.println(g.routers[i].name + ": " + g.routers[i].buffer.size() + "/" + g.bufferSize + " packets");
    }
}

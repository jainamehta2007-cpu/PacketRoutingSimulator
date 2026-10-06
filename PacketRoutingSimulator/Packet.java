public class Packet {
    int id, src, dest, ttl, pos;   // pos = index in route[] where packet currently is
    String data;
    int[] route;

    Packet(int id, int src, int dest, String data, int[] route, int ttl) {
        this.id = id; this.src = src; this.dest = dest;
        this.data = data; this.route = route; this.ttl = ttl; this.pos = 0;
    }
}

package de.zeus.digraph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TestEdge {

    @Test
    public void testEqualsEdge() {
        Node a = new Node("a");
        Node b = new Node("b");

        Edge ab = new Edge(a, b, 0);
        Edge anotherAb = new Edge(a, b, 0);

        assertEquals(ab, anotherAb);

        // change latency and check again
        anotherAb.setLatency(10);
        assertEquals(ab, anotherAb);

        Edge ba = new Edge(b, a, 0);
        assertNotEquals(ba, ab);
    }

    public void testToString() {
        Node a = new Node("A");
        Node b = new Node("B");
        Edge ab = new Edge(a, b, 0);

        assertEquals("AB0", ab.toString());
    }

}

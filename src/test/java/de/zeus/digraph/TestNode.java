package de.zeus.digraph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TestNode {

    @Test
    public void testEqualsNode() {
        Node a = new Node("a");
        Node anotherA = new Node("a");

        assertEquals(a, anotherA);

        Node b = new Node("b");
        assertNotEquals(a, b);
    }

}

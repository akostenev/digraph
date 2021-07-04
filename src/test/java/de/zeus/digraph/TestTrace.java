package de.zeus.digraph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestTrace {

    @Test
    public void testTraceCon() {
        Trace trace = new Trace("A-D");

        assertEquals(trace.getNodes().size(), 2);
    }

    @Test
    public void testMalformedTrace() {

        try {
            new Trace("-A--B-"); // this must throw
            assertTrue(false);

        } catch (Exception e) {
            assertTrue(e.getMessage().contains("malformed"));
        }

    }

    public void testSubTrace() {
        Trace trace = new Trace("A-B-C");
        Trace subTrace = trace.getSubTrace();

        assertEquals(subTrace.getNodes().size(), 2);
        assertEquals(subTrace.getNodes().get(0), "B");
        assertEquals(subTrace.getNodes().get(1), "C");
    }

}

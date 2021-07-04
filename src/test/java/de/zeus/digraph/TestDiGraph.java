package de.zeus.digraph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class TestDiGraph {

    private static DiGraph diGraph = new DiGraph("AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7");

    @Test
    public void testNodesPresent() {
        Set<Node> nodes = diGraph.getNodes();

        assertEquals(5, nodes.size()); // A, B, C, D, E

        for (Node node : nodes) {
            switch (node.getName()) {
            case "A":
            case "B":
            case "C":
            case "D":
            case "E":
                assertTrue(true); // all nodes present
                break;

            default:
                throw new RuntimeException("unknown node");
            }
        }
    }

    @Test
    public void testEdgesPresent() {
        Set<Node> nodes = diGraph.getNodes();

        for (Node node : nodes) {
            Map<String, Edge> edges = node.getEdges();

            switch (node.getName()) {
            case "A":
                assertEquals(3, edges.size()); // AB5, AD5, AE7

                for (Edge edge : edges.values()) {
                    assertEquals("A", edge.getFrom().getName());

                    switch (edge.getTo().getName()) {
                    case "B":
                    case "D":
                        assertEquals(5, edge.getLatency());
                        break;

                    case "E":
                        assertEquals(7, edge.getLatency());
                        break;

                    default:
                        throw new RuntimeException("malformed/unknown: " + edge);
                    }
                }
                break;

            case "B":
                assertEquals(1, edges.size()); // BC4

                for (Edge edge : edges.values()) {
                    assertEquals("B", edge.getFrom().getName());

                    switch (edge.getTo().getName()) {
                    case "C":
                        assertEquals(4, edge.getLatency());
                        break;

                    default:
                        throw new RuntimeException("malformed/unknown edge: " + edge);
                    }
                }
                break;

            case "C":
                assertEquals(2, edges.size()); // CD8, CE2

                for (Edge edge : edges.values()) {
                    assertEquals("C", edge.getFrom().getName());

                    switch (edge.getTo().getName()) {
                    case "D":
                        assertEquals(8, edge.getLatency());
                        break;

                    case "E":
                        assertEquals(2, edge.getLatency());
                        break;

                    default:
                        throw new RuntimeException("malformed/unknown edge: " + edge);
                    }
                }
                break;

            case "D":
                assertEquals(2, edges.size()); // DC8, DE6

                for (Edge edge : edges.values()) {
                    assertEquals("D", edge.getFrom().getName());

                    switch (edge.getTo().getName()) {
                    case "C":
                        assertEquals(8, edge.getLatency());
                        break;

                    case "E":
                        assertEquals(6, edge.getLatency());
                        break;

                    default:
                        throw new RuntimeException("malformed/unknown edge: " + edge);
                    }
                }
                break;

            case "E":
                assertEquals(1, edges.size()); // EB3

                for (Edge edge : edges.values()) {
                    assertEquals("E", edge.getFrom().getName());

                    switch (edge.getTo().getName()) {
                    case "B":
                        assertEquals(3, edge.getLatency());
                        break;

                    default:
                        throw new RuntimeException("malformed/unknown edge: " + edge);
                    }
                }
                break;

            default: // unknown node
                throw new RuntimeException("Unknown node");
            }
        }
    }

    @Test
    public void testTraceABC() {
        assertEquals(9, diGraph.getLatency("A-B-C"));
    }

    @Test
    public void testTraceAD() {
        assertEquals(5, diGraph.getLatency("A-D"));
    }

    @Test
    public void testTraceADC() {
        assertEquals(13, diGraph.getLatency("A-D-C"));
    }

    @Test
    public void testTraceAEBCD() {
        assertEquals(22, diGraph.getLatency("A-E-B-C-D"));
    }

    @Test
    public void testTraceAED() {
        assertEquals(-1, diGraph.getLatency("A-E-D"));
    }

    @Test
    public void testPathCCMax3() {
        List<Trace> paths = diGraph.getPaths("C", "C", 3); // "C-D-C", "C-E-B-C"

        assertEquals(2, paths.size());

        for (Trace t : paths) {

            switch (t.toString()) {
            case "C-D-C":
            case "C-E-B-C":
                assertTrue(true);
                break;

            default:
                assertTrue(false);
            }
        }
    }

    @Test
    public void testPathACExact4() {
        List<Trace> paths = diGraph.getPathsExact("A", "C", 4); // "A-B-C-D-C", "A-D-C-D-C", "A-D-E-B-C"

        assertEquals(3, paths.size());

        for (Trace t : paths) {
            switch (t.toString()) {
            case "A-B-C-D-C":
            case "A-D-C-D-C":
            case "A-D-E-B-C":
                assertTrue(true);
                break;

            default:
                assertFalse(false);
            }
        }
    }

    @Test
    public void testShortestPathAC() {
        assertEquals(9, diGraph.getShortestLatency("A", "C"));
    }

    @Test
    public void testShortestPathBB() {
        assertEquals(9, diGraph.getShortestLatency("B", "B"));
    }

    @Test
    public void testShortestPathAE() {
        assertEquals(7, diGraph.getShortestLatency("A", "E"));
    }

    @Test
    public void testShortestPathCA() {
        assertEquals(-1, diGraph.getShortestLatency("C", "A"));
    }

    @Test
    public void testPathMaxLatencyCC30() {
        // C-D-C, C-E-B-C, C-E-B-C-D-C, C-D-C-E-B-C, C-D-E-B-C, C-E-B-C-E-B-C,
        // C-E-B-C-E-B-C-E-B-C
        List<Trace> paths = diGraph.getPathMaxLatency("C", "C", 30);

        assertEquals(7, paths.size());

        for (Trace trace : paths) {
            switch (trace.toString()) {
            case "C-D-C":
            case "C-E-B-C":
            case "C-E-B-C-D-C":
            case "C-D-C-E-B-C":
            case "C-D-E-B-C":
            case "C-E-B-C-E-B-C":
            case "C-E-B-C-E-B-C-E-B-C":
                assertTrue(true);
                break;

            default:
                assertTrue(false);
            }
        }
    }

    @Test
    public void testPathMaxLatencyCA30() {
        // C-D-C, C-E-B-C, C-E-B-C-D-C, C-D-C-E-B-C, C-D-E-B-C, C-E-B-C-E-B-C,
        // C-E-B-C-E-B-C-E-B-C
        List<Trace> paths = diGraph.getPathMaxLatency("C", "A", 30);

        assertEquals(0, paths.size());
    }

}

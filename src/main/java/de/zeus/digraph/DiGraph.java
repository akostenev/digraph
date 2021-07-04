package de.zeus.digraph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Directed graph containing nodes that contains edges to other nodes.
 */
public class DiGraph {

    // all nodes of the directed graph
    private Map<String, Node> nodes = new HashMap<>();

    /**
     * Constructs the DiGraph from an input string.
     *
     * @param graph
     *            a comma separated list of nodes and edges, e.g. "AB5, BC4" means
     *            that A points B and has an average latency of 5ms, and B points to
     *            C with an latency of 4ms.
     */
    public DiGraph(String graph) throws NumberFormatException {

        // split the string by ","
        for (String split : graph.split(",")) {
            String trace = split.trim(); // remove trailing whitespaces

            if (trace.length() < 3) {
                throw new RuntimeException("malformed trace: " + trace);
            }

            Node from = putNode(String.valueOf(trace.charAt(0)));
            Node to = putNode(String.valueOf(trace.charAt(1)));

            // parse latency, can throw NumberFormatException
            int latency = Integer.parseInt(trace.substring(2));

            // connect the nodes
            from.connect(to, latency);
        }
    }

    /**
     * Returns the nodes of the directed graph
     *
     * @return
     */
    public Set<Node> getNodes() {
        return new HashSet<>(nodes.values());
    }

    /**
     * Calculates the latency of a trace, where a trace is a list of nodes,
     * separated by "-".
     *
     * @param sTrace
     *            e.g. "A-B-C"
     * @return the latency of the trace or -1 if no such trace exists
     * @throws RuntimeException
     *             on malformed trace.
     */
    public int getLatency(String sTrace) {
        Trace trace = new Trace(sTrace);

        // get first node
        Node firstNode = nodes.get(trace.getNodes().get(0));
        if (firstNode == null) { // e.g. "X-" where X is an unknown node
            return -1;
        }

        // trace the remaining nodes, e.g "A-B-C" substrings to "B-C", and return the
        // latency
        return firstNode.getLatency(trace.getSubTrace());
    }

    /**
     * Gets the path of the trace, where e.g. "A-B" would return all paths from A to
     * B where the number of hops do not exceed the max depth.
     *
     * @param sTrace
     *            a trace with exactly two nodes e.g. "A-C"
     * @param maxDepth
     *            e.g. 3
     * @return e.g. "A-B-C"
     */
    public List<Trace> getPaths(String from, String to, int maxDepth) {
        Node f = nodes.get(from);
        Node t = nodes.get(to);

        if (f == null || to == null) {
            return null;
        }

        return f.getPaths(t, maxDepth);
    }

    /**
     * Gets the path from "from" to "to", e.g. "A-B" would return all paths from A
     * to B where the number of hops is exactly exactHops
     *
     * @param from
     *            e.g. "A"
     * @param to
     *            e.g. "B"
     * @param exactHops
     *            e.g. 3
     * @return e.g. "A-C-D-B", "A-E-F-B"
     */
    public List<Trace> getPathsExact(String from, String to, int exactHops) {
        List<Trace> paths = getPaths(from, to, exactHops);

        // get all paths with the exact hop count
        return paths.stream().filter(p -> {
            return p.getNodes().size() == exactHops + 1; // + 1 for the first node
        }).collect(Collectors.toList());
    }

    /**
     * Returns all traces from "from" to "to" with a latency smaller than maxLatency
     *
     * @param from
     *            e.g. "C"
     * @param to
     *            e.g. "C"
     * @param maxLatency
     *            e.g. 30
     * @return e.g. C-D-C, C-E-B-C, C-E-B-C-D-C, C-D-C-E-B-C,
     *         C-D-E-B-C,C-E-B-C-E-B-C, C-E-B-C-E-B-C-E-B-C
     */
    public List<Trace> getPathMaxLatency(String from, String to, int maxLatency) {
        Node f = nodes.get(from);
        Node t = nodes.get(to);

        if (f == null || t == null) {
            return null;
        }

        return f.getPathsMaxLatency(t, maxLatency);
    }

    /**
     * Finds the shortest path from "from" to "to".
     *
     * Implemented with Dijkstra's algorithm.
     *
     * @param from
     *            "A"
     * @param to
     *            "B"
     * @return -1 if no such trace exists
     */
    public int getShortestLatency(String from, String to) {
        Node f = nodes.get(from);
        Node t = nodes.get(to);

        if (f == null || t == null) {
            return -1;
        }

        Set<Node> unvisited = new HashSet<>(nodes.values());
        Map<Node, Integer> distance = new HashMap<>();

        nodes.forEach((name, node) -> {
            distance.put(node, Integer.MAX_VALUE);
        });

        // dist[source] ← 0
        distance.put(f, 0);

        while (unvisited.isEmpty() == false) {
            // u ← vertex in Q with min dist[u]
            Entry<Node, Integer> minDistance = distance.entrySet().stream().filter(e -> {
                return unvisited.contains(e.getKey());
            }).min(Comparator.comparing(Entry::getValue)).get();

            // remove u from Q
            unvisited.remove(minDistance.getKey());

            // for each neighbor v of u
            for (Edge edge : minDistance.getKey().getEdges().values()) {
                if (unvisited.contains(edge.getTo()) == false) { // only v that are still in Q
                    continue;
                }

                // alt ← dist[u] + length(u, v)
                Integer alt = minDistance.getValue() + edge.getLatency();

                // take the smaller distance
                if (alt < distance.get(edge.getTo())) {
                    distance.put(edge.getTo(), alt);
                }
            }
        }

        // for equal from and to nodes
        if (f.equals(t)) {

            // find the edge connected from other nodes
            return distance.entrySet().stream().mapToInt(e -> {
                Edge edge = e.getKey().getEdges().get(to);

                // if not present or not connected
                if (edge == null || e.getValue() == Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE; // trace does not exist
                }

                // return the latency of the previous path plus the edge latency
                return e.getValue() + edge.getLatency();
            }).min().getAsInt(); // find the smallest latency, and return it
        }

        int latency = distance.get(t);
        return latency == Integer.MAX_VALUE ? -1 : latency;
    }


    /**
     * Creates a new node in the node map (if not present).
     *
     * @param name
     *            the nodes (unique) name
     * @return The matching node.
     */
    private Node putNode(String name) {
        Node newNode = new Node(name);
        Node presentNode = nodes.putIfAbsent(name, newNode);

        if (presentNode == null) {
            return newNode;
        }

        return presentNode;
    }

}
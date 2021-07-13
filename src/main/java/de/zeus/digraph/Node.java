package de.zeus.digraph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A Node is represented by its name, which is unique.
 */
public class Node {

    private String name;

    // Stores the outgoing edges, indexed by the node name which is unique, based on
    // the constraint: "A given connection will never appear more than once"
    private Map<String, Edge> edges = new HashMap<>();

    public Node(String name) {
        this.name = name;
    }

    /**
     * Creates an Edge to the node "to"
     *
     * @param to
     *            the node to connect to
     * @param latency
     *            edge payload
     */
    public void connect(Node to, int latency) {
        // Create an Edge, and put it in the edges map.
        // Check constraint "A given connection will never appear more than once"
        if (edges.putIfAbsent(to.getName(), new Edge(this, to, latency)) != null) {
            throw new RuntimeException("Duplicate edge");
        }
    }

    /**
     * Calculates the latency of a trace.
     *
     * Recursive approach.
     *
     * @param trace
     *            e.g. "A-B-C"
     * @return the latency of the trace or -1 if no such trace exists
     * @throws RuntimeException
     *             on malformed trace.
     */
    public int getLatency(Trace trace) {
        // get the next edge
        Edge edge = edges.get(trace.getNodes().get(0));
        if (edge == null) { // e.g. "X" where X is not a known node
            return -1;
        }

        // check if last node
        if (trace.getNodes().size() == 1) { // stops recursive calls
            return edge.getLatency();
        }

        // get the latency of the subtrace
        int latency = edge.getTo().getLatency(trace.getSubTrace());
        if (latency == -1) { // trace does not exist
            return latency;
        }

        // return the latency of the sub-traces and the latency of our edge
        return latency + edge.getLatency();
    }

    /**
     * Returns all paths to "to" with the given max depth.
     *
     * @param to
     * @param maxDepth
     * @return
     */
    public List<Trace> getPaths(Node to, int maxDepth) {
        List<Trace> ret = new LinkedList<>();

        if (maxDepth == 0) {
            return ret;
        }

        // for each edge
        edges.values().stream().forEach(edge -> {

            // find path
            if (edge.getTo().equals(to)) {
                ret.add(new Trace(edge.getFrom() + "-" + edge.getTo()));
            }

            // find sub paths
            edge.getTo().getPaths(to, maxDepth - 1).forEach(trace -> {
                trace.getNodes().add(0, getName());
                ret.add(trace);
            });
        });

        return ret;
    }

    /**
     * Returns all paths to "to" with the given max latency.
     *
     * @param to
     * @param maxLatency
     * @return
     */
    public List<Trace> getPathsMaxLatency(Node to, int maxLatency) {
        LinkedList<String> trace = new LinkedList<>();
        trace.add(to.getName());

        return getPathsMaxLatency(to, maxLatency, 0, new Trace(trace));
    }

    private List<Trace> getPathsMaxLatency(Node to, int maxLatency, int currentLatency, Trace trace) {
        List<Trace> ret = new LinkedList<>();

        if (currentLatency >= maxLatency) {
            return ret;
        }

        for (Edge edge : edges.values()) {
            int newLatency = edge.getLatency() + currentLatency;

            if (newLatency >= maxLatency) {
                continue; // latency of this edge exceeds max latency
            }

            Trace traceCopy = new Trace(trace);
            traceCopy.getNodes().add(traceCopy.getNodes().size(), edge.getTo().getName());

            // trace found within max latency
            if (edge.getTo().equals(to)) {
                ret.add(traceCopy);
            }

            ret.addAll(edge.getTo().getPathsMaxLatency(to, maxLatency, newLatency, traceCopy));
        }

        return ret;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the edges
     */
    public Map<String, Edge> getEdges() {
        return edges;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}

package de.zeus.digraph;

/**
 * An Edge links two nodes from "from" to "to" and carries an average latency.
 *
 * An Edge is unique based on the two immutable nodes, the latency can change.
 */
public class Edge {

    private Node from;
    private Node to;
    private int latency;

    public Edge(Node left, Node right, int latency) {
        // null nodes
        if (left == null || right == null) {
            throw new RuntimeException("Node(s) are null");
        }

        // for a given connection the starting and ending service will not be the same
        // service.
        if (left.equals(right)) {
            throw new RuntimeException("Nodes are equal, loop detected");
        }

        // check negative latency
        if (latency < 0) {
            throw new RuntimeException("Latency is negative");
        }

        this.from = left;
        this.to = right;
        this.latency = latency;
    }

    /**
     * @return the "from" node
     */
    public Node getFrom() {
        return from;
    }

    /**
     * @return the "to" node
     */
    public Node getTo() {
        return to;
    }

    /**
     * @return the average latency
     */
    public int getLatency() {
        return latency;
    }

    /**
     * @param latency
     *            the latency to set
     */
    public void setLatency(int latency) {
        this.latency = latency;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + ((from == null) ? 0 : from.hashCode());
        result = 37 * result + ((to == null) ? 0 : to.hashCode());
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
        Edge other = (Edge) obj;
        if (from == null) {
            if (other.from != null)
                return false;
        } else if (!from.equals(other.from))
            return false;
        if (to == null) {
            if (other.to != null)
                return false;
        } else if (!to.equals(other.to))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return from.toString() + to.toString() + latency;
    }

}

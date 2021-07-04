package de.zeus.digraph;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds a trace represented by String e.g. "A-B-C".
 */
public class Trace {

    private static final String DELIMITER = "-";

    private List<String> nodes;

    /**
     * Tests if the trace is well formed.
     *
     * @param trace e.g. "A-B-C"
     * @throws RuntimeException if the trace is malformed
     */
    public Trace(String trace) {
        int firstDelimiter = trace.indexOf(DELIMITER);
        if (firstDelimiter == -1 // no "-" found
                || firstDelimiter == 0 // e.g. "-A"
                || firstDelimiter == trace.length() - 1 // e.g. "A-"
                || trace.contains(DELIMITER + DELIMITER) // "A--B"
        ) {
            throw new RuntimeException("trace is malformed: " + trace);
        }

        nodes = new LinkedList<String>();
        nodes.addAll(Arrays.asList(trace.split(DELIMITER)));
    }

    public Trace(List<String> nodes) {
        this.nodes = nodes;
    }

    public Trace(Trace trace) {
        this.nodes = new LinkedList<>(trace.nodes);
    }

    /**
     * Returns a sub trace where the first node is removed.
     *
     * @param trace
     *            e.g. "A-B-C"
     * @return e.g. "B-C"
     */
    public Trace getSubTrace() {
        return new Trace(nodes.subList(1, nodes.size()));
    }

    /**
     * @return the nodes
     */
    public List<String> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return String.join(DELIMITER, nodes);
    }

}

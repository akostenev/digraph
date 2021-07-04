package de.zeus.digraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void usage() {
        System.out.println("java -jar target/Main.java /path/to/input/file");
        System.out.println("example: java -jar target/digraph-0.0.1-SNAPSHOT-jar-with-dependencies.jar src/main/resources/input");
    }

    public static String getLatency(int latency) {
        if (latency < 0) {
            return "NO SUCH TRACE";
        }

        return String.valueOf(latency);
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("No input file ...");
            usage();
            return;
        }

        DiGraph diGraph = null;
        try {
             diGraph = new DiGraph(Files.readString(Paths.get(args[0])));

        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("1. " + getLatency(diGraph.getLatency("A-B-C")));
        System.out.println("2. " + getLatency(diGraph.getLatency("A-D")));
        System.out.println("3. " + getLatency(diGraph.getLatency("A-D-C")));
        System.out.println("4. " + getLatency(diGraph.getLatency("A-E-B-C-D")));
        System.out.println("5. " + getLatency(diGraph.getLatency("A-E-D")));
        System.out.println("6. " + diGraph.getPaths("C", "C", 3).size());
        System.out.println("7. " + diGraph.getPathsExact("A", "C", 4).size());
        System.out.println("8. " + getLatency(diGraph.getShortestLatency("A", "C")));
        System.out.println("9. " + getLatency(diGraph.getShortestLatency("B", "B")));
        System.out.println("10. " + diGraph.getPathMaxLatency("C", "C", 30).size());
    }

}

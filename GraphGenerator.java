import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class GraphGenerator {
    public static class Edge {
        String from;
        String to;
        int weight;

        Edge(String from, String to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    public static class Graph {
        String id;
        List<String> nodes;
        List<Edge> edges;

        Graph(String id, List<String> nodes, List<Edge> edges) {
            this.id = id;
            this.nodes = nodes;
            this.edges = edges;
        }
    }

    // Генерация графов с учетом заданной плотности рёбер
    public static List<Graph> generateGraphs() {
        List<Graph> graphs = new ArrayList<>();
        Random rand = new Random();
        int graphId = 1;

        // Создаем графы разных размеров
        graphs.addAll(generateGraphGroup(5, 5, 29, 0.3, 0.6, graphId));
        graphId += 5;
        graphs.addAll(generateGraphGroup(10, 30, 300, 0.1, 0.3, graphId));
        graphId += 10;
        graphs.addAll(generateGraphGroup(10, 300, 1000, 0.03, 0.1, graphId));
        graphId += 10;
        graphs.addAll(generateGraphGroup(3, 1000, 2000, 0.005, 0.02, graphId));

        return graphs;
    }

    private static List<Graph> generateGraphGroup(int count, int minNodes, int maxNodes, double minDensity, double maxDensity, int startId) {
        List<Graph> graphs = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            int numNodes = rand.nextInt(maxNodes - minNodes + 1) + minNodes;
            double density = minDensity + rand.nextDouble() * (maxDensity - minDensity);
            int numEdges = (int) Math.min(numNodes * (numNodes - 1) / 2, density * numNodes * (numNodes - 1) / 2);
            Graph graph = generateGraph(startId + i, numNodes, numEdges);
            graphs.add(graph);
        }
        return graphs;
    }

    // Генерация одного графа
    private static Graph generateGraph(int id, int numNodes, int numEdges) {
        List<String> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Random rand = new Random();

        // Создаем имена вершин
        for (int i = 0; i < numNodes; i++) {
            nodes.add("Node" + (i + 1));
        }

        // Создаем рёбра
        Set<String> edgeSet = new HashSet<>();
        while (edges.size() < numEdges) {
            int u = rand.nextInt(numNodes);
            int v = rand.nextInt(numNodes);
            if (u != v && !edgeSet.contains(u + "-" + v) && !edgeSet.contains(v + "-" + u)) {
                int weight = rand.nextInt(20) + 1;
                edges.add(new Edge(nodes.get(u), nodes.get(v), weight));
                edgeSet.add(u + "-" + v);
            }
        }

        return new Graph("Graph" + id, nodes, edges);
    }

    public static void main(String[] args) {
        List<Graph> graphs = generateGraphs();
        JSONArray graphsJson = new JSONArray();
        for (Graph graph : graphs) {
            JSONObject graphJson = new JSONObject();
            graphJson.put("id", graph.id);
            graphJson.put("nodes", new JSONArray(graph.nodes));
            JSONArray edgesJson = new JSONArray();
            for (Edge edge : graph.edges) {
                JSONObject edgeJson = new JSONObject();
                edgeJson.put("from", edge.from);
                edgeJson.put("to", edge.to);
                edgeJson.put("weight", edge.weight);
                edgesJson.put(edgeJson);
            }
            graphJson.put("edges", edgesJson);
            graphsJson.put(graphJson);
        }

        // Записываем графы в JSON файл
        try (java.io.FileWriter file = new java.io.FileWriter("data/ass_3_input.json")) {
            file.write(graphsJson.toString());
            System.out.println("Saved ass_3_input.json with " + graphs.size() + " graphs");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

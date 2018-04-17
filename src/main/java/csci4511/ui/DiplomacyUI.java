package csci4511.ui;

import csci4511.engine.data.Board;
import csci4511.engine.data.Node;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;

import javax.swing.*;
import java.awt.*;

public class DiplomacyUI {

    private static Node a = new Node("A", true, null);
    private static Node b = new Node("B", true, null);
    private static Node c = new Node("C", true, null);

    public static void main(String[] args) {
        SparseMultigraph<Node, String> map = new SparseMultigraph<>();

        map.addVertex(a);
        map.addVertex(b);

        map.addEdge("The One Road", a, b);
        map.addEdge("That Other One Road", b, c);
        map.addEdge("The Road The Spies Use", c, a);

        FRLayout<Node, String> layout = new FRLayout<>(map);
        layout.setSize(new Dimension(300, 300));
        layout.initialize();

        while (!layout.done()) {
            layout.step();
        }

        VisualizationViewer<Node, String> vs = new VisualizationViewer<>(layout);
        vs.setPreferredSize(new Dimension(350, 350));

        JFrame frame = new JFrame("Map View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vs);
        frame.pack();
        frame.setVisible(true);
    }

    public static void showBoard(Board board, Dimension size) {
        SparseGraph<Node, Integer> map = new SparseGraph<>();
        int i = 0;

        for (Node node : board.getNodes()) {
            map.addVertex(node);

            for (Node armyNode : node.getArmyMovements()) {
                if (!map.containsVertex(armyNode)) {
                    map.addVertex(armyNode);
                }
                if (map.findEdge(node, armyNode) == null) {
                    map.addEdge(i++, node, armyNode);
                }
            }

            for (Node fleetNode : node.getFleetMovements()) {
                if (!map.containsVertex(fleetNode)) {
                    map.addVertex(fleetNode);
                }
                if (map.findEdge(node, fleetNode) == null) {
                    map.addEdge(i++, node, fleetNode);
                }
            }
        }

        FRLayout<Node, Integer> layout = new FRLayout<>(map);
        layout.setSize(size);
        layout.initialize();

        while (!layout.done()) {
            layout.step();
        }

        VisualizationViewer<Node, Integer> vs = new VisualizationViewer<>(layout);
        vs.setPreferredSize(size);

        JFrame frame = new JFrame("Map View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vs);
        frame.pack();
        frame.setVisible(true);
    }
}

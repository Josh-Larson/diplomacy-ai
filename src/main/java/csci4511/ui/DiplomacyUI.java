package csci4511.ui;

import csci4511.engine.data.Board;
import csci4511.engine.data.Node;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
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

    public static JFrame showBoard(Board board, Dimension size) {
        SparseGraph<Node, Integer> map = new SparseGraph<>();
        int i = 0;

        board.getNodes().forEach(map::addVertex);
        for (Node node : board.getNodes()) {
            for (Node armyNode : node.getArmyMovements()) {
				map.addEdge(i++, node, armyNode);
            }

            for (Node fleetNode : node.getFleetMovements()) {
				map.addEdge(i++, node, fleetNode, EdgeType.UNDIRECTED);
            }
        }

        SpringLayout<Node, Integer> layout = new SpringLayout<>(map);
        layout.setSize(size);
        layout.initialize();
//        layout.setAttractionMultiplier(1);
//        layout.setRepulsionMultiplier(1);
//        layout.setMaxIterations(10000);
//
//        while (!layout.done()) {
//            layout.step();
//        }
//		layout.setRepulsionRange(100);
		layout.setForceMultiplier(0.25);
		layout.setStretch(0.7);
//		AtomicInteger force = new AtomicInteger(1);
//		new Thread(() -> {
//			while (true) {
//				layout.setForceMultiplier(Math.sin(force.incrementAndGet() / 10.0 / Math.PI));
//				for (int iter = 0; iter < 1000; iter++)
//					layout.step();
//				Delay.sleepMilli(10);
//				Log.t("Testing FM %d", force.get());
//			}
//		}).start();
		for (int iter = 0; iter < 1000; iter++)
			layout.step();
	
		VisualizationViewer<Node, Integer> vs = new VisualizationViewer<>(layout);
        vs.setPreferredSize(size);
        vs.setVertexToolTipTransformer(Node::getName);

        JFrame frame = new JFrame("Map View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vs);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }
}

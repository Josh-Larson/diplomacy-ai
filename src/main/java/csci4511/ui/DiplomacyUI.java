package csci4511.ui;

import csci4511.engine.data.*;
import csci4511.engine.data.action.Action;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

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

        ISOMLayout<Node, Integer> layout = new ISOMLayout<>(map);
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
//		layout.setForceMultiplier(0.25);
//		layout.setStretch(0.7);
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
//		for (int iter = 0; iter < 1000; iter++)
//			layout.step();
	
		VisualizationViewer<Node, Integer> vs = new VisualizationViewer<>(layout);
        vs.setPreferredSize(size);
        vs.setVertexToolTipTransformer(Node::getName);

        vs.getRenderContext().setEdgeShapeTransformer(EdgeShape.line(map));

        vs.getRenderContext().setVertexShapeTransformer(DiplomacyUI::getNodeShape);
        vs.getRenderContext().setVertexFillPaintTransformer(DiplomacyUI::getNodeColor);

        vs.getRenderContext().setVertexStrokeTransformer(DiplomacyUI::getNodeStroke);
        vs.getRenderContext().setVertexDrawPaintTransformer(DiplomacyUI::getNodeStrokeColor);

        vs.getRenderContext().setVertexLabelTransformer(DiplomacyUI::getNodeLabel);

        JFrame frame = new JFrame("Map View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vs);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    private static String getNodeLabel(Node node) {
        String label = node.getName();
        Unit unit = node.getGarissoned();
        if (unit != null) {
            label += " " + (unit.getType() == UnitType.ARMY ? "A" : "F");
            Action action = unit.getAction();
            if (action != null) {
                label += " " + action.toString()
                        .replaceAll("Action", "")
                        .replaceAll("Unit", "")
                        .replaceAll("Node", "")
                        .replaceAll("\\[\\w*?@", "[");
            }
        }
        return label;
    }

    private static Stroke getNodeStroke(Node node) {
        if (node.getGarissoned() == null)
            return new BasicStroke(1);
        return new BasicStroke(5);
    }

    private static Color getNodeStrokeColor(Node node) {
        if (node.getGarissoned() == null)
            return Color.orange;
        return getCountryColor(node.getGarissoned().getCountry()).darker();
    }

    private static Color getNodeColor(Node node) {
        if (node.getHomeCountry() == null) {
            return node.getArmyMovements().isEmpty() ? Color.blue : Color.lightGray;
        }
        return getCountryColor(node.getHomeCountry());
    }

    private static Color getCountryColor(Country country) {
        switch (country) {
            case ENGLAND:
                return Color.magenta;
            case FRANCE:
                return Color.cyan;
            case GERMANY:
                return Color.black;
            case RUSSIA:
                return Color.white;
            case ITALY:
                return Color.green;
            case AUSTRIA:
                return Color.red;
            case TURKEY:
                return Color.yellow;
            default:
                return Color.orange;
        }
    }

    private static Shape getNodeShape(Node node) {
        if (node.isSupply()) {
            return new StarPolygon(0, 0, 15, 7, 5, -Math.PI / 10);
        }
        return new Ellipse2D.Double(-10, -10, 20, 20);
    }
}

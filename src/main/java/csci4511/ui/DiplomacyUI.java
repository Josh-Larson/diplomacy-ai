package csci4511.ui;

import csci4511.engine.data.*;
import csci4511.engine.data.action.Action;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import me.joshlarson.jlcommon.concurrency.Delay;
import me.joshlarson.jlcommon.log.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DiplomacyUI {

    private static boolean REAL_UI = true;
    
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
    
    public static void showBoardAndWait(Board board, Dimension size) {
    	JFrame frame = showBoard(board, size);
    	while (frame.isShowing()) {
    		if (!Delay.sleepMilli(50))
    			break;
		}
	}

    public static JFrame showBoard(Board board, Dimension size) {
        SparseGraph<Node, Integer> map = new SparseGraph<>();
        int i = 0;

        board.getNodes().forEach(map::addVertex);
        if (!REAL_UI) {
			for (Node node : board.getNodes()) {
				for (Node armyNode : node.getArmyMovements()) {
					map.addEdge(i++, node, armyNode);
				}
		
				for (Node fleetNode : node.getFleetMovements()) {
					map.addEdge(i++, node, fleetNode, EdgeType.UNDIRECTED);
				}
			}
		}

        AbstractLayout<Node, Integer> layout = REAL_UI ? createStaticLayout(map, size, board) : new ISOMLayout<>(map);
        layout.setSize(size);
        layout.initialize();
	
		VisualizationViewer<Node, Integer> vs = new VisualizationViewer<>(layout);
        vs.setSize(size);
        vs.setVertexToolTipTransformer(Node::getName);
	
        if (REAL_UI) {
			try {
				Image img = ImageIO.read(DiplomacyUI.class.getResourceAsStream("/map.gif"));
				vs.addPreRenderPaintable(new Paintable() {
					public void paint(Graphics g) { g.drawImage(img, 0, 0, (int) size.getWidth(), (int) size.getHeight(), null); }
					public boolean useTransform() { return false; }
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			vs.getRenderContext().setVertexShapeTransformer(DiplomacyUI::getRealUINodeShape);
		} else {
			vs.getRenderContext().setEdgeShapeTransformer(EdgeShape.line(map));
			vs.getRenderContext().setVertexLabelTransformer(DiplomacyUI::getNodeLabel);
			vs.getRenderContext().setVertexShapeTransformer(DiplomacyUI::getNodeShape);
		}
        vs.getRenderContext().setVertexFillPaintTransformer(DiplomacyUI::getNodeColor);
        vs.getRenderContext().setVertexStrokeTransformer(DiplomacyUI::getNodeStroke);
        vs.getRenderContext().setVertexDrawPaintTransformer(DiplomacyUI::getNodeStrokeColor);
        
        JFrame frame = new JFrame("Map View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(size);
		frame.getContentPane().add(vs);
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
    	if (node.getArmyMovements().isEmpty())
    		return Color.blue;
        Country country = node.getCountry();
        if (country == null) {
        	Unit unit = node.getGarissoned();
        	if (unit != null)
        		country = unit.getCountry();
        	else
        		return Color.lightGray;
		}
        return getCountryColor(country);
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
    
    private static Shape getRealUINodeShape(Node node) {
    	Unit garissoned = node.getGarissoned();
    	if (garissoned == null) {
    		if (node.isSupply())
				return new Ellipse2D.Double(-10, -10, 20, 20);
    		else
				return new Ellipse2D.Double(0, 0, 1, 1);
		}
    	if (garissoned.getType() == UnitType.ARMY)
			return new StarPolygon(0, 0, 15, 7, 5, -Math.PI / 10);
		else
			return new Ellipse2D.Double(-15, -15, 30, 30);
	}

    private static Shape getNodeShape(Node node) {
        if (node.isSupply()) {
            return new StarPolygon(0, 0, 15, 7, 5, -Math.PI / 10);
        }
        return new Ellipse2D.Double(-10, -10, 20, 20);
    }
	
    private static AbstractLayout<Node, Integer> createStaticLayout(SparseGraph<Node, Integer> graph, Dimension size, Board board) {
    	StaticLayout<Node, Integer> layout = new StaticLayout<>(graph, size);
    	try (BufferedReader reader = new BufferedReader(new InputStreamReader(DiplomacyUI.class.getResourceAsStream("/diplomacy-locations.txt")))) {
    		String line;
    		while ((line = reader.readLine()) != null) {
    			line = line.trim();
    			if (line.isEmpty())
    				continue;
    			String [] parts = line.split(",", 3);
    			layout.setLocation(board.getNode(parts[0]), Double.parseDouble(parts[1]) * size.getWidth(), Double.parseDouble(parts[2]) * size.getHeight());
			}
		} catch (IOException e) {
			Log.w(e);
		}
		return layout;
	}
	
	private static class ImagePanel extends JComponent {
		
		private final Image image;
		private final Dimension size;
		
		public ImagePanel(Image image, Dimension size) {
			this.image = image;
			this.size = size;
			setSize((int) size.getWidth(), (int) size.getHeight());
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, getWidth(), getHeight(), Color.WHITE,  this);
		}
		
	}
	
}

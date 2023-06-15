package entropy.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.List;

import javax.swing.JPanel;

import entropy.map.GameMap;
import entropy.map.Obstacle;
import entropy.map.PathCalculator.PathSegment;
import entropy.map.Tower;
import entropy.wave.WaveCalculator.Tick;

public class GameCanvas extends JPanel {

	private final GameMap state;
	private VolatileImage buffer;
	
	public static final int SIZE = 32;
	
	// TODO does not belong here
	private List<Tick> ticks;
	int currentTick = 0;
	
	private final SidePanel sidePanel;
	
	public GameCanvas(GameMap state, SidePanel sidePanel) {
		this.state = state;
		this.sidePanel = sidePanel;
		// TODO make size dependent rendering
		setSize(state.width*SIZE, state.height*SIZE);
		setMinimumSize(new Dimension(state.width*SIZE, state.height*SIZE));
		setPreferredSize(getMinimumSize());
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				onHover(e.getX(), e.getY());
			}
		});
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(validPlacement && sidePanel.towerPlacingActive) {
					state.placeTower(prevHoverCoords, false);
					updateGraphics();
					repaint();
				}
			}
		});
		// TODO add click listener for placing tower
		
	}
	
	public void setTicks(List<Tick> ticks) {
		this.ticks = ticks;
	}
	
	public void setCurrentTick(int currentTick) {
		this.currentTick = currentTick;
	}

	BufferedImage buffer2;
	
	@Override
	public void paint(Graphics g) {
		if(buffer==null) {
			buffer = createVolatileImage(getWidth(), getHeight());
			buffer2 = new BufferedImage(buffer.getWidth(), buffer.getHeight(), BufferedImage.TYPE_INT_ARGB);
			updateGraphics();
		}
		super.paint(g);
		g.drawImage(buffer2, 0, 0, null);
		g.dispose();
	}

	
	protected void updateGraphics() {
		Graphics g = buffer.getGraphics();
		g.clearRect(0, 0, getWidth(), getHeight());

		// draw obstacles
		
		for(Obstacle o : state.getObstacles()) {
			Rectangle r = o.toRectangleDisplay();
//			int height = 2;
//			int width = o.size;
//			if(o.vertical) {
//				width = 2;
//				height = o.size;
//			}
			g.setColor(new Color(152, 80, 80));
			g.fillRect(r.x*SIZE, r.y*SIZE, r.width*SIZE, r.height*SIZE);
			g.setColor(Color.BLACK);
			g.drawRect(r.x*SIZE+2, r.y*SIZE+2, r.width*SIZE-4, r.height*SIZE-4);
		}
		
		for(Tower t : state.getTowers()) {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(t.x*SIZE, t.y*SIZE, 2*SIZE, 2*SIZE);
			g.setColor(Color.BLACK);
			g.drawRect(t.x*SIZE+2, t.y*SIZE+2, 2*SIZE-4, 2*SIZE-4);
		}
		
		g.setColor(Color.DARK_GRAY);
		
		for(int x = 0; x<state.width; x++) {
			for(int y = 0; y<state.height; y++) {
//				BufferedImage img = ImageUtil.getImageForType(m.getTypeFor(x, y));
//				g.drawImage(img, x*SIZE, y*SIZE, null);
				switch(state.getMap()[y][x].type) {
				case EMPTY: break;
				case START: 
					g.setColor(Color.GREEN);
					g.fillOval(x*SIZE, y*SIZE, SIZE, SIZE);
					g.setColor(Color.DARK_GRAY);
					break;
				case FINISH:
					g.setColor(Color.RED);
					g.fillOval(x*SIZE, y*SIZE, SIZE, SIZE);
					g.setColor(Color.DARK_GRAY);
					break;
				case WP_1:
					g.drawString("1", x*SIZE, y*SIZE+SIZE);
					break;
				case WP_2:
					g.drawString("2", x*SIZE, y*SIZE+SIZE);
					break;
				case WP_3:
					g.drawString("3", x*SIZE, y*SIZE+SIZE);
					break;
				case WP_4:
					g.drawString("4", x*SIZE, y*SIZE+SIZE);
					break;
				case WP_5:
					g.drawString("5", x*SIZE, y*SIZE+SIZE);
					break;
				case WP_6:
					g.drawString("6", x*SIZE, y*SIZE+SIZE);
					break;
				}
			}
		}
		
		g.setColor(Color.lightGray);
		for(int x = 0; x<state.width; x++) {
			g.drawLine(x*SIZE, 0, x*SIZE, getHeight());
			for(int y = 0; y<state.height; y++) {
				g.drawLine(0, y*SIZE, getWidth(), y*SIZE);
			}
		}
		
		// draw edgepoints for debug
		g.setColor(Color.BLACK);
		for(Point2D.Double p : state.getEdgeMap().allPoints) {
			double x = p.x * SIZE;
			double y = p.y * SIZE;
			g.drawOval((int)x-4, (int)y-4, 8, 8);
		}
		
		g.setColor(Color.RED);
//		PathSegment[] path = PathCalculator.calculatePath(state);
		for(PathSegment s : state.getPath()) {
//		PathSegment s = path[0];
			Point2D.Double current = s.path.get(0);
			for(int i = 1; i<s.path.size(); i++) {
				Point2D.Double p2 = s.path.get(i);
				g.drawLine((int)(current.x*SIZE), (int)(current.y*SIZE), (int)(p2.x*SIZE), (int)(p2.y*SIZE));
				current = p2;
			}
		}

		if(ticks!=null && currentTick<ticks.size()) {
//			System.out.println("currentTick " + currentTick);
			// render tick info
			Tick t = ticks.get(currentTick);
			g.setColor(Color.CYAN);
//			System.out.println("rendering tick");
			for(Point2D.Double p : t.creepPositions) {
				double x = p.x * SIZE;
				double y = p.y * SIZE;
				
				g.fillOval((int)x-8, (int)y-8, 16, 16);
//				System.out.println("rendered creep at " + x + "/" + y + "( " + p.x + "/" + p.y + ")");
			}
		}
		
		Graphics g2 = buffer2.getGraphics();
		g2.clearRect(0, 0, buffer2.getWidth(), buffer2.getHeight());
		g2.drawImage(buffer, 0, 0, null);
		g2.dispose();
		g.dispose();
	}

	
	private Point prevHoverCoords = null;
	private boolean validPlacement = false;
	
	public void onHover(int x, int y) {
		if(x<0 || y<0) {
			sidePanel.displayCoords(-1, -1);
			Graphics2D g = buffer2.createGraphics();
			g.clearRect(0, 0, buffer2.getWidth(), buffer2.getHeight());
			g.drawImage(buffer, 0, 0, null);
			g.dispose();
			repaint();
			return;
		}
		int gridX = x/SIZE;
		int gridY = y/SIZE;
		sidePanel.displayCoords(gridX, gridY);
		if(sidePanel.towerPlacingActive) {
			Point p = new Point(gridX, gridY);
			if(prevHoverCoords!=null && prevHoverCoords.equals(p)) {
				return;
			}
			prevHoverCoords = p;
			boolean green = state.placeTower(p, true);
			validPlacement = green;
			Color c = green?Color.GREEN:Color.RED;
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			Graphics2D g = buffer2.createGraphics();
			g.clearRect(0, 0, buffer2.getWidth(), buffer2.getHeight());
			g.drawImage(buffer, 0, 0, null);
			g.setComposite(ac);
			g.setColor(c);
			g.fillRect(gridX*SIZE, gridY*SIZE, 2*SIZE, 2*SIZE);
			g.dispose();
			repaint();
		}
		
	}

}

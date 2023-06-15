package entropy;

import javax.swing.JFrame;

import entropy.map.GameMap;
import entropy.map.MapGenerator;
import entropy.ui.GamePanel;

public class EntropyMain {

	
	
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////		MapParameters p = new MapParameters();
//		p.height = 50;
//		p.width = 50;
//		p.maxObstacles = 50;
//		p.maxObstacleSize = 20;
		GameMap g = MapGenerator.generate(0);
		g.calculateEdgePoints();
		g.calculatePath();
//		 = new GameMap(field);
//		GameCanvas gc = new GameCanvas(g);
//		System.out.println(gc.getMinimumSize());
		GamePanel gp = new GamePanel(g);
		frame.getContentPane().add(gp);
		
		frame.setVisible(true);
		
		frame.setSize(1000, 1000);
		
		frame.pack();
		
//		// 33ms this sucks!
//		long t1 = System.currentTimeMillis();
//		PathSegment[] sa = PathCalculator.calculatePath(g);
//		long t2 = System.currentTimeMillis();
//		System.out.println(t2-t1 + "ms");
//		double path = 0;
//		for(PathSegment s : sa) {
//			path += s.length;
//		}
//		System.out.println("length " + path);
//		
//		List<Tick> ticks = WaveCalculator.processWave(g);
//		long t3 = System.currentTimeMillis();
//		System.out.println("took " + (t3-t2) + "ms");
//		System.out.println("ticks: " + ticks.size());
		// create game state (32x32, 16px)	//
		// draw								//
		// generate maps					//
		// canvas input / output (display coords in side panel)	//
		// waypoints / valid maps			//
		// calc path						//
		// waves walking simulation			//
		// waves walking on button click	//
		// fix that weird bug where path is not displayed path/shortest path
		// placing towers					//
		// fix race condition with checking tower placement and setting tower // (fixed by cheap hack...)
		// (leads to NPE because path=null is set after placing tower then) //
		
		// towers shooting
		// HP calculation / lives
		// ... (TPs, QoL features, animations)
		// make floating point proof? (any FP rounded to 2-3 digits after period, rounded)
		
	}
}

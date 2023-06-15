package entropy.map;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import entropy.map.GameMap.EdgeMap;
import entropy.tile.TileType;
import entropy.util.Util;

public class PathCalculator {

	// calculate list of subPaths from start to finish over all WPs
	
	public static class PathSegment {

		public List<Point2D.Double> path;
		public double length;
		Point2D.Double endPoint;
		public PathSegment(List<Point2D.Double> path, double length, Point2D.Double endPoint) {
			this.path = path;
			this.length = length;
			this.endPoint = endPoint;
		}
		
		
	}
	
	private static class Node {
	final Point2D.Double p;
	Node previous;
	public Node(Point2D.Double p, Node previous) {
		this.p = p;
		this.previous = previous;
	}
	
	public double calcDistance() {
		if(previous==null) {
			return 0;
		}
		return p.distance(previous.p) + previous.calcDistance();
	}
	
}
	
	public static PathSegment[] calculatePath(GameMap map) {
		
		// get edge points (+all start/targets)
		EdgeMap m = map.getEdgeMap();
//		// sort by coords
//		// why?
//		Arrays.sort(edges, EDGEPOINT_COMPARATOR);
		
		// make collision checks with all possible obstacles
		Map<Point2D.Double, List<Point2D.Double>> connections = new HashMap<>();
		
		List<Point2D.Double> list = new ArrayList<>(m.allPoints);
		
		List<Rectangle2D.Double> obstacles = new ArrayList<>();
		for(Obstacle o : map.getObstacles()) {
			obstacles.add(o.toRectanglePath());
		}
		for(Tower t : map.getTowers()) {
			obstacles.add(t.toRectanglePath());
		}

		// fucking points need to be middle of field!!
		
		for(int i = 0; i<list.size(); i++) {
			Point2D.Double p1 = list.get(i);
			for(int j = i+1; j<list.size(); j++) {
				Point2D.Double p2 = list.get(j);
				// check obstacle collisions
				// TODO can be optimized by using only "possible" obstacles between coords
				boolean collides = false;
				for(Rectangle2D.Double r : obstacles) {
					if(r.intersectsLine(new Line2D.Double(p1, p2))) {
						collides = true;
						break;
					}
				}
				if(!collides) {
					Util.addToMap(connections, p1, p2);
					Util.addToMap(connections, p2, p1);
				}
			}
		}
		
		// do bfs on connections
		
		List<TileType> targets = new ArrayList<>();
		for(TileType t : TileType.WPs) {
			if(m.mappedByType.containsKey(t)) {
				targets.add(t);
			}
		}
		targets.add(TileType.FINISH);
		
		List<PathSegment> path = new ArrayList<>();
		
		List<Point2D.Double> start = m.mappedByType.get(TileType.START);
		for(TileType t : targets) {
			PathSegment s = path(start, m.mappedByType.get(t), connections);
			if(s==null) {
				return null;
			}
			path.add(s);
			start = new ArrayList<>();
			start.add(s.endPoint);
		}
		return path.toArray(new PathSegment[0]);
	}
	
	private static PathSegment path(List<Point2D.Double> currentStart, List<Point2D.Double> t, Map<Point2D.Double, List<Point2D.Double>> connections) {
		Set<Point2D.Double> target = new HashSet<>(t);
		Set<Node> currentIteration = new HashSet<>();
		Map<Point2D.Double, Node> nodeMap = new HashMap<>();
		for(Point2D.Double p : currentStart) {
			Node n = new Node(p, null);
			nodeMap.put(p, n);
			currentIteration.add(n);
		}

		while(!currentIteration.isEmpty()) {
			Set<Node> nextIteration = new HashSet<>();
			for(Node c : currentIteration) {
				double cDistance = c.calcDistance();
				List<Point2D.Double> cList = connections.get(c.p);
				if(cList!=null) {
					for(Point2D.Double p : cList) {
						Node n = nodeMap.get(p);
						double addDistance = c.p.distance(p);
						if(n!=null) {
							if(n.calcDistance()>cDistance + addDistance) {
								n.previous = c;
							}
						} else {
							n = new Node(p, c);
							nodeMap.put(p, n);
							nextIteration.add(n);
						}
					}
				}
			}
			currentIteration = nextIteration;
		}
		
		Node shortest = null;
		for(Point2D.Double p : target) {
			Node n = nodeMap.get(p);
			if(n!=null && (shortest==null || n.calcDistance()<shortest.calcDistance())) {
				shortest = n;
			}
		}
		if(shortest==null) {
			return null;
		}
		List<Point2D.Double> path = new ArrayList<>();
		double length = shortest.calcDistance();
		while(shortest.previous!=null) {
			path.add(shortest.p);
			shortest = shortest.previous;
		}
		path.add(shortest.p);
		Collections.reverse(path);
		return new PathSegment(path, length, path.get(path.size()-1));
	}

}

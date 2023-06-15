package entropy.map;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import entropy.map.PathCalculator.PathSegment;
import entropy.map.Tower.TowerType;
import entropy.tile.Tile;
import entropy.tile.TileType;
import entropy.util.Util;

public class GameMap {

	
	// TODO leave lower bit free for "blocked"?
	// TODO different values for each 4 coords of points? (due proper images)
//	public static final byte START = 124;
//	public static final byte FINISH = 126;
//	public static final byte WP_1 = 100;
//	public static final byte WP_2 = 102;
//	public static final byte WP_3 = 104;
//	public static final byte WP_4 = 106;
//	public static final byte WP_5 = 108;
//	public static final byte WP_6 = 110;
//	public static final byte OBSTACLE = 98;
//	
//	public static final byte[] WPs = new byte[] {WP_1, WP_2, WP_3, WP_4, WP_5, WP_6};
	
	public final long seed;
	public final int width;
	public final int height;
	
	private final List<Obstacle> obstacles;
	private final MapPoint start;
	private final MapPoint finish;
	private final List<MapPoint> waypoints;
	
	private final List<MapPoint> allPoints;
	
	private final List<Tower> towers = new ArrayList<>();
	
	// TODO other params
	private final Tile[][] map;
	
	private EdgeMap edgeMap;
	private PathSegment[] path;
	
//	private Waves waves;

	public GameMap(long seed, int width, int height, List<Obstacle> obstacles, MapPoint start, MapPoint finish,
			List<MapPoint> waypoints, Tile[][] map) {
		this.seed = seed;
		this.width = width;
		this.height = height;
		this.obstacles = obstacles;
		this.start = start;
		this.finish = finish;
		this.waypoints = waypoints;
		this.map = map;
		allPoints = new ArrayList<>();
		allPoints.add(start);
		allPoints.addAll(this.waypoints);
		allPoints.add(finish);
	}


	public List<Obstacle> getObstacles() {
		return obstacles;
	}


	public MapPoint getStart() {
		return start;
	}


	public MapPoint getFinish() {
		return finish;
	}


	public List<MapPoint> getWaypoints() {
		return waypoints;
	}


	public Tile[][] getMap() {
		return map;
	}
	
	public static class EdgeMap {
		public Set<Point2D.Double> allPoints;
		public Map<TileType, List<Point2D.Double>> mappedByType;
		public EdgeMap(Set<Point2D.Double> allPoints, Map<TileType, List<Point2D.Double>> mappedByType) {
			this.allPoints = allPoints;
			this.mappedByType = mappedByType;
		}
		
	}
	
	
	public EdgeMap getEdgeMap() {
		return edgeMap;
	}


	public PathSegment[] getPath() {
		return path;
	}


	public EdgeMap calculateEdgePoints() {
		Set<Point2D.Double> edges = new HashSet<>();
		Map<TileType, List<Point2D.Double>> mappedByType = new HashMap<>();
		// start
		List<Point2D.Double> s = start.toNonBlockedPointsPath(map);
		edges.addAll(s);
		mappedByType.put(TileType.START, s);
		// finish
		List<Point2D.Double> f = finish.toNonBlockedPointsPath(map);
		edges.addAll(f);
		mappedByType.put(TileType.FINISH, f);
		
		// wps
		for(MapPoint p : waypoints) {
			List<Point2D.Double> w = p.toNonBlockedPointsPath(map);
			edges.addAll(w);
			mappedByType.put(p.type, w);
		}
		
		// obstacles
		// TODO ideally do some pruning
		for(Obstacle o : obstacles) {
			// edges are 1-off the actual obstacle, if still on map
			for(Point2D.Double p :  Util.getEdgePoints(o, map)) {
				if(edges.add(p)) {
					Util.addToMap(mappedByType, TileType.EMPTY, p);
				}
			}
		}
		for(Tower t : towers) {
			// edges are 1-off the actual obstacle, if still on map
			for(Point2D.Double p :  Util.getEdgePoints(t, map)) {
				if(edges.add(p)) {
					Util.addToMap(mappedByType, TileType.EMPTY, p);
				}
			}
		}
		EdgeMap m = new EdgeMap(edges, mappedByType);
		edgeMap = m;
		return m;
	}
	
	public PathSegment[] calculatePath() {
		path = PathCalculator.calculatePath(this);
		return path;
	}
	
	static boolean existsNonBlocked(Point p, Tile[][] map) {
		if(p.y<0 || p.y >=map.length) {
			return false;
		}
		if(p.x<0 || p.x >=map[p.y].length) {
			return false;
		}
		return !map[p.y][p.x].isBlocked();
	}
	
	// really bad hack for race condition :)
	public synchronized boolean placeTower(Point coords, boolean testOnly) {

		int x = coords.x;
		int y = coords.y;
		
		Set<Point> towerPoints = new HashSet<>();
		towerPoints.add(new Point(x, y));
		towerPoints.add(new Point(x+1, y));
		towerPoints.add(new Point(x, y+1));
		towerPoints.add(new Point(x+1, y+1));
		// valid coords
		// check if tiles are empty at coords (+1/+1+1)
		for(Point p : towerPoints) {
			if(!existsNonBlocked(p, map)) {
				return false;
			}
		}
		// check if no mapPoint completely covered
		for(MapPoint p : allPoints) {
			if(p.fullyCovered(towerPoints, map)) {
				return false;
			}
		}
		
		// place tower
		Tower t = new Tower(coords.x, coords.y, TowerType.NORMAL);
		t.applyToMap(map);
		towers.add(t);
		// recalculate edgePoints and path
		calculateEdgePoints();
		calculatePath();
		boolean ok = path!=null;
		// if testOnly remove again
		if(testOnly || !ok) {
			t.removeFromMap(map);
			calculateEdgePoints();
			calculatePath();
			towers.remove(t);
		}
		return ok;
	}


	public List<Tower> getTowers() {
		return towers;
	}
	
	
}

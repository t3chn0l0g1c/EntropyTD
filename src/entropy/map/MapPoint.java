package entropy.map;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import entropy.tile.Tile;
import entropy.tile.TileType;

// has always size 2x2
public class MapPoint {
	public final int x;
	public final int y;
	public final TileType type;

	public MapPoint(int x, int y, TileType type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	public boolean overlaps(MapPoint p2) {
		return Math.abs(x-p2.x)<2 && Math.abs(y-p2.y)<2;
	}
	
	public boolean overlaps(List<MapPoint> list) {
		for(MapPoint p2 : list) {
			if(overlaps(p2)) {
				return true;
			}
		}
		return false;
	}
	
	public void addToMap(Tile[][] map) {
		set(x, y, type, map);
		set(x+1, y, type, map);
		set(x, y+1, type, map);
		set(x+1, y+1, type, map);
	}
	
	public List<Point.Double> toNonBlockedPointsPath(Tile[][] map) {
		List<Point2D.Double> result = new ArrayList<>();
		if(!map[y][x].isBlocked()) {
			result.add(new Point2D.Double(x+0.5d, y+0.5d));
		}
		if(!map[y][x+1].isBlocked()) {
			result.add(new Point2D.Double(x+1.5d, y+0.5d));
		}
		if(!map[y+1][x].isBlocked()) {
			result.add(new Point2D.Double(x+0.5d, y+1.5d));
		}
		if(!map[y+1][x+1].isBlocked()) {
			result.add(new Point2D.Double(x+1.5d, y+1.5d));
		}
		return result;
	}
	
	private static void set(int x, int y, TileType type, Tile[][] map) {
		if(map[y][x].type!=TileType.EMPTY) {
			throw new RuntimeException("overlap at " + x + "/" + y);
		}
		map[y][x].type = type;
	}
	
	public void print() {
		System.out.println(type + " at " + x + "/" + y );
	}

	public boolean fullyCovered(Set<Point> towerPoints, Tile[][] map) {
		int points = 4;
		Point p1 = new Point(x, y);
		if(!GameMap.existsNonBlocked(p1, map) || towerPoints.contains(p1)) {
			points--;
		}
		Point p2 = new Point(x+1, y);
		if(!GameMap.existsNonBlocked(p2, map) || towerPoints.contains(p2)) {
			points--;
		}
		Point p3 = new Point(x, y+1);
		if(!GameMap.existsNonBlocked(p3, map) || towerPoints.contains(p3)) {
			points--;
		}
		Point p4 = new Point(x+1, y+1);
		if(!GameMap.existsNonBlocked(p4, map) || towerPoints.contains(p4)) {
			points--;
		}
		return points==0;
	}
	
}
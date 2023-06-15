package entropy.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import entropy.map.Obstacle;
import entropy.map.Tower;
import entropy.tile.Tile;

public class Util {

	
	public static <T, U> void addToMap(Map<T, List<U>> map, T t, U u) {
		List<U> list = map.get(t);
		if(list==null) {
			list = new ArrayList<>();
			map.put(t, list);
		}
		list.add(u);
	}
	
	public static boolean exists(int x, int y, Tile[][] map) {
		return y >=0 && x>=0 && y < map.length && x < map[0].length;
	}
	
	// check field directly and then add offset (0.5, -0.5)
	public static void ifExistsNotBlockedAdd(int x, double xOffset, int y, double yOffset, Tile[][] map, List<Point2D.Double> list) {
		if(exists(x, y, map) && !map[y][x].isBlocked()) {
			double x2 = x + xOffset;
			double y2 = y + yOffset;
			list.add(new Point2D.Double(x2, y2));
		}
	}
	
	public static List<Point2D.Double> getEdgePoints(Obstacle o, Tile[][] map) {
		return getEdgePoints(o.x, o.y, o.size, o.vertical, map);
	}
	
	public static List<Point2D.Double> getEdgePoints(Tower o, Tile[][] map) {
		return getEdgePoints(o.x, o.y, 2, false, map);
	}
	
	public static List<Point2D.Double> getEdgePoints(int x, int y, int size, boolean vertical, Tile[][] map) {
		// check with -1, add with -0.5 ...
		List<Point2D.Double> result = new ArrayList<>();
		// upper left
		Util.ifExistsNotBlockedAdd(x-1, 0.5, y-1, 0.5, map, result);
		if(vertical) {
			// upper right
			Util.ifExistsNotBlockedAdd(x+2, 0.5, y-1, 0.5, map, result);
			//lower left
			Util.ifExistsNotBlockedAdd(x-1, 0.5, y+size, 0.5, map, result);
			// lower right
			Util.ifExistsNotBlockedAdd(x+2, 0.5, y+size, 0.5, map, result);
		} else {
			// upper right
			Util.ifExistsNotBlockedAdd(x+size, 0.5, y-1, 0.5, map, result);
			//lower left
			Util.ifExistsNotBlockedAdd(x-1, 0.5, y+2, 0.5, map, result);
			// lower right
			Util.ifExistsNotBlockedAdd(x+size, 0.5, y+2, 0.5, map, result);
		}
		return result;
	}
	
}

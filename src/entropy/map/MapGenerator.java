package entropy.map;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import entropy.tile.Tile;
import entropy.tile.TileType;

public class MapGenerator {

	// the higher the less obstacles
	public static final int MAX_OBSTACLE_RATIO = 7;
	// off by one
	public static final int MAX_OBSTACLE_SIZE = 5;

	// we assume there is always enough space
	private static MapPoint randomPoint(Random random, int maxX, int maxY, TileType type, List<MapPoint> noOverlap) {
		MapPoint result = null;
		do {
			int x = random.nextInt(maxX);
			int y = random.nextInt(maxY);
			result = new MapPoint(x, y, type);
		} while (result.overlaps(noOverlap));
		return result;
	}

	
	private static void tryPlaceObstacle(Obstacle o, Tile[][] map, MapPoint start, MapPoint finish, List<MapPoint> wpList, List<Obstacle> obstacles) {

		// check if overlaps with any start/finish/wp
		if(!o.canApplyToMap(map)) {
			return;
		}
		// place
		o.applyToMap(map);
		// do path validity check
		boolean valid = validPath(start, finish, map, wpList);

		if (valid) {
			// add to list and map = temp;
//			map = temp;
			obstacles.add(o);
		} else {
			o.removeFromMap(map);
		}
		// if not, ignore
	}
	public static GameMap generate(long seed) {

		Random r = new Random(seed);

		// width
		// height
		// # wps
		// obstacles
		// rest
//		int width = r.nextInt(32)+8;
//		int height = r.nextInt(32)+8;
		int width = 32;
		int height = 32;

		List<MapPoint> used = new ArrayList<>();

		MapPoint start = randomPoint(r, width-1, height-1, TileType.START, used);
		used.add(start);

		MapPoint finish = randomPoint(r, width-1, height-1, TileType.FINISH, used);
		used.add(finish);
		
		Tile[][] map = new Tile[height][width];
		for(int y = 0; y<map.length; y++) {
			for(int x = 0; x<map[0].length; x++) {
				map[y][x] = new Tile();
			}
		}
start.print();
finish.print();
		start.addToMap(map);
		finish.addToMap(map);

		int wps = r.nextInt(6);

		List<MapPoint> wpList = new ArrayList<>();

		for (int i = 0; i < wps; i++) {

			MapPoint wp = randomPoint(r, width-1, height-1, TileType.WPs[i], used);
			wp.addToMap(map);
			// other coords of point
			used.add(wp);
			wpList.add(wp);
			wp.print();
		}

		// set obstacles
		// obstacles can be max 2/3 of map
		// arbitrary value
		// use sqrt or such
		int maxObstacles = r.nextInt((height * width) / MAX_OBSTACLE_RATIO);

		List<Obstacle> obstacles = new ArrayList<>();

		for (int i = 0; i < maxObstacles; i++) {

			int oX = r.nextInt(width);
			int oY = r.nextInt(height);
			int oSize = r.nextInt(MAX_OBSTACLE_SIZE) + 1;
			boolean alignment = r.nextBoolean();
//System.out.println("obstacle at " + oX + " / " + oY + " size " + oSize + " vertical " + alignment);
			// create obstacle
			Obstacle o = new Obstacle(oX, oY, oSize, alignment, i);
			
			tryPlaceObstacle(o, map, start, finish, wpList, obstacles);

		}
//		int oX = 5;
//		int oY = 5;
//		int oSize = 5;
//		boolean alignment = false;
//		Obstacle o = new Obstacle(oX, oY, oSize, alignment, 1);
//		tryPlaceObstacle(o, map, start, finish, wpList, obstacles);
//		oX = 7;
//		oY = 12;
//		alignment = true;
//		o = new Obstacle(oX, oY, oSize, alignment, 2);
//		tryPlaceObstacle(o, map, start, finish, wpList, obstacles);
//		oX = 8;
//		oY = 11;
//		alignment = true;
//		o = new Obstacle(oX, oY, oSize, alignment, 3);
//		tryPlaceObstacle(o, map, start, finish, wpList, obstacles);
//		oX = 9;
//		oY = 14;
//		alignment = false;
//		o = new Obstacle(oX, oY, oSize, alignment, 3);
//		tryPlaceObstacle(o, map, start, finish, wpList, obstacles);
//		oX = 6;
//		oY = 16;
//		alignment = false;
//		o = new Obstacle(oX, oY, oSize, alignment, 3);
//		tryPlaceObstacle(o, map, start, finish, wpList, obstacles);
System.out.println("map generated with");
System.out.println("width " + width);
System.out.println("height " + height);
System.out.println("obstacles: " + obstacles.size());
System.out.println("WPs: " + wpList.size());
		return new GameMap(seed, width, height, obstacles, start, finish, wpList, map);
	}

	private static boolean passable(Point p, Tile[][] map, Set<Point> used) {
		return !used.contains(p) && !map[p.y][p.x].isBlocked();
	}
	private static boolean validPath(MapPoint start, MapPoint finish, Tile[][] map, List<MapPoint> wpList) {
		// use bfs for simplicity, as long as TPs don't exist
		List<MapPoint> targets = new ArrayList<>();
		targets.addAll(wpList);
		targets.add(finish);
		Point[] stack = new Point[(map.length+2) * (map[0].length+2)];
		int fill = 0;
		List<Point2D.Double> t = start.toNonBlockedPointsPath(map);
		if(t.size()==0) {
			return false;
		}
		for(Point2D.Double p : t) {
			// ugly hack
			stack[fill++] = new Point((int)p.x, (int)p.y);
		}

		Set<Point> used = null;

		for (MapPoint target : targets) {
			used = new HashSet<>();
			Point endPoint = null;
//			System.out.println("checking for " + target.type);
			for (int i = 0; i < fill; i++) {
				int x = stack[i].x;
				int y = stack[i].y;
				Tile current = map[y][x];
				if (current.type == target.type) {
					// finished current iteration, break
					endPoint = stack[i];
					break;
				}
				Point up = new Point(x, y - 1);
				if (up.y >= 0 && passable(up, map, used)) {
					stack[fill++] = up;
					used.add(up);
				}
				Point down = new Point(x, y + 1);
				if (down.y < map.length && passable(down, map, used)) {
					stack[fill++] = down;
					used.add(down);
				}
				Point left = new Point(x - 1, y);
				if (left.x >= 0 && passable(left, map, used)) {
					stack[fill++] = left;
					used.add(left);
				}
				Point right = new Point(x + 1, y);
				if (right.x < map[0].length && passable(right, map, used)) {
					stack[fill++] = right;
					used.add(right);
				}
			}
			if(endPoint == null) {
//				System.out.println("NOT found");
				return false;
			}
//			System.out.println("found");
			fill = 0;
			stack[fill++] = endPoint;
		}
		return true;
	}

//	private static byte[][] copy(byte[][] map) {
//		byte[][] result = new byte[map.length][];
//		for (int i = 0; i < result.length; i++) {
//			result[i] = Arrays.copyOf(map[i], map[i].length);
//		}
//		return result;
//	}

}

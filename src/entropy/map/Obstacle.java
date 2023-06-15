package entropy.map;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import entropy.tile.Tile;
import entropy.tile.TileType;
import entropy.util.Util;

// this could be a Record :)
public class Obstacle {
	
	public final int id;
	
	public final int x;
	public final int y;
	// length, width is always 2
	public final int size;
	// true is vertical
	public final boolean vertical;
	
	public Obstacle(int x, int y, int size, boolean alignment, int id) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.vertical = alignment;
		this.id = id;
	}
	
	public List<Point> toPoints() {
		List<Point> result = new ArrayList<>();
		if(vertical) {
			// vertical
			for(int i = 0; i<size; i++) {
				result.add(new Point(x, y+i));
				result.add(new Point(x+1, y+i));
			}
		} else {
			// horizontal
			for(int i = 0; i<size; i++) {
				result.add(new Point(x+i, y));
				result.add(new Point(x+i, y+1));
			}
		}
		return result;
	}
	
	public void applyToMap(Tile[][] map) {
		applyToMap(map, Tile::addObstacle);
	}
	public void removeFromMap(Tile[][] map) {
		applyToMap(map, Tile::removeObstacle);
	}
	
	public void applyToMap(Tile[][] map, BiConsumer<Tile, Obstacle> c) {
		for(Point p : toPoints()) {
			ifExists(p.x, p.y, map, c);
		}
	}
	
	private void ifExists(int x, int y, Tile[][] map, BiConsumer<Tile, Obstacle> c) {
		if(Util.exists(x, y, map)) {
			c.accept(map[y][x], Obstacle.this);
		}
	}
	
	public boolean canApplyToMap(Tile[][] map) {
		 for(Point p : toPoints()) {
			 if(Util.exists(p.x, p.y, map) && map[p.y][p.x].type!=TileType.EMPTY) {
				 return false;
			 }
		 }
		 return true;
	}
	
	public Rectangle toRectangleDisplay() {
		if(vertical) {
			return new Rectangle(x, y, 2, size);
		} else {
			return new Rectangle(x, y, size, 2);
		}
	}
	
	public Rectangle2D.Double toRectanglePath() {
		if(vertical) {
			return new Rectangle2D.Double(x-0.4, y-0.4, 2.8, size+0.8);
		} else {
			return new Rectangle2D.Double(x-0.4, y-0.4, size+0.8, 2.8);
		}
	}
	
}
package entropy.map;

import java.awt.geom.Rectangle2D;

import entropy.tile.Tile;

public class Tower {

	public enum TowerType {
		NORMAL,
		HIGH_DMG;
	}
	
	public enum AttackType {
		DIRECT,
		PROJECTILE;
	}
	
	public final int x;
	public final int y;
	
	private TowerType type;
	// TODO firing speed
	// upgrades

	public Tower(int x, int y, TowerType type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	public void applyToMap(Tile[][] map) {
		map[y][x].addTower(this);
		map[y+1][x].addTower(this);
		map[y][x+1].addTower(this);
		map[y+1][x+1].addTower(this);
	}
	
	public void removeFromMap(Tile[][] map) {
		map[y][x].removeTower(this);
		map[y+1][x].removeTower(this);
		map[y][x+1].removeTower(this);
		map[y+1][x+1].removeTower(this);
	}

	public Rectangle2D.Double toRectanglePath() {
		return new Rectangle2D.Double(x-0.4, y-0.4, 2.8, 2.8);
	}
	
}

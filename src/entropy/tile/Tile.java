package entropy.tile;

import java.util.ArrayList;
import java.util.List;

import entropy.map.Obstacle;
import entropy.map.Tower;

public class Tile {

	public TileType type = TileType.EMPTY;

	private List<Obstacle> obstacles = null;
	
	private List<Tower> towers = null;
	
	
	public void addObstacle(Obstacle o) {
		if(obstacles==null) {
			obstacles = new ArrayList<>();
		}
		obstacles.add(o);
	}
	
	public void removeObstacle(Obstacle o) {
		if(obstacles!=null) {
			if(!obstacles.remove(o)) {
				System.err.println("removed non existing obstacle!!");
			}
			if(obstacles.isEmpty()) {
				obstacles = null;
			}
		}
	}
	
	public void addTower(Tower o) {
		if(towers==null) {
			towers = new ArrayList<>();
		}
		towers.add(o);
	}
	
	public void removeTower(Tower o) {
		if(towers!=null) {
			if(!towers.remove(o)) {
				System.err.println("removed non existing tower!!");
			}
			if(towers.isEmpty()) {
				towers = null;
			}
		}
	}
	
	public boolean isBlocked() {
		return obstacles!=null || towers!=null;
	}

	public void printObstacles() {
		if(obstacles==null) {
			System.out.println("NO OBSTACLES");
			return;
		}
		for(Obstacle o: obstacles) {
			System.out.println("obstacle " + o.id + " at " + o.x + "/" + o.y + " vertical " + o.vertical + " size " + o.size);
		}
		System.out.println();
	}
	
	
}

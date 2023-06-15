package entropy.wave;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import entropy.map.GameMap;
import entropy.map.PathCalculator;
import entropy.map.PathCalculator.PathSegment;
import entropy.map.Tower;
import entropy.map.Tower.AttackType;
import entropy.wave.Waves.Creep;

public class WaveCalculator {

	// TODO
	// lets wave travel on map
	// towers fire & dmg
	// kill count etc
	// output is a stepping in game ticks with actions for UI to display
	
	public static class Tick {
		// id for debug
		final int id;
		// all creeps position and hp
		// for now just position, anything else does not yet exist
		public List<Point2D.Double> creepPositions = new ArrayList<>();
		// projectiles & location
		// kills & dmg

		public Tick(int id) {
			this.id = id;
		}
		
	}
	
	// TODO needs to stand in the middle of the 4 fields its holding!
	private static class WaveTower {
		public final int x;
		public final int y;
		public final double range;
		// fire if tick <= current
		private int nextFireTick = 0;
		public WaveTower(int x, int y, double range) {
			this.x = x;
			this.y = y;
			this.range = range;
		}
		
		
//		private AttackType attackType;
		
		// 0 for no splash
//		private final double splashRange;
	}
	
//	public static class Projectile {
//		public final Point2D.Double coords;
//		public final Creep target;
//		public final Point2D.Double targetPosition;
//	}
	
	

	// TODO towers shooting
	// direct fire, projectile and splash
	
	public static List<Tick> processWave(GameMap map) {
		List<Tick> list = new ArrayList<>();
		PathSegment[] path = map.getPath();
		
		List<Tower> tl = map.getTowers();
		List<WaveTower> towers = new ArrayList<>();
		for(Tower tt : tl) {
			WaveTower wt = new WaveTower(tt.x+1, tt.y+1, 7);
			towers.add(wt);
		}
		int creepId = 1;
		List<Creep> creeps = new ArrayList<>();
		// start with first creep on start of path
		// TODO creep should be made by map or Waves

		Point2D.Double start = path[0].path.get(0);

		int ticks = 0;
		int spawned = 0;
		int waveSize = 5; // TODO move to Waves
		
		// TODO starts at wrong spot (end)
		// doesn't move
		
		do {
			Tick t = new Tick(ticks++);
			list.add(t);
			if(t.id%15==0 && spawned < waveSize) {
				// spawn creep
				Creep c = new Creep(creepId++);
				c.currentPosition = new Point2D.Double(start.x, start.y);
				c.pathSegmentIdx = 0;
				creeps.add(c);
				spawned++;
			}
			// move creeps
			// for now we'll ignore the first one will be moved on spawn
			Iterator<Creep> creepIter = creeps.iterator();
			while(creepIter.hasNext()) {
				Creep c = creepIter.next();
				// move creep
				double creepSpeed = c.speed;
				// get length (distance) of current path segment
//				Point2D.Double creepPos = c.currentPosition;
				PathSegment segment = path[c.pathSegmentIdx];
				Point2D.Double targetPoint = segment.path.get(c.targetPointIdx);

				double remaining = c.currentPosition.distance(targetPoint);
				// (assuming default speed is 0.3 and shortest segment can't be shorter than that)
				if(remaining<creepSpeed) {
					creepSpeed -= remaining;
					// switch to next point (currentPos is set to current target then)
					c.targetPointIdx++;
					// if no more, next pathSegment
					if(c.targetPointIdx>=segment.path.size()) {
						c.pathSegmentIdx++;
						// if no more, despawn
						if(c.pathSegmentIdx>=path.length) {
							creepIter.remove();
							continue;
						}
						segment = path[c.pathSegmentIdx];
						c.targetPointIdx = 1;
					}
					targetPoint = segment.path.get(c.targetPointIdx);
					Point2D.Double newStart = segment.path.get((c.targetPointIdx-1));
					c.currentPosition = new Point2D.Double(newStart.x, newStart.y);
					remaining = c.currentPosition.distance(targetPoint);
				}
				// scale x/y delta to creep move speed
				double xOffset = targetPoint.x - c.currentPosition.x;
				double yOffset = targetPoint.y - c.currentPosition.y;
				double factor = remaining / creepSpeed;
				// apply x/y delta to creep
				c.currentPosition.x += xOffset /factor;
				c.currentPosition.y += yOffset /factor;
				
				// apply new data to current tick
				t.creepPositions.add(new Point2D.Double(c.currentPosition.x, c.currentPosition.y));
			}
			
			// TODO towers firing
			// TODO projectiles moving
			
			list.add(t);
		} while(!creeps.isEmpty());
		System.out.println("ticks: " + list.size());
		return list;
		// after x ticks (15 default?) set next one (use precalculated tick numbers to determine when)
		
		// keep traveling them on paths til >= finish
		// despawn there
		// once all finished return result;
	}
	
}

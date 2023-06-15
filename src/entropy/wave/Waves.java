package entropy.wave;

import java.awt.geom.Point2D;

public class Waves {

	public enum WaveType {
		// all types of creeps go here
		NORMAL; // for now just one
	}
	public static class Creep {
		final int id;
		double currentHp;
		Point2D.Double currentPosition;
		double speed = 0.2; // per tick, 30 tick / s
//		PathSegment currentLine;
		int pathSegmentIdx;
		int targetPointIdx;
		
		public Creep(int id) {
			this.id = id;
		}
	}
	
	// TODO
	// x types of creeps
	// y number
	// track wave #
	// scaling factor
}

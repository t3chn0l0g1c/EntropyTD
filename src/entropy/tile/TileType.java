package entropy.tile;

public enum TileType {

	// how to differ multi tile differences (e.g. start, wp_1, etc) graphics?
	// TODO differ tileType from textureType!!
	
	EMPTY,
	START,
	WP_1,
	WP_2,
	WP_3,
	WP_4,
	WP_5,
	WP_6,
	FINISH;
	
	public static final TileType[] WPs = new TileType[] {WP_1, WP_2, WP_3, WP_4, WP_5, WP_6};
}

package entropy.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SidePanel extends JPanel{

	
	// TODO
	// coord display
	// tile info display
	// unit details
	private final JLabel coords;
	
	private final JButton startWave;
	
	// TODO stays activated til clicked again
	private final JButton placeTower;
	
	public SidePanel(GamePanel panel) {
		coords = new JLabel();
		add(coords);
		startWave = new JButton("Start Wave");
		add(startWave);
		
		placeTower = new JButton("Place tower");
		add(placeTower);
		placeTower.addActionListener(e-> togglePlaceTower(placeTower));
		
		startWave.addActionListener(e-> panel.startWave(startWave, placeTower));
		// set preferred size
	}
	
	
	boolean towerPlacingActive = false;
	
	private void togglePlaceTower(JButton placeTower2) {
		if(towerPlacingActive) {
			placeTower.setText("Place tower");
		} else {
			placeTower.setText("Stop placing tower");
		}
		towerPlacingActive = !towerPlacingActive;
	}

	public void displayCoords(int x, int y) {
		if(x<0) {
			coords.setText("- / -");
		} else {
			coords.setText(x + "/" + y);
		}
	}

}

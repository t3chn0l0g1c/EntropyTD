package entropy.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import entropy.map.GameMap;
import entropy.wave.WaveCalculator;
import entropy.wave.WaveCalculator.Tick;

public class GamePanel extends JPanel{

	
	// TODO holds canvas, sidePanel and bottom panel
	
	private SidePanel sidePanel;
//	private BottomPanel bottomPanel;
	private GameCanvas gameCanvas;
	private GameMap state;
	
	public GamePanel(GameMap gs) {
		this.state = gs;
		sidePanel = new SidePanel(this);
//		bottomPanel = new BottomPanel();
		
		gameCanvas = new GameCanvas(gs, sidePanel);
		gameCanvas.setBackground(Color.YELLOW);

		setLayout(new GridBagLayout());
		
		sidePanel.setBackground(Color.green);
//		sidePanel.setMinimumSize(new Dimension(50, 200));
		sidePanel.setPreferredSize(new Dimension(200, 800));
//		sidePanel.setSize(50, 200);
		
//		bottomPanel.setBackground(Color.BLUE);
////		bottomPanel.setMinimumSize(new Dimension(200, 50));
////		bottomPanel.setSize(1000, 50);
//		bottomPanel.setPreferredSize(new Dimension(1000, 200));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(gameCanvas, gbc);
		gbc.gridx = 1;
		add(sidePanel, gbc);
//		gbc.gridx = 0;
//		gbc.gridy = 1;
//		gbc.gridwidth = 2;
//		add(bottomPanel, gbc);
		
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
//				System.out.println("washere");
//				if(e.getComponent()==gameCanvas) {
//					gameCanvas.mouseOver(e.getX(), e.getY());
//				} else {
//					sidePanel.displayCoords(-1, -1);
					gameCanvas.onHover(-1, -1);
//				}
				
			}
		});
	}

	public void startWave(JButton startButton, JButton towerButton) {
		startButton.setEnabled(false);
		towerButton.setEnabled(false);
		boolean b = sidePanel.towerPlacingActive;
		sidePanel.towerPlacingActive = false;
		System.out.println("starting wave...");
		List<Tick> ticks = WaveCalculator.processWave(state);
		gameCanvas.setTicks(ticks);
		gameCanvas.currentTick = -1;
		int tickCount = ticks.size();
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					while(gameCanvas.currentTick<tickCount) {
						gameCanvas.currentTick++;
						gameCanvas.updateGraphics();
						gameCanvas.repaint();
						try {
							Thread.sleep(33);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					// just in case that it really gets cleaned up
				} finally {
					gameCanvas.setTicks(null);
					startButton.setEnabled(true);
					towerButton.setEnabled(true);
					sidePanel.towerPlacingActive = b;
				}
			}
		};
		t.start();
		// TODO start wave
		// deactivate start wave button
		// spawn thread
		// thread iterates through ticks and sleeps at tick rate
		// calls repaint on canvas
		// activates button again
//		.
	}
	
}

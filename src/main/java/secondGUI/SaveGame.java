package secondGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;

import adds.InputAction;
import builders.FoxFontBuilder;
import builders.ResManager;
import games.FoxCursor;
import registry.Registry;



@SuppressWarnings("serial")
public class SaveGame extends JDialog {
	private Dimension toolk = Toolkit.getDefaultToolkit().getScreenSize();
	private int WIDTH = (int) (this.toolk.getWidth() * 0.5D), HEIGHT = (int) (toolk.getHeight() * 0.75D);
	
	private Rectangle button0Rect, button1Rect, button2Rect;
	private Double widthPercent = WIDTH /100D, heightPercent = HEIGHT / 100D;
	private Double buttonsWidth = widthPercent * 20D - 10D;
	private Boolean saveChosen = true;
	
	private Font f0 = FoxFontBuilder.setFoxFont(8, 20, true);
	private Font f1 = FoxFontBuilder.setFoxFont(9, 18, false);
	
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		Registry.render(g2D, false);
		
		g2D.drawImage(ResManager.getBImage("picSaveLoad"), 0, 0, getWidth(), getHeight(), null);
		
		g2D.setColor(Color.ORANGE);
		g2D.setFont(f0);
		g2D.drawString("Загрузка и сохранение:", (int) (widthPercent * 4D), (int) (heightPercent * 3.5D));
		
		g2D.drawRect((int) (widthPercent * 67D), (int) (heightPercent * 6D), (int) (widthPercent * 30D), (int) (heightPercent * 5D));
//		g2D.drawRect((int) (widthPercent * 67D), (int) (heightPercent * 12D), (int) (widthPercent * 30D), (int) (heightPercent * 5D));
//		g2D.drawRect((int) (widthPercent * 67D), (int) (heightPercent * 18D), (int) (widthPercent * 30D), (int) (heightPercent * 5D));
//		g2D.drawRect((int) (widthPercent * 67D), (int) (heightPercent * 24D), (int) (widthPercent * 30D), (int) (heightPercent * 5D));
//		g2D.drawRect((int) (widthPercent * 67D), (int) (heightPercent * 30D), (int) (widthPercent * 30D), (int) (heightPercent * 5D));
//		g2D.drawRect((int) (widthPercent * 67D), (int) (heightPercent * 36D), (int) (widthPercent * 30D), (int) (heightPercent * 5D));
//		g2D.drawRect((int) (widthPercent * 67D), (int) (heightPercent * 42D), (int) (widthPercent * 30D), (int) (heightPercent * 5D));
//		g2D.drawRect((int) (widthPercent * 67D), (int) (heightPercent * 48D), (int) (widthPercent * 30D), (int) (heightPercent * 5D));
//		g2D.drawRect((int) (widthPercent * 67D), (int) (heightPercent * 54D), (int) (widthPercent * 30D), (int) (heightPercent * 5D));
		
		if (saveChosen) {
			g2D.setFont(f1);
			g2D.drawString("Info will be here soon...", (int) (widthPercent * 4D), (int) (heightPercent * 73D));
			g2D.drawString("Info will be here soon...", (int) (widthPercent * 4D), (int) (heightPercent * 77D));
			g2D.drawString("Info will be here soon...", (int) (widthPercent * 4D), (int) (heightPercent * 81D));
			g2D.drawString("Info will be here soon...", (int) (widthPercent * 4D), (int) (heightPercent * 85D));
			g2D.drawString("Info will be here soon...", (int) (widthPercent * 4D), (int) (heightPercent * 89D));
			
			g2D.setColor(Color.ORANGE);
			g2D.drawRect(button0Rect.x, button0Rect.y, (int) button0Rect.getWidth(), (int) button0Rect.getHeight());
			g2D.drawString(
					"option 0", 
					(int) (button0Rect.getCenterX() - FoxFontBuilder.getStringBounds(g2D, "option 0").getWidth() / 2D), 
					(int) (button0Rect.getCenterY() + FoxFontBuilder.getStringBounds(g2D, "option 0").getHeight() / 5D));
			
			g2D.drawRect(button1Rect.x, button1Rect.y, (int) button1Rect.getWidth(), (int) button1Rect.getHeight());
			g2D.drawString(
					"option 1", 
					(int) (button1Rect.getCenterX() - FoxFontBuilder.getStringBounds(g2D, "option 1").getWidth() / 2D), 
					(int) (button1Rect.getCenterY() + FoxFontBuilder.getStringBounds(g2D, "option 1").getHeight() / 5D));
			
			g2D.drawRect(button2Rect.x, button2Rect.y, (int) button2Rect.getWidth(), (int) button2Rect.getHeight());
			g2D.drawString(
					"option 2", 
					(int) (button2Rect.getCenterX() - FoxFontBuilder.getStringBounds(g2D, "option 2").getWidth() / 2D), 
					(int) (button2Rect.getCenterY() + FoxFontBuilder.getStringBounds(g2D, "option 2").getHeight() / 5D));
		}
		
		g2D.dispose();
//		super.paint(g);
	}
	
	public SaveGame() {
		setSize(WIDTH, HEIGHT);
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setCursor(FoxCursor.createCursor("curGaleryCursor"));
		
		InputAction.add("save", this);
		InputAction.set("save", "close", KeyEvent.VK_ESCAPE	, 0, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {dispose();}
		});
	
		button0Rect = new Rectangle((int) (widthPercent * 4D), (int) (heightPercent * 92D), (int) (buttonsWidth * 1), (int) (heightPercent * 4D));
		button1Rect = new Rectangle((int) (widthPercent * 24.5D), (int) (heightPercent * 92D), (int) (buttonsWidth * 1), (int) (heightPercent * 4D));
		button2Rect = new Rectangle((int) (widthPercent * 45D), (int) (heightPercent * 92D), (int) (buttonsWidth * 1), (int) (heightPercent * 4D));
		
		setLocationRelativeTo(null);
		setModal(true);
		setVisible(true);
	}
}
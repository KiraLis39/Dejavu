package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import adds.FoxConsole;
import adds.IOM;
import adds.InputAction;
import adds.Out;
import adds.Out.LEVEL;
import builders.FoxFontBuilder;
import builders.ResManager;
import components.FoxTipsEngine;
import games.FoxCursor;
import games.FoxSpritesCombiner;
import media.Media;
import resourses.Registry;
import door.Exit;
import secondGUI.AutorsFrame;
import secondGUI.GalleryFrame;
import secondGUI.GenderFrame;
import secondGUI.NewUserForm;
import secondGUI.OptMenuFrame;
import secondGUI.SaveGame;


@SuppressWarnings("serial")
public class MainMenu extends JFrame implements MouseListener, MouseMotionListener, ActionListener {
	private static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	
	private static BufferedImage centerImage, picMenuImage;
	private static BufferedImage[] exitImages, startImages, menuImages;
	
	private static Font f0 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CONSTANTIA, 28, true);
	private static Font f1 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CONSOLAS, 28, true);
	
	private static Point2D mouseWasOnScreen, frameWas;
	
	private static String downText, userName;

	private static JPanel basePane;
	private static JButton optionsButton, galeryButton, saveLoadButton, playButton, aboutButton;
	private static JLabel downTextLabel;
	
	private static float wPercent, hPercent;
	private boolean fullscreen;
	
	private FoxConsole cons;
	private FoxTipsEngine cd;
	
	
	public MainMenu() {
		setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setCursor(FoxCursor.createCursor("curSimpleCursor"));
		
		preLoading();
		switchFullscreen();
		
		add(buildBasePane());
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		pack();
		setLocationRelativeTo(null);
		
		testNewbie();
		
		Out.Print(MainMenu.class, LEVEL.INFO, "MainMenu setts visible...");
		setVisible(true);
		
		setStatusText(null);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(250);
					mediaStart();
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}).start();
		
		Media.playBackg("fonKricket");
		Media.playMusic("musMainMenu", true);
		
		this.cons= new FoxConsole(this);
		Out.connectFoxConsole(this.cons);
	}
	
	private static void mediaStart() {
		Media.setBackgEnabled(!IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.BACKG_MUTE));
		Media.setMusicEnabled(!IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.MUSIC_MUTE));
		Media.setSoundEnabled(!IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.SOUND_MUTE));
		Media.setVoiceEnabled(!IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.VOICE_MUTE));
		
		Media.setSoundVolume((float) (IOM.getDouble(IOM.HEADERS.CONFIG, IOMs.CONFIG.SOUND_VOL) * 1f));
		Media.setMusicVolume((float) (IOM.getDouble(IOM.HEADERS.CONFIG, IOMs.CONFIG.MUSIC_VOL) * 1f));
		Media.setBackgVolume((float) (IOM.getDouble(IOM.HEADERS.CONFIG, IOMs.CONFIG.BACKG_VOL) * 1f));
		Media.setVoiceVolume((float) (IOM.getDouble(IOM.HEADERS.CONFIG, IOMs.CONFIG.VOICE_VOL) * 1f));
	}

	private void preLoading() {
		Out.Print(MainMenu.class, LEVEL.INFO, "MainMenu preloading...");
		
		this.fullscreen = IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.FULLSCREEN);
		
		startImages = FoxSpritesCombiner.addSpritelist("PlayButtonSprite", ResManager.getBImage("picPlayButtonSprite", true), 1, 3);
		menuImages = FoxSpritesCombiner.addSpritelist("MenuButtonSprite", ResManager.getBImage("picMenuButtonSprite", true), 1, 3);
		exitImages = FoxSpritesCombiner.addSpritelist("ExitButtonSprite", ResManager.getBImage("picExitButtonSprite", true), 1, 3);
		
		centerImage = ResManager.getBImage("picMenuBase");
		picMenuImage = ResManager.getBImage("picMenupane");

		downText = "\u266B " + userName + " \u266B";
		
		InputAction.add("MainMenu", this);
		InputAction.set("MainMenu", "Ctrl+F4", KeyEvent.VK_F4, 512, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {Exit.exit(0, "Exit by users 'Ctrl+F4'");}
		});
	}
	
	private static void testNewbie() {
		userName = IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_NAME);
		if (userName.equals("newUser") || userName.equals("none")) {
			Out.Print(MainMenu.class, LEVEL.ACCENT, "Open NewUserForm to change name by " + userName);
			new NewUserForm();
			userName = IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_NAME);
		}
		
//		Out.Print("\nДанная программа использует " + 
//				(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576 + 
//				"мб из " + Runtime.getRuntime().totalMemory() / 1048576 + 
//				"мб выделенных под неё. \nСпасибо за использование утилиты компании MultyVerse39 Group!");
	}

	private static void setStatusText(String newText) {
		if (newText == null) {downTextLabel.setText("\u266B " + userName + " \u266B");
		} else {downTextLabel.setText("\u266B " + newText + " \u266B");}
	}
	
	private void switchFullscreen() {
		Out.Print(MainMenu.class, LEVEL.INFO, "MainMenu fullscreen test...");
		
		if (IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.FULLSCREEN)) {
			setBackground(Color.BLACK);
			setPreferredSize(new Dimension((int) (screen.getWidth()), (int) (screen.getHeight())));
			setSize(new Dimension((int) (screen.getWidth()), (int) (screen.getHeight())));
		} else {
			setBackground(new Color(0.0f, 0.0f, 0.0f, 0.5f));
			setPreferredSize(new Dimension((int) (screen.width * 0.75f), (int) (screen.height * 0.75f)));
			setSize(new Dimension((int) (screen.width * 0.75f), (int) (screen.height * 0.75f)));
		}
		setLocationRelativeTo(null);
		
		wPercent = getWidth() / 100f;
		hPercent = getHeight() / 100f;
		
		if (fullscreen != IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.FULLSCREEN)) {
			fullscreen = IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.FULLSCREEN);
			
			if (basePane != null) {
				MainMenu.this.remove(basePane);
				basePane = null;
				
				MainMenu.this.add(buildBasePane());
			}
		}
	}
	
	
	private JComponent buildBasePane() {
		basePane = new JPanel(new BorderLayout((int) (wPercent * 2.6f), (int) (hPercent * 2.0f))) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2D = (Graphics2D) g;
				Registry.render(g2D, false);
				g2D.drawImage(picMenuImage, 0, 0, getWidth(), getHeight(), this);
			}
			
			{
				setBorder(new EmptyBorder((int) (hPercent * 3f), (int) (wPercent * 2f), (int) (wPercent * 1.6f), (int) (hPercent * 4.2f)));
				
				JPanel upPlayPane = new JPanel(new BorderLayout()) {
					{
						setOpaque(false);
						
						JButton playButton = new JButton("play game") {
							BufferedImage bImage = startImages[0];
							
							@Override
							public void paintComponent(Graphics g) {
								if (startImages != null) {
									Graphics2D g2D = (Graphics2D) g;
									Registry.render(g2D, false);
									
									g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), null, null);
									g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
//									g2D.dispose();
								} else {super.paintComponent(g);}
							};
							
							{
								setName("playButton");
								setPreferredSize(new Dimension(0, (int) (hPercent * 6.5f)));
								setFont(f0);
								setForeground(Color.BLACK);
								setBorderPainted(false);
								setFocusPainted(false);
								setOpaque(false);
								
								setActionCommand("play");
								addActionListener(MainMenu.this);
								addMouseListener(new MouseAdapter() {
							         public void mouseEntered(MouseEvent me) {
							        	 setStatusText("Начать/продолжить игру");
							        	 bImage = startImages[1];
							        	 repaint();
							         }
							         public void mouseExited(MouseEvent me) {
							        	 setStatusText(null);
							        	 bImage = startImages[0];
							        	 repaint();
							         }
							         public void mousePressed(MouseEvent me) {
							        	 bImage = startImages[2];
							        	 repaint();
							         }
							         public void mouseReleased(MouseEvent me) {
							        	 bImage = startImages[0];
							        	 repaint();
							         }
							      });
							}
						};
						
						add(playButton);
					}
				};

				JPanel rightButPane = new JPanel(new GridLayout(10, 0, 3, 3)) {
					{
						setOpaque(false);
//						setBorder(new EmptyBorder(0, 0, 0, 6));
						
						optionsButton = new JButton("game options") {
							BufferedImage bImage = menuImages[0];
							
							@Override
							public void paintComponent(Graphics g) {
								if (menuImages != null) {
									Graphics2D g2D = (Graphics2D) g;
									Registry.render(g2D, false);
									
									g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
									g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
//									g2D.dispose();
								} else {super.paintComponent(g);}
							};
							
							{
								setName("optionsButton");
								setPreferredSize(new Dimension((int) (wPercent * 28.55f), 50));
								setFont(f0);
								setForeground(Color.BLACK);
								setBorderPainted(false);
								setFocusPainted(false);
								setOpaque(false);
								
								setActionCommand("options");
								addActionListener(MainMenu.this);
								addMouseListener(new MouseAdapter() {
							         public void mouseEntered(MouseEvent me) {
							        	 setStatusText("Настройки игры");
							        	 bImage = menuImages[1];
							        	 repaint();
							         }
							         public void mouseExited(MouseEvent me) {
							        	 setStatusText(null);
							        	 bImage = menuImages[0];
							        	 repaint();
							         }
							         public void mousePressed(MouseEvent me) {
							        	 bImage = menuImages[2];
							        	 repaint();
							         }
							         public void mouseReleased(MouseEvent me) {
							        	 bImage = menuImages[0];
							        	 repaint();
							         }
							      });
							}
						};
						
						saveLoadButton = new JButton("save/load") {
							BufferedImage bImage = menuImages[0];
							
							@Override
							public void paintComponent(Graphics g) {
								if (menuImages != null) {
									Graphics2D g2D = (Graphics2D) g;
									Registry.render(g2D, false);
									
									g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
									g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
//									g2D.dispose();
								} else {super.paintComponent(g);}
							};
							
							{
								setName("saveLoadButton");
//								setPreferredSize(new Dimension(410, 50));
								setFont(f0);
								setForeground(Color.BLACK);
								setBorderPainted(false);
								setFocusPainted(false);
								setOpaque(false);
								
								setActionCommand("saveLoad");
								addActionListener(MainMenu.this);
								addMouseListener(new MouseAdapter() {
							         public void mouseEntered(MouseEvent me) {
							        	 setStatusText("Сохранить и загрузить");
							        	 bImage = menuImages[1];
							        	 repaint();
							         }
							         public void mouseExited(MouseEvent me) {
							        	 setStatusText(null);
							        	 bImage = menuImages[0];
							        	 repaint();
							         }
							         public void mousePressed(MouseEvent me) {
							        	 bImage = menuImages[2];
							        	 repaint();
							         }
							         public void mouseReleased(MouseEvent me) {
							        	 bImage = menuImages[0];
							        	 repaint();
							         }
							      });
							}
						};
						
						galeryButton = new JButton("galery") {
							BufferedImage bImage = menuImages[0];
							
							@Override
							public void paintComponent(Graphics g) {
								if (menuImages != null) {
									Graphics2D g2D = (Graphics2D) g;
									Registry.render(g2D, false);
									
									g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
									g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
//									g2D.dispose();
								} else {super.paintComponent(g);}
							};
							
							{
								setName("galeryButton");
//								setPreferredSize(new Dimension(410, 50));
								setFont(f0);
								setForeground(Color.BLACK);
								setBorderPainted(false);
								setFocusPainted(false);
								setOpaque(false);
								
								setActionCommand("galery");
								addActionListener(MainMenu.this);
								addMouseListener(new MouseAdapter() {
							         public void mouseEntered(MouseEvent me) {
							        	 setStatusText("Галерея воспоминаний");
							        	 bImage = menuImages[1];
							        	 repaint();
							         }
							         public void mouseExited(MouseEvent me) {
							        	 setStatusText(null);
							        	 bImage = menuImages[0];
							        	 repaint();
							         }
							         public void mousePressed(MouseEvent me) {
							        	 bImage = menuImages[2];
							        	 repaint();
							         }
							         public void mouseReleased(MouseEvent me) {
							        	 bImage = menuImages[0];
							        	 repaint();
							         }
							      });
							}
						};
						
						aboutButton = new JButton("about") {
							BufferedImage bImage = menuImages[0];
							
							@Override
							public void paintComponent(Graphics g) {
								if (menuImages != null) {
									Graphics2D g2D = (Graphics2D) g;
									Registry.render(g2D, false);
									
									g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), this);
									g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
//									g2D.dispose();
								} else {super.paintComponent(g);}
							};
							
							{
								setName("aboutButton");
//								setPreferredSize(new Dimension(410, 50));
								setFont(f0);
								setForeground(Color.BLACK);
								setBorderPainted(false);
								setFocusPainted(false);
								setOpaque(false);
								
								setActionCommand("about");
								addActionListener(MainMenu.this);
								addMouseListener(new MouseAdapter() {
							         public void mouseEntered(MouseEvent me) {
							        	 setStatusText("Об игре и создателях");
							        	 bImage = menuImages[1];
							        	 repaint();
							         }
							         public void mouseExited(MouseEvent me) {
							        	 setStatusText(null);
							        	 bImage = menuImages[0];
							        	 repaint();
							         }
							         public void mousePressed(MouseEvent me) {
							        	 bImage = menuImages[2];
							        	 repaint();
							         }
							         public void mouseReleased(MouseEvent me) {
							        	 bImage = menuImages[0];
							        	 repaint();
							         }
							      });
							}
						};
						
						add(optionsButton);
						add(saveLoadButton);
						add(galeryButton);
						add(aboutButton);
					}
				};
				
				JPanel midImagePane = new JPanel() {
					@Override
					public void paintComponent(Graphics g) {
						if (centerImage != null) {
							Graphics2D g2D = (Graphics2D) g;
							Registry.render(g2D, false);
							
							g2D.drawImage(centerImage, 0, 0, getWidth(), getHeight(), this);
							
							g2D.setColor(Color.BLACK);
							g2D.drawString("v." + Registry.version, 7, 18);
							g2D.setColor(Color.ORANGE);
							g2D.drawString("v." + Registry.version, 8, 16);
//							g2D.dispose();
						} else {super.paintComponent(g);}
					}
					
					{
						
					}
				};
				
				JPanel downExitPane = new JPanel(new BorderLayout((int) (wPercent * 3f), (int) (hPercent * 2.0f))) {
					{
						setOpaque(false);

						playButton = new JButton("exit game") {
							BufferedImage bImage = exitImages[0];
							
							@Override
							public void paintComponent(Graphics g) {
								if (exitImages != null) {
									Graphics2D g2D = (Graphics2D) g;
									Registry.render(g2D, false);
									
									g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), null, null);
									g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
									g2D.dispose();
								} else {super.paintComponent(g);}
							};
							
							{
								setName("exitButton");
								setPreferredSize(new Dimension((int) (wPercent * 28.55f), (int) (hPercent * 6.5f)));
								setFont(f0);
								setForeground(Color.BLACK);
								setBorderPainted(false);
								setFocusPainted(false);
								setOpaque(false);
								
								setActionCommand("exit");
								addActionListener(MainMenu.this);
								addMouseListener(new MouseAdapter() {
							         public void mouseEntered(MouseEvent me) {
							        	 setStatusText("Завершить игру и выйти");
							        	 bImage = exitImages[1];
							        	 repaint();
							         }
							         public void mouseExited(MouseEvent me) {
							        	 setStatusText(null);
							        	 bImage = exitImages[0];
							        	 repaint();
							         }
							         public void mousePressed(MouseEvent me) {
							        	 bImage = exitImages[2];
							        	 repaint();
							         }
							         public void mouseReleased(MouseEvent me) {
							        	 bImage = exitImages[0];
							        	 repaint();
							         }
							      });
							}
						};
						
						try {cd = new FoxTipsEngine(this, FoxTipsEngine.TYPE.INFO, ImageIO.read(new File("./resources/tipIco.png")), 
								"Заголовок подсказки:", 
								"Это текст сообщения. Его необходимо правильно<br>переносить на следующую строку и вообще...<br>всё в таком духе. Вот.",
								"Тем более, если сообщение окажется черезчур длинным."
								);									
						} catch (Exception e) {e.printStackTrace();}
						
						downTextLabel = new JLabel() {
							{
//								setBorder(new EmptyBorder(0, 0, 0, 128));
								setText(downText);
								setFont(f1);
								setForeground(Color.WHITE);
								setHorizontalAlignment(0);
								
								addMouseListener(new MouseAdapter() {
							         public void mouseEntered(MouseEvent me) {
							        	 setForeground(Color.ORANGE);
							        	 setStatusText("Выбрать другого игрока (2x click)");
							        	 cd.show();
							         }
							         public void mouseExited(MouseEvent me) {
							        	 setForeground(Color.WHITE);
							        	 setStatusText(null);
//							        	 cd.close();
							         }
							         public void mousePressed(MouseEvent me) {
							        	 if (me.getClickCount() >= 2) {new NewUserForm();}
							         }
							      });
							}
						};
						
						add(playButton, BorderLayout.EAST);
						add(downTextLabel, BorderLayout.CENTER);
					}
				};
				
				add(upPlayPane, BorderLayout.NORTH);
				add(rightButPane, BorderLayout.EAST);
				add(midImagePane, BorderLayout.CENTER);
				add(downExitPane, BorderLayout.SOUTH);
			}
		};
		
		return basePane;
	}
	

	@Override
	public void mouseDragged(MouseEvent e) {
		if (fullscreen) {return;}
		
		setLocation(
				(int) (frameWas.getX() - (mouseWasOnScreen.getX() - e.getXOnScreen())), 
				(int) (frameWas.getY() - (mouseWasOnScreen.getY() - e.getYOnScreen())));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseWasOnScreen = new Point(e.getXOnScreen(), e.getYOnScreen());
		frameWas = MainMenu.this.getLocation();
	}

	public void mouseMoved(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}	
	public void mouseEntered(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "play":
				dispose();
				if (IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.AVATAR_INDEX) == -1) {new GenderFrame();
				} else {new GameFrame();}
				break;
		
			case "exit":
				int exit = JOptionPane.showConfirmDialog(null,	"Точно закрыть игру и выйти?", "Подтверждение:", JOptionPane.YES_NO_OPTION);
				if (exit == 0) {Exit.exit(0);}
				break;
				
			case "galery":
				new GalleryFrame(MainMenu.this);
				break;
				
			case "saveLoad":
				new SaveGame();
				break;
				
			case "options":
				new OptMenuFrame();
				switchFullscreen();
				break;
				
			case "about":
				new AutorsFrame();
				break;
				
			default:
		}
	}
}
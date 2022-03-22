package secondGUI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import adds.IOM;
import adds.Out;
import adds.Out.LEVEL;
import builders.FoxFontBuilder;
import builders.ResManager;
import games.FoxCursor;
import registry.Registry;
import door.MainClass;


@SuppressWarnings("serial")
public class NewUserForm extends JDialog {
	private JTextField nameField, ageField;

	private int WIDTH = 450, HEIGHT = 260;
	private JCheckBox maleBox, femaBox;
	
	
	@Override
  	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setColor(Color.DARK_GRAY);		
		g2D.fillRoundRect(0, 0, WIDTH, HEIGHT, 30, 30);
		
		g2D.setStroke(new BasicStroke(1.2f));
		g2D.setColor(Color.BLACK);
		g2D.drawRoundRect(4, 6, WIDTH - 11, HEIGHT - 9, 25, 25);
		g2D.setColor(Color.GRAY);
		g2D.drawRoundRect(5, 5, WIDTH - 10, HEIGHT - 10, 25, 25);
		
		g2D.setFont(Registry.f0);
		g2D.setColor(Color.BLACK);
		g2D.drawString("Настройка персонажа:", (int) (WIDTH / 2 - FoxFontBuilder.getStringBounds(g2D, "Настройка персонажа:").getWidth() / 2D) - 1, 41);
		g2D.setColor(Color.ORANGE);
		g2D.drawString("Настройка персонажа:", (int) (WIDTH / 2 - FoxFontBuilder.getStringBounds(g2D, "Настройка персонажа:").getWidth() / 2D), 40);
		
		// name field:
//		if (isNameFieldOver) {g2D.setColor(Color.GRAY.brighter());} else {g2D.setColor(Color.GRAY);}
//		g2D.drawRoundRect(nameRect.x, nameRect.y, nameRect.width, nameRect.height, 5, 5);
//		if (isAgeFieldOver) {g2D.setColor(Color.GRAY.brighter());} else {g2D.setColor(Color.GRAY);}
//		g2D.drawRoundRect(ageRect.x, ageRect.y, ageRect.width, ageRect.height, 5, 5);

		g2D.setFont(Registry.f0);
		if (nameField.hasFocus()) {g2D.setColor(Color.GREEN);} else {g2D.setColor(Color.WHITE);}
//		g2D.drawString(nameField.getText(), (int) (nameRect.getCenterX() - Registry.ffb.getStringBounds(g2D, nameField.getText()).getWidth() / 2D), (int) nameRect.getCenterY() + 6);
		if (ageField.hasFocus()) {g2D.setColor(Color.GREEN);} else {g2D.setColor(Color.WHITE);}
//		g2D.drawString(ageField.getText(), (int) (ageRect.getCenterX() - Registry.ffb.getStringBounds(g2D, ageField.getText()).getWidth() / 2D), (int) ageRect.getCenterY() + 6);
		
		// sex choise:
//		GradientPaint gp = new GradientPaint(0, 0, Color.BLUE.darker(), (float) (nameRect.getWidth()), (float) nameRect.getHeight(), Color.RED.darker());
//		g2D.setPaint(gp);
//		g2D.fillRoundRect(nameRect.x, nameRect.y + nameRect.height + 20, nameRect.width + ageRect.width + 10, nameRect.height * 2, 5, 5);
		
		g2D.setColor(Color.GRAY);
//		g2D.drawRoundRect(nameRect.x, nameRect.y + nameRect.height + 20, nameRect.width + ageRect.width + 10, nameRect.height * 2, 5, 5);
		
//		if (isMaleSexPressed) {g2D.setColor(Color.GREEN);} else {g2D.setColor(Color.WHITE);}
//		g2D.drawRoundRect(maleSexChoiserRect.x, maleSexChoiserRect.y, maleSexChoiserRect.width, maleSexChoiserRect.height, 3, 3);
//		if (isFemaSexPressed) {g2D.setColor(Color.GREEN);} else {g2D.setColor(Color.WHITE);}
//		g2D.drawRoundRect(femaSexChoiserRect.x, femaSexChoiserRect.y, femaSexChoiserRect.width, femaSexChoiserRect.height, 3, 3);
		
		g2D.setColor(Color.GREEN);
//		if (isMaleSexPressed) {
//			g2D.fillPolygon(
//					new Polygon(
//							new int[] {maleSexChoiserRect.x, maleSexChoiserRect.x + 10, maleSexChoiserRect.x + 15, maleSexChoiserRect.x + 7}, 
//							new int[] {maleSexChoiserRect.y + 3, maleSexChoiserRect.y + 13, maleSexChoiserRect.y - 9, maleSexChoiserRect.y + 8}, 
//							4));
//		} else {
//			g2D.fillPolygon(
//					new Polygon(
//							new int[] {femaSexChoiserRect.x, femaSexChoiserRect.x + 10, femaSexChoiserRect.x + 15, femaSexChoiserRect.x + 7}, 
//							new int[] {femaSexChoiserRect.y + 3, femaSexChoiserRect.y + 13, femaSexChoiserRect.y - 9, femaSexChoiserRect.y + 8}, 
//							4));
//		}

		g2D.setColor(Color.WHITE);
//		g2D.drawString("Парень:", (int) (maleSexChoiserRect.getCenterX() - Registry.ffb.getStringBounds(g2D, "Парень:").getWidth() / 2D), maleSexChoiserRect.y - 10);
//		g2D.drawString("Девушка:", (int) (femaSexChoiserRect.getCenterX() - Registry.ffb.getStringBounds(g2D, "Девушка:").getWidth() / 2D), femaSexChoiserRect.y - 10);
		
		// ok button:
//		if (isOkButtonOver) {g2D.setColor(Color.WHITE);} else {g2D.setColor(Color.GRAY);}		
//		g2D.drawRoundRect(okButtonRect.x, okButtonRect.y, okButtonRect.width, okButtonRect.height, 10, 10);
//		if (isOkButtonPressed) {g2D.setColor(Color.GREEN);} else {g2D.setColor(Color.WHITE);}
//		g2D.drawString("Готово", (int) (okButtonRect.getCenterX() - Registry.ffb.getStringBounds(g2D, "Готово").getWidth() / 2D), (int) (okButtonRect.getCenterY() + 6));
		
//		g2D.dispose();
		
		super.paintComponents(g2D);
	}

	public NewUserForm() {
		setUndecorated(true);
		setBackground(new Color(0,0,0,0));
		setPreferredSize(new Dimension(this.WIDTH, this.HEIGHT));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setCursor(FoxCursor.createCursor(ResManager.getBImage("curOtherCursor"), "nhc"));
		setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);
		setIgnoreRepaint(true);
		getRootPane().setBorder(new EmptyBorder(45, 12, 15, 12));
		getContentPane().setLayout(new BorderLayout(6, 6));
		
		JPanel midAreasPane = new JPanel(new BorderLayout(3, 3)) {
			{
				setOpaque(false);
				setBorder(new EmptyBorder(9, 0, 6, 0));

				nameField = new JTextField() {
					{
						setFont(Registry.f0);
						setOpaque(false);
						setForeground(Color.WHITE);
						setHorizontalAlignment(CENTER);
						setText(IOM.getString(IOM.HEADERS.LAST_USER, "LUSER"));
						setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), "Имя:", 1, 0, Registry.f2, Color.BLACK), 
								new EmptyBorder(-3, 0, 3, 0)
						));
					}
				};
				
				ageField = new JTextField(6) {
					{
						setFont(Registry.f0);
						setOpaque(false);
						setForeground(Color.WHITE);
						setHorizontalAlignment(CENTER);
						setText(IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_AGE));
						setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), "Возраст:", 1, 0, Registry.f2, Color.BLACK), 
								new EmptyBorder(-3, 0, 3, 0)
						));
					}
				};
				
				JPanel midSexPane = new JPanel(new GridLayout(1, 2)) {
					{
						setOpaque(false);
						setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), "Пол:", 1, 0, Registry.f2, Color.BLACK), 
								new EmptyBorder(-3, 0, 3, 0)
						));
						
						ButtonGroup bg = new ButtonGroup();
						
						maleBox = new JCheckBox("Парень") {
							{
								setOpaque(false);
								setFont(Registry.f0);
								setHorizontalAlignment(0);
								setFocusPainted(false);
								setForeground(Color.CYAN);
								setSelected(IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_SEX).equals("male") ? true : false);
							}
						};
						femaBox = new JCheckBox("Девушка") {
							{
								setOpaque(false);
								setFont(Registry.f0);
								setHorizontalAlignment(0);
								setFocusPainted(IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_SEX).equals("male") ? false : true);
								setForeground(Color.MAGENTA.brighter());
							}
						};
						
						bg.add(maleBox);
						bg.add(femaBox);
						
						add(maleBox);
						add(femaBox);
					}
				};
				
				add(nameField);
				add(ageField, BorderLayout.EAST);
				add(midSexPane, BorderLayout.SOUTH);
			}
		};
		
		JPanel downOkPane = new JPanel(new BorderLayout()) {
			{
				setOpaque(false);
				setBorder(new EmptyBorder(0, 270, 3, 3));
				
				JButton okButton = new JButton("Готово") {
					{
						setFocusPainted(false);
						setBackground(Color.BLACK);
						setForeground(Color.ORANGE);
						setFont(Registry.f1);
						setPreferredSize(new Dimension(0, 42));
						addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								if (nameField.getText().equals("")) {
									JOptionPane.showMessageDialog(null, 
											"Не введен ник персонажа!", "Ошибка:", 
											JOptionPane.ERROR_MESSAGE);
								} else {
									if (!IOM.getString(IOM.HEADERS.LAST_USER, "LUSER").equals(nameField.getText().trim())) {
										IOM.set(IOM.HEADERS.LAST_USER, "LUSER", nameField.getText().trim());
										IOM.save(IOM.HEADERS.LAST_USER.name());
										
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_SEX, maleBox.isSelected() ? "male" : "fema");
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_AGE, ageField.getText());
										
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_ANN, 0);
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_DMI, 0);
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_KUR, 0);
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_MAR, 0);
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_MSH, 0);
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_OKS, 0);
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_OLG, 0);
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_OLE, 0);
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_LIS, 0);
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_POS, 0);
										IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.KARMA_NEG, 0);
										
										try {
											MainClass.reloadUserData(nameField.getText(), maleBox.isSelected() ? "male" : "fema", Integer.parseInt(ageField.getText()));
											Out.Print(NewUserForm.class, LEVEL.ACCENT, "Создан успешно игрок " + nameField.getText() + ", пол: " + (maleBox.isSelected() ? "male" : "fema") + ", age: " + ageField.getText());
											IOM.saveAll();
										} catch (Exception e2) {e2.printStackTrace();}
									}
									
									dispose();
								}
							}
						});
					}
				};
				
				add(okButton);
			}
		};

		add(midAreasPane, BorderLayout.CENTER);
		add(downOkPane, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(null);
		
		setModal(true);
		setVisible(true);
	}
}
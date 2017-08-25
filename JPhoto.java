import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class ImageButton extends JButton {
	private static final long serialVersionUID = 1;

	/** @serial */
	private ImageIcon image;

	/** @serial */
	private final Rectangle innerArea = new Rectangle();

	public ImageButton(ImageIcon image) {
		this.image = image;
	}

	public ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (image != null) {
			SwingUtilities.calculateInnerArea(this, innerArea);

			g.drawImage(image.getImage(), innerArea.x, innerArea.y, innerArea.width, innerArea.height, this);
		}
	}
}

public class JPhoto extends JPanel {

	private static final long serialVersionUID = 1L;
	private String title;
	private ImageIcon image;

	private JLabel titleLabel;
	private JCheckBox checkBox;
	private ImageButton imageLabel;

	public JPhoto(String title, ImageIcon image) {
		setLayout(new BorderLayout(5, 5));
		//
		// setBorder(BorderFactory.createLoweredSoftBevelBorder());

		this.image = image;
		this.imageLabel = new ImageButton(image);
		this.add(imageLabel, BorderLayout.CENTER);

		this.title = title;
		this.titleLabel = new JLabel(title);

		this.checkBox = new JCheckBox();
		this.checkBox.setText("Relevant");
		this.checkBox.setVisible(false);
		/*
		 * Font f = this.titleLabel.getFont(); f = f.deriveFont(Font.BOLD); f =
		 * f.deriveFont(18f); this.titleLabel.setFont(f);
		 */
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(titleLabel, BorderLayout.WEST);
		northPanel.add(checkBox, BorderLayout.CENTER);
		this.add(northPanel, BorderLayout.SOUTH);

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		repaint();
	}

	public ImageIcon getImage() {
		return image;
	}

	public JButton getImageButton() {
		return imageLabel;
	}

	public JCheckBox getCheckBox() {
		return this.checkBox;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
		repaint();
	}
}
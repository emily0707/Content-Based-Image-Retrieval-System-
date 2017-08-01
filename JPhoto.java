import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JPhoto extends JPanel {

	private static final long serialVersionUID = 1L;
	private String title;
	private ImageIcon image;

	private JLabel titleLabel;
	private JButton imageLabel;

	public JPhoto(String title, ImageIcon image) {
		setLayout(new BorderLayout(5, 5));
		//
		// setBorder(BorderFactory.createLoweredSoftBevelBorder());

		this.image = image;
		this.imageLabel = new JButton(image);
		this.add(imageLabel, BorderLayout.CENTER);

		this.title = title;
		this.titleLabel = new JLabel(title);
		/*
		 * Font f = this.titleLabel.getFont(); f = f.deriveFont(Font.BOLD); f =
		 * f.deriveFont(18f); this.titleLabel.setFont(f);
		 */
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(titleLabel, BorderLayout.CENTER);
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

	public void setImage(ImageIcon image) {
		this.image = image;
		repaint();
	}
}
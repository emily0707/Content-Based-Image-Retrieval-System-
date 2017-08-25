
/* Project 1
*/

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import javafx.util.Pair;


public class CBIR extends JFrame {

	private static final long serialVersionUID = 1L;
	private JLabel photographLabel = new JLabel(); // container to hold a large text
	private int[] buttonOrder = new int[101]; // creates an array to keep up with the image order
	private GridLayout gridLayout1;
	private GridLayout gridLayout2;
	private GridLayout gridLayout3;
	private JPanel panelBottom1;
	private JPanel panelTop;
	private JPanel buttonPanel;
	//holds the results of the query of each picture based on intensity method. 
	private Integer[][] intensityIndexes = new Integer[100][100]; 
	//holds the results of the query of each picture based on color code method. 
	private Integer[][] colorCodeIndexes = new Integer[100][100];
	private Double[][] intensityMatrix = new Double[100][25];
	private Double[][] colorCodeMatrix = new Double[100][64];
	private JPhoto[] photo; //creates an array of JPhotos

	private ArrayList<RelevantData> imageRelevance = new ArrayList<RelevantData>();

	int picNo = 0;
	int imageCount = 1; // keeps up with the number of images displayed since the first page.
	int pageNo = 1;


	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				CBIR app = new CBIR();
				app.setVisible(true);
			}
		});
	}

	public CBIR() {
		// The following lines set up the interface including the layout of the
		// buttons and JPanels.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Icon Demo: Please Select an Image");
		panelBottom1 = new JPanel();
		panelTop = new JPanel();
		buttonPanel = new JPanel();

		gridLayout1 = new GridLayout(4, 5, 5, 5);
		gridLayout2 = new GridLayout(2, 1, 5, 5);
		gridLayout3 = new GridLayout(1, 2, 5, 5);

		GridBagLayout gbl4 = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		setLayout(gridLayout2);
		panelBottom1.setLayout(gridLayout1);
		panelTop.setLayout(gridLayout3);
		add(panelTop);

		add(panelBottom1);
		photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
		photographLabel.setHorizontalTextPosition(JLabel.CENTER);
		photographLabel.setHorizontalAlignment(JLabel.CENTER);
		photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.setLayout(gbl4);
		panelTop.add(photographLabel);

		panelTop.add(buttonPanel);
		JButton previousPage = new JButton("Previous Page");
		JButton nextPage = new JButton("Next Page");
		JButton reset = new JButton("Reset");
		JButton intensity = new JButton("Intensity");
		JButton colorCode = new JButton("Color Code");
		JButton colorCodeIntensity = new JButton("Color Code and Intensity");
		JButton preprocess = new JButton("PreProcess");
		JCheckBox relevantCheckBox = new JCheckBox("Relevant");

		gbc.gridx = 0;
		gbc.gridy = 0;
		buttonPanel.add(intensity, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		buttonPanel.add(colorCode, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		buttonPanel.add(colorCodeIntensity, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		buttonPanel.add(relevantCheckBox, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		buttonPanel.add(preprocess, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		buttonPanel.add(reset, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		buttonPanel.add(previousPage, gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		buttonPanel.add(nextPage, gbc);



		nextPage.addActionListener(new nextPageHandler());
		previousPage.addActionListener(new previousPageHandler());
		intensity.addActionListener(new intensityHandler());
		colorCode.addActionListener(new colorCodeHandler());
		reset.addActionListener(new resetButtonHandler());
		preprocess.addActionListener(new preprocessButtonHandler());
		colorCodeIntensity.addActionListener(new colorCodeAndIntensityHandler());
		relevantCheckBox.addActionListener(new RelevantCheckButtonHandler());
		setSize(1100, 750);
		// this centers the frame on the screen
		setLocationRelativeTo(null);


		photo = new JPhoto[101];
		/*
		 * This for loop goes through the images in the database and stores them
		 * as icons and adds the images to JPhoto and then to the JPhoto array
		 */
		for (int i = 1; i < 101; i++) {
			ImageIcon icon;
			icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
			if (icon != null) {
				photo[i] = new JPhoto(i + ".jpg", icon);
				photo[i].getImageButton().addActionListener(new IconButtonHandler(i, icon));
				photo[i].getCheckBox().addActionListener(new ImageRelevantCheckButtonHandler(i));
				buttonOrder[i] = i;
			}
		}

		if (null == getClass().getResource("images/intensity.txt")) {
			DoPreprocess();
		} else {
			this.readColorCodeFile();
			this.readIntensityFile();
			// readIntensityDistanceFile();
			// readcolorCodeDistanceFile();
		}

		for (int i = 0; i < 100; ++i) {
			RelevantData r = new RelevantData((i + 1) + ".jpg");

			r.AddCodeCodeFeatures(this.colorCodeMatrix[i]);
			r.AddIntensityFeatures(this.intensityMatrix[i]);
			this.imageRelevance.add(r);
		}

		RelevantData.CalcNormalizedFeatures(this.imageRelevance);
		displayFirstPage();
	}

	/*
	 * This method opens the intensity-distances text file containing the results of query 
	 * of each picture based on intensity method.  The contents of this file are processed line by line and stored in a 
	 * two dimensional array called intensityIndexes. 
	 */
	public void readIntensityDistanceFile() {
		Scanner read;
		String line = "";
		int lineNumber = 0;
		read = new Scanner(getClass().getResourceAsStream("images/intensity-distances.txt"));
		while (read.hasNextLine()) {
			line = read.nextLine();
			String[] tokens = line.split(",");
			for (int i = 0; i < tokens.length; ++i) {
				intensityIndexes[lineNumber][i] = Integer.parseInt(tokens[i]);
			}
			lineNumber++;
		}
		read.close();
	}
	
	   /*
     * This method opens the intensity-distances text file containing the results of query 
     * of each picture based on color code method.  The contents of this file are processed
     *  line by line and stored in a two dimensional array called colorCodeIndexes. 
     */
	public void readcolorCodeDistanceFile() {
		Scanner read;
		String line = "";
		int lineNumber = 0;
		read = new Scanner(getClass().getResourceAsStream("images/colorcode-distances.txt"));
		while (read.hasNextLine()) {
			line = read.nextLine();
			String[] tokens = line.split(",");
			for (int i = 0; i < tokens.length; ++i) {
				colorCodeIndexes[lineNumber][i] = Integer.parseInt(tokens[i]);
			}
			lineNumber++;
		}
		read.close();
	}

	/*
	 * This method opens the intensity text file containing the intensity matrix
	 * with the histogram bin values for each image. The contents of the matrix
	 * are processed and stored in a two dimensional array called
	 * intensityMatrix.
	 */
	public void readIntensityFile() {
		Scanner read;
		String line = "";
		int lineNumber = 0;
		read = new Scanner(getClass().getResourceAsStream("images/intensity.txt"));
		while (read.hasNextLine()) {
			line = read.nextLine();
			String[] tokens = line.split(",");
			for (int i = 0; i < tokens.length; ++i) {
				intensityMatrix[lineNumber][i] = Double.parseDouble(tokens[i]);
			}
			lineNumber++;
		}
		read.close();
	}

	/*
	 * This method opens the color code text file containing the color code
	 * matrix with the histogram bin values for each image. The contents of the
	 * matrix are processed and stored in a two dimensional array called
	 * colorCodeMatrix.
	 */
	private void readColorCodeFile() {
		Scanner read;
		String line = "";
		int lineNumber = 0;
		read = new Scanner(getClass().getResourceAsStream("images/ColorCodes.txt"));
		while (read.hasNextLine()) {
			line = read.nextLine();
			String[] tokens = line.split(",");
			for (int i = 0; i < tokens.length; ++i) {
				this.colorCodeMatrix[lineNumber][i] = Double.parseDouble(tokens[i]);
			}

			lineNumber++;
		}
		read.close();
	}

	/*
	 * This method displays the first twenty images in the panelBottom. The for
	 * loop starts at number one and gets the image number stored in the
	 * buttonOrder array and assigns the value to imageButNo. The button
	 * associated with the image is then added to panelBottom1. The for loop
	 * continues this process until twenty images are displayed in the
	 * panelBottom1
	 */
	private void displayFirstPage() {
		int imageButNo = 0;
		panelBottom1.removeAll();
		for (int i = 1; i < 21; i++) {
			imageButNo = buttonOrder[i];
			panelBottom1.add(photo[imageButNo]);
			imageCount++;
		}
		panelBottom1.revalidate();
		panelBottom1.repaint();

	}
	

    /*
     * This method calculate the intensity value of each pixels of a picture. and then 
     * increment counts of related bins array intensity correspondingly.
     */
    private int[] calculateIntensity(BufferedImage input) {
        int[] intensity = new int[25];
        int width = input.getWidth();
        int height = input.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color = new Color(input.getRGB(i, j));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                double I = 0.299 * red + 0.587 * green + 0.114 * blue;
                int bins = (int) (I / 10);
                if (bins >= intensity.length) {
                    bins = intensity.length - 1;
                }
                intensity[bins]++;
            }
        }
        return intensity;
    }
    
    
    /*
     * This method calculate the color code  value of each pixels of a picture. and then 
     * increment counts of related bins array colorCode correspondingly.
     */
	private int[] calculateColorCode(BufferedImage input) {
		int[] colorCode = new int[64];
		int width = input.getWidth();
		int height = input.getHeight();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Color color = new Color(input.getRGB(i, j));
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();
				int redByte = (red & (3 << 6)) >> 6; // get the the most significant 2 bits of read.
				int greenByte = (green & (3 << 6)) >> 6;
				int blueByte = (blue & (3 << 6)) >> 6;
				//get the 6-bit color value. 
				int code = blueByte | (greenByte << 2) | (redByte << 4);
				if (code > colorCode.length) {
					code = code % colorCode.length;
				}
				colorCode[code]++;
			}
		}
		return colorCode;
	}


	/*
	 * This class implements an ActionListener for each iconButton. When an icon
	 * button is clicked, the image on the button is added to the
	 * photographLabel and the picNo is set to the image number selected and
	 * being displayed.
	 */
	private class IconButtonHandler implements ActionListener {
		int pNo = 0;
		ImageIcon iconUsed;
		IconButtonHandler(int i, ImageIcon j) {
			pNo = i;
			iconUsed = j; // sets the icon to the one used in the button
		}
		public void actionPerformed(ActionEvent e) {
			photographLabel.setIcon(iconUsed);
			picNo = pNo;
		}

	}

	/*
	 * This class implements an ActionListener for each iconButton. When an icon
	 * button is clicked, the image on the button is added to the
	 * photographLabel and the picNo is set to the image number selected and
	 * being displayed.
	 */
	private class ImageRelevantCheckButtonHandler implements ActionListener {
		int imageIndex = 0;

		ImageRelevantCheckButtonHandler(int i) {
			imageIndex = i;
		}

		public void actionPerformed(ActionEvent e) {
			imageRelevance.get(picNo - 1).AddRelevantImage(imageRelevance.get(this.imageIndex - 1));
			imageRelevance.get(picNo - 1).CalcNormalizedWeight();
		}
	}

	/*
	 * This class implements an ActionListener for each iconButton. When an icon
	 * button is clicked, the image on the button is added to the
	 * photographLabel and the picNo is set to the image number selected and
	 * being displayed.
	 */
	private class RelevantCheckButtonHandler implements ActionListener {

		boolean selected = false;

		RelevantCheckButtonHandler() {

		}

		public void actionPerformed(ActionEvent e) {

			for (int i = 1; i < 101; ++i) {
				photo[i].getCheckBox().setSelected(false);
				photo[i].getCheckBox().setVisible(!selected);
			}
			selected = !selected;
			photo[picNo].getCheckBox().setSelected(true);
			displayFirstPage();
		}
	}

	/*
	 * This class implements an ActionListener for the nextPageButton. The last
	 * image number to be displayed is set to the current image count plus 20.
	 * If the endImage number equals 101, then the next page button does not
	 * display any new images because there are only 100 images to be displayed.
	 * The first picture on the next page is the image located in the
	 * buttonOrder array at the imageCount
	 */
	private class nextPageHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int imageButNo = 0;
			int endImage = imageCount + 20;
			if (endImage <= 101) {
				panelBottom1.removeAll();
				for (int i = imageCount; i < endImage; i++) {
					imageButNo = buttonOrder[i];
					panelBottom1.add(photo[imageButNo]);
					imageCount++;

				}
				panelBottom1.revalidate();
				panelBottom1.repaint();
			}
		}

	}

	
	/*
	 * This class implements an ActionListener for the previousPageButton. The
	 * last image number to be displayed is set to the current image count minus
	 * 40. If the endImage number is less than 1, then the previous page button
	 * does not display any new images because the starting image is 1. The
	 * first picture on the next page is the image located in the buttonOrder
	 * array at the imageCount
	 */
	private class previousPageHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int imageButNo = 0;
			int startImage = imageCount - 40;
			int endImage = imageCount - 20;
			if (startImage >= 1) {
				panelBottom1.removeAll();
				/*
				 * The for loop goes through the buttonOrder array starting with
				 * the startImage value and retrieves the image at that place
				 * and then adds the button to the panelBottom1.
				 */
				for (int i = startImage; i < endImage; i++) {
					imageButNo = buttonOrder[i];
					panelBottom1.add(photo[imageButNo]);
					imageCount--;
				}
				panelBottom1.revalidate();
				panelBottom1.repaint();
			}
		}
	}
	

    //This class read write the intensity data of each pictures into a text file named intensity.txt. 
	private void generateIntensityFile() throws IOException {
		String workingdirectory = System.getProperty("user.dir");
	        
		File file = new File(workingdirectory + "/intensity.txt");
		if (!file.exists()) {
	            file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		//loop for go though the 100 pictures 
		for (int iFile = 1; iFile < 101; ++iFile) {
			BufferedImage img;
			img = ImageIO.read(getClass().getResourceAsStream("images/" + iFile + ".jpg"));
            //store intensity data in an array
			int[] intensity = calculateIntensity(img);
			//loop for write intensity data of each picture to txt file.
			for (int i = 0; i < intensity.length; ++i) {
				this.intensityMatrix[iFile - 1][i] = Double.valueOf(intensity[i]);
				bw.write(Integer.toString(intensity[i]));
				bw.write(",");
			}
			bw.newLine();
			bw.flush();
		}
		bw.close();
	}

    //This class read write the color code  data of each pictures into a text file 
	//named ColorCodes.txt. 
	private void generateColorCodeFile() throws IOException {
	    
	    String workingdirectory = System.getProperty("user.dir");
	    
		File file = new File(workingdirectory + "/ColorCodes.txt");
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
	     //loop for go though the 100 pictures 
		for (int iFile = 1; iFile < 101; ++iFile) {
			BufferedImage img;
			img = ImageIO.read(getClass().getResourceAsStream("images/" + iFile + ".jpg"));//new File("bin/images//" + iFile + ".jpg"));
	         //store color code data in an array
			int[] colorCode = calculateColorCode(img);
            //loop for write color code data of each picture to txt file.
			for (int i = 0; i < colorCode.length; ++i) {
				this.colorCodeMatrix[iFile - 1][i] = Double.valueOf(colorCode[i]);
				bw.write(Integer.toString(colorCode[i]));
				bw.write(",");
			}
			bw.newLine();
			bw.flush();
		}
		bw.close();
	}

	
	 //This method calculate the Manhattan distances.
	Double calculateManhattanDistance(int iPixels, Double[] Hi, int kPixels, Double[] Hk) {
		Double distance = 0.0;
		int binSize = Hi.length;
		for (int j = 0; j < binSize; ++j) {
			distance += Math.abs((Hi[j] / iPixels) - (Hk[j] / kPixels));
		}
		return distance;
	}

	/*
	 * This class implements an ActionListener when the user selects the
	 * intensityHandler button. it reads indexes from intensityindexes array
	 * and display base on this sequence. The images are then arranged from most 
	 * similar to the least. 
	 */
	private class intensityHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			BufferedImage iImage = null;
			try {

				iImage = ImageIO.read(new File(getClass().getResource("images/" + picNo + ".jpg").toURI()));
			} catch (IOException | URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			int iPixels = iImage.getWidth() * iImage.getHeight();
			ArrayList<Pair<Double, Integer>> distances = new ArrayList<Pair<Double, Integer>>();

			for (int k = 0; k < 100; ++k) {
				try {
					BufferedImage kImage = ImageIO
							.read(new File(getClass().getResource("images/" + (k + 1) + ".jpg").toURI()));
					int kPixels = kImage.getHeight() * kImage.getWidth();
					Double d1 = calculateManhattanDistance(iPixels, intensityMatrix[picNo - 1], kPixels,
							intensityMatrix[k]);
					distances.add(new Pair<Double, Integer>(d1, k + 1));
				} catch (IOException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			distances.sort(new Comparator<Pair<Double, Integer>>() {
				@Override
				public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});

			for (int i = 0; i < distances.size(); ++i) {
				buttonOrder[i + 1] = distances.get(i).getValue();
			}

			imageCount = 1;
			displayFirstPage();

		
		}

	}

    /*
     * This class implements an ActionListener when the user selects the
     * intensityHandler button. it reads indexes from colorCodeIndexes array
     * and display base on this sequence. The images are then arranged from most 
     * similar to the least. 
     */
	private class colorCodeAndIntensityHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			ArrayList<Pair<Double, Integer>> distances = new ArrayList<Pair<Double, Integer>>();

			for (int k = 0; k < 100; ++k) {
				Double d1 = imageRelevance.get(picNo - 1).CalcWeightedDistance(imageRelevance.get(k));
				distances.add(new Pair<Double, Integer>(d1, k + 1));
			}

			distances.sort(new Comparator<Pair<Double, Integer>>() {
				@Override
				public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});

			for (int i = 0; i < distances.size(); ++i) {
				buttonOrder[i + 1] = distances.get(i).getValue();
			}

			imageCount = 1;
			displayFirstPage();

		}
	}

	private class colorCodeHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {			
			BufferedImage iImage = null;
			try {

				iImage = ImageIO.read(new File(getClass().getResource("images/" + picNo + ".jpg").toURI()));
			} catch (IOException | URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			int iPixels = iImage.getWidth() * iImage.getHeight();
			ArrayList<Pair<Double, Integer>> distances = new ArrayList<Pair<Double, Integer>>();

			for (int k = 0; k < 100; ++k) {
				try {
					BufferedImage kImage = ImageIO
							.read(new File(getClass().getResource("images/" + (k + 1) + ".jpg").toURI()));
					int kPixels = kImage.getHeight() * kImage.getWidth();
					Double d1 = calculateManhattanDistance(iPixels, colorCodeMatrix[picNo - 1], kPixels,
							colorCodeMatrix[k]);
					distances.add(new Pair<Double, Integer>(d1, k + 1));
				} catch (IOException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			distances.sort(new Comparator<Pair<Double, Integer>>() {
				@Override
				public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});

			for (int i = 0; i < distances.size(); ++i) {
				buttonOrder[i + 1] = distances.get(i).getValue();
			}

			imageCount = 1;
			displayFirstPage();

		}
	}

    /*
     * This class implements an ActionListener when the user selects the
     * reset button. it will set the system back to original state. 
     */
	private class resetButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < buttonOrder.length - 1; ++i) {
				buttonOrder[i + 1] = i + 1;
			}
			imageCount = 1;
			displayFirstPage();
		}
	}

    /*
     * This method recalculate every pictures in the database  and update related txt files. 
     */
	private void DoPreprocess() {
		try {
			generateColorCodeFile();
			generateIntensityFile();
			//generateIntensityDistanceFile();
			//generateColorCodeDistanceFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
    /*
     * This class implements an ActionListener when the user selects the
     * Preprocess button. it result in recalculating every pictures in the database 
     * and update related txt files. 
     */
	private class preprocessButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			DoPreprocess();
		}
	}
}

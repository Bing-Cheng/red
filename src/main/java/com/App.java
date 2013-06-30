package com;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JColorChooser;

import com.RedeyeReduction;

public class App {
	static BufferedImage imgOriginal;
	static BufferedImage imgProcessed; 
	static JPanel processedArea;
	static JPanel originalArea;
	static Container content;
	static JButton openButton;
	static JButton processRedEyesButton;
	static JButton processGreenEyesButton;
	static JButton processWhiteEyesButton;
	static JButton pickEyesButton;
	static JButton pickColorButton;
	static JButton moveButton;
	static JButton clearButton;
	static JButton saveImageButton;
	static JButton compareImageButton;
	static JLabel colorLabel;
	static Boolean pickEye;
	static Boolean pickColor;
	static Boolean moveImage;
	static JFrame f;
	static GroupLayout layout;
	static JList eyeLocList;
	static JScrollPane listPane;
	static JPanel listPanel;
	static JPanel buttonsPanel;
	static JCheckBox fromImage;
	static JCheckBox hsvHist;
	static Color defaultButtonColor;
	static DefaultListModel model;
	static MouseEvent mouseEvent;
	static MouseMotionEvent mouseMotionEvent;
	static int offsetX;
	static int offsetY;
	static int startX;
	static int startY;
	static RedeyeReduction originalImage;
	static RedeyeReduction processedImage;
	static Histogram redHist;
	static Histogram greenHist;
	static Histogram blueHist;
	static ArrayList<EyeLocation> eyeLocations;
	static Boolean locPicked;
	static EyeLocation eyeLoc;
	static Color pickedColor;
	static JPanel infoPanel;

	private void initParams() { 
		locPicked = false;
		eyeLocations = new ArrayList<EyeLocation>();
		System.out.println(eyeLocations.isEmpty());
		offsetX = 0;
		offsetY = 0;
		startX = 0;
		startY = 0;
		pickEye = false;
		pickColor = false;
		moveImage = false;
		pickedColor = Color.WHITE;

	}
	private void initUI() { 
		f = new JFrame("Redeye Reduction");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(1800, 800);
		content = f.getContentPane();
		content.setBackground(Color.white);
		openButton = new JButton("Open Image");
		pickEyesButton = new JButton("Pick Eyes");
		pickColorButton = new JButton("Pick Color");
		clearButton = new JButton("Clear List");
		moveButton = new JButton("Move Image");
		saveImageButton = new JButton("Save Image");
		compareImageButton = new JButton("Compare Image");
		defaultButtonColor = pickEyesButton.getBackground();
		processRedEyesButton = new JButton("Red Eyes");
		processGreenEyesButton = new JButton("Green Eyes");
		processWhiteEyesButton = new JButton("White Eyes");
		fromImage = new JCheckBox("Choose from image");
		hsvHist = new JCheckBox("HSV Histogram");
		pickEyesButton.setEnabled(false);
		pickColorButton.setEnabled(false);
		clearButton.setEnabled(false);
		moveButton.setEnabled(false);
		saveImageButton.setEnabled(false);
		processRedEyesButton.setEnabled(false);
		processGreenEyesButton.setEnabled(false);
		processWhiteEyesButton.setEnabled(false);
		fromImage.setEnabled(false);
		colorLabel = new JLabel();
		colorLabel.setMaximumSize(new Dimension(30,30));
		Border border = BorderFactory.createLineBorder(Color.black);
		colorLabel.setBorder(border);
		colorLabel.setBackground(Color.white);
		buttonsPanel = new JPanel();
	//	buttonsPanel.setSize(800,100);
		buttonsPanel.setBackground(Color.white);
		GridLayout bLayout = new GridLayout(3,4,50,5);
		buttonsPanel.setLayout(bLayout);
		buttonsPanel.add(openButton);
		buttonsPanel.add(moveButton);
		buttonsPanel.add(processRedEyesButton);
		buttonsPanel.add(pickColorButton);
		buttonsPanel.add(compareImageButton);
		buttonsPanel.add(pickEyesButton);
		buttonsPanel.add(processGreenEyesButton);
		buttonsPanel.add(colorLabel);
		buttonsPanel.add(saveImageButton);
		buttonsPanel.add(clearButton);
		buttonsPanel.add(processWhiteEyesButton);
		buttonsPanel.add(fromImage);
		redHist = new Histogram(Color.RED);
		greenHist = new Histogram(Color.GREEN);
		blueHist = new Histogram(Color.BLUE);
		redHist.setBackground(Color.WHITE);
		redHist.setBorder(border);
		redHist.setPreferredSize(new Dimension(128, 80));
		//greenHist.setPreferredSize(new Dimension(128, 100));
		//blueHist.setPreferredSize(new Dimension(128, 100));
		infoPanel = new JPanel();
		originalArea = new JPanel();
		originalArea.setPreferredSize(new Dimension(800, 600));
		processedArea = new JPanel();
		processedArea.setPreferredSize(new Dimension(800, 600));
		model = new DefaultListModel();
		eyeLocList = new JList(model);
		eyeLocList.setVisibleRowCount(6);
		listPane = new JScrollPane(eyeLocList);
		listPanel = new JPanel();
		listPanel.setBackground(Color.white);
		Border listPanelBorder = BorderFactory.createTitledBorder("Eye Locations");
		//listPanel.setBorder(listPanelBorder);
		listPanel.add(listPane);
		layout = new GroupLayout(content);
		content.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		GridLayout iLayout = new GridLayout(0,4);
		infoPanel.setLayout(new FlowLayout(FlowLayout.LEADING,2,0));
		infoPanel.setBackground(Color.WHITE);
		infoPanel.add(listPane);
		infoPanel.add(redHist);
		infoPanel.add(greenHist);
		infoPanel.add(blueHist);
		infoPanel.add(hsvHist);
	}

	public class MouseMotionEvent implements MouseMotionListener {

		public void mouseDragged(java.awt.event.MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("drag X = " + (e.getX()-offsetX) +"   Y = " + (e.getY()-offsetY));
			if (moveImage == true) {
				int X = e.getX()- offsetX - startX;
				int Y = e.getY() - offsetY - startY;
				if (Math.abs(X)>10||Math.abs(Y)>10){
					startX = e.getX() - offsetX;
					startY = e.getY() - offsetY;
					offsetX = offsetX + X;
					offsetY = offsetY + Y;
					originalImage.setParam(offsetX,offsetY,locPicked, eyeLocations);
					originalImage.repaint();
					processedImage.setParam(offsetX,offsetY,false, eyeLocations);
					processedImage.repaint();
				}
			}else if (pickEye == true) {
				int xMin = ((e.getX()-offsetX) > startX) ? startX : e.getX() - offsetX;
				int width = Math.abs(startX - e.getX() + offsetX);
				int yMin = ((e.getY()-offsetY) > startY) ? startY : e.getY() - offsetY;
				int height = Math.abs(startY - e.getY() + offsetY);
				eyeLoc.set(xMin,yMin,width,height);
				originalImage.setParam(offsetX,offsetY,locPicked, eyeLocations);
				originalImage.repaint();
			}//pickEye
		}

		public void mouseMoved(java.awt.event.MouseEvent e) {
			//System.out.println("move X = " + (e.getX()-offsetX) +"   Y = " + (e.getY()-offsetY));
		}

	}
	public class MouseEvent implements MouseListener {

		public void mouseClicked(java.awt.event.MouseEvent e) {
			System.out.println("mouseClicked");
			if (pickColor == true && fromImage.isSelected()) {
				int rgb = imgOriginal.getRGB(e.getX() - offsetX, e.getY() - offsetY);
				Color bg = new Color(rgb);
				colorLabel.setBackground(bg);
				pickedColor = bg;
				System.out.println("r="+bg.getRed()+" g="+bg.getGreen()+" b="+bg.getBlue());
				float[] hsv = RGB2HSV(bg.getRed(),bg.getGreen(),bg.getBlue());
				System.out.println("h="+hsv[0]+" s="+hsv[1]+" v="+hsv[2]);
			}
		}

		public void mousePressed(java.awt.event.MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("mousePressed");
			startX = e.getX() - offsetX;
			startY = e.getY() - offsetY;
			if (pickEye){
				eyeLoc = new EyeLocation(startX,startY,0,0);
				eyeLocations.add(eyeLoc);
				locPicked = true;
			}
		}

		public void mouseReleased(java.awt.event.MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("mouseReleased");
			if (pickEye){
				model.addElement("x = " + eyeLoc.x + ";  y = " + eyeLoc.y + ";  w = " + eyeLoc.width + ";  h = " + eyeLoc.height);
				if (eyeLoc.width>0 && eyeLoc.height >0){
					int scale = 1000;
					if (hsvHist.isSelected()){
					int[] hHist = new int[128];
					int[] sHist = new int[128];
					int[] vHist = new int[128];

					for(int i = eyeLoc.x; i < eyeLoc.x + eyeLoc.width; i++){
						for(int j = eyeLoc.y; j < eyeLoc.y + eyeLoc.height; j++){
							int intColor = imgOriginal.getRGB(i, j);
							int b = intColor & 0x000000FF;
							int g = (intColor & 0x0000FF00) >> 8;
							int r = (intColor & 0x00FF0000) >> 16;
							float[] hsv = RGB2HSV(r,g,b);
							int h = Math.round(hsv[0]*127);
							int s = Math.round(hsv[1]*127);
							int v = Math.round(hsv[2]*127);
							hHist[h]++;
							sHist[s]++;
							vHist[v]++;
						}
					}
					int[] HistPointsX = new int[128];
					for(int k = 0; k <128; k++)
					{
						HistPointsX[k] = k;
						hHist[k] = scale * hHist[k]/(eyeLoc.width*eyeLoc.height);
						sHist[k] = scale * sHist[k]/(eyeLoc.width*eyeLoc.height);
						vHist[k] = scale * vHist[k]/(eyeLoc.width*eyeLoc.height);

					}
					redHist.drawHist(HistPointsX,hHist);
					greenHist.drawHist(HistPointsX,sHist);
					blueHist.drawHist(HistPointsX,vHist);
					redHist.repaint();
					greenHist.repaint();
					blueHist.repaint();
					}else{
						int[] rHist = new int[256];
						int[] gHist = new int[256];
						int[] bHist = new int[256];

						for(int i = eyeLoc.x; i < eyeLoc.x + eyeLoc.width; i++){
							for(int j = eyeLoc.y; j < eyeLoc.y + eyeLoc.height; j++){
								int intColor = imgOriginal.getRGB(i, j);
								int b = intColor & 0x000000FF;
								int g = (intColor & 0x0000FF00) >> 8;
								int r = (intColor & 0x00FF0000) >> 16;
								rHist[r]++;
								gHist[g]++;
								bHist[b]++;
							}
						}
						int[] HistPointsX = new int[128];
						int[] rHistPointsY = new int[128];
						int[] gHistPointsY = new int[128];
						int[] bHistPointsY = new int[128];
						for(int k = 0; k <128; k++)
						{
							HistPointsX[k] = k;
							rHistPointsY[k] = scale * (rHist[2*k]+rHist[2*k+1])/(eyeLoc.width*eyeLoc.height);
							gHistPointsY[k] = scale * (gHist[2*k]+gHist[2*k+1])/(eyeLoc.width*eyeLoc.height);
							bHistPointsY[k] = scale * (bHist[2*k]+bHist[2*k+1])/(eyeLoc.width*eyeLoc.height);

						}
						redHist.drawHist(HistPointsX,rHistPointsY);
						greenHist.drawHist(HistPointsX,gHistPointsY);
						blueHist.drawHist(HistPointsX,bHistPointsY);
						redHist.repaint();
						greenHist.repaint();
						blueHist.repaint();						
					}
				}//if width>0
			}//if picked eye
		}

		public void mouseEntered(java.awt.event.MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("mouseEntered");
		}

		public void mouseExited(java.awt.event.MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("mouseExited");
		}
	}

	private void addEventHandler(){
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("open pressed");
				locPicked = false;
				eyeLocations.clear();
				model.removeAllElements();
				offsetX = 0;
				offsetY = 0;
				startX = 0;
				startY = 0;
				pickEye = false;
				pickColor = false;
				moveImage = false;
				pickEyesButton.setBackground(defaultButtonColor);
				pickColorButton.setBackground(defaultButtonColor);
				moveButton.setBackground(defaultButtonColor);
				JFileChooser openFile = new JFileChooser();
				if (openFile.showOpenDialog(openButton) == JFileChooser.APPROVE_OPTION) {
					File file = openFile.getSelectedFile();
					pickEyesButton.setEnabled(true);
					pickColorButton.setEnabled(true);
					clearButton.setEnabled(true);
					moveButton.setEnabled(true);
					saveImageButton.setEnabled(false);
					processRedEyesButton.setEnabled(true);
					processGreenEyesButton.setEnabled(true);
					processWhiteEyesButton.setEnabled(true);
					fromImage.setEnabled(true);
					try {
						imgOriginal = ImageIO.read(file);
						originalImage = new RedeyeReduction(imgOriginal,offsetX,offsetY,locPicked, eyeLocations);
						mouseEvent = new MouseEvent();
						mouseMotionEvent = new MouseMotionEvent();
						originalImage.addMouseListener(mouseEvent);
						originalImage.addMouseMotionListener(mouseMotionEvent);
						originalArea.removeAll();
						originalArea.add(originalImage);
						processedImage= new RedeyeReduction(imgOriginal,offsetX,offsetY,false, eyeLocations);
						processedArea.removeAll();
						processedArea.add(processedImage);
					} catch (IOException ex) {
						System.out.println("file does not exist. " + ex.toString());
					}
					displayImages();
				}
			}
		});
		compareImageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				locPicked = false;
				eyeLocations.clear();
				model.removeAllElements();
				offsetX = 0;
				offsetY = 0;
				startX = 0;
				startY = 0;
				pickEye = false;
				pickColor = false;
				moveImage = false;
				pickEyesButton.setBackground(defaultButtonColor);
				pickColorButton.setBackground(defaultButtonColor);
				moveButton.setBackground(defaultButtonColor);
				pickEyesButton.setEnabled(false);
				pickColorButton.setEnabled(false);
				clearButton.setEnabled(false);
				moveButton.setEnabled(true);
				saveImageButton.setEnabled(false);
				processRedEyesButton.setEnabled(false);
				fromImage.setEnabled(false);
				JFileChooser openFile1 = new JFileChooser();
				JFileChooser openFile2 = new JFileChooser();
				int fileOpen1 = openFile1.showOpenDialog(openButton);
				int fileOpen2 = openFile2.showOpenDialog(openButton);
				if ((fileOpen1== JFileChooser.APPROVE_OPTION) && (fileOpen2== JFileChooser.APPROVE_OPTION)) {
					File file1 = openFile1.getSelectedFile();
					String fName1 = file1.toString();
					System.out.println(fName1);
					File file2 = openFile2.getSelectedFile();
					String fName2 = file2.toString();
					System.out.println(fName2);
					moveButton.setEnabled(true);

					try {
						imgOriginal = ImageIO.read(file1);
						originalImage = new RedeyeReduction(imgOriginal,offsetX,offsetY,false, eyeLocations);
						mouseEvent = new MouseEvent();
						mouseMotionEvent = new MouseMotionEvent();
						originalImage.addMouseListener(mouseEvent);
						originalImage.addMouseMotionListener(mouseMotionEvent);
						originalArea.removeAll();
						originalArea.add(originalImage);
						imgProcessed = ImageIO.read(file2);
						processedImage= new RedeyeReduction(imgProcessed,offsetX,offsetY,false, eyeLocations);
						processedArea.removeAll();
						processedArea.add(processedImage);
					} catch (IOException ex) {
						System.out.println("files does not exist" + ex.toString());
					}
					displayImages();
				}
			}
		});
		saveImageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser saveFile = new JFileChooser();
				if (saveFile.showSaveDialog(saveImageButton) == JFileChooser.APPROVE_OPTION) {
					File outputfile = saveFile.getSelectedFile();
					String fName = outputfile.toString();
					System.out.println(fName);
					try {
					    ImageIO.write(imgProcessed, "png", outputfile);
					} catch (IOException ex) {
					    System.out.println("wrong location for saving. " + ex.toString());
					}
				}
			}
		});
		pickEyesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("pick pressed");
				if (pickEye == true) {
					pickEye = false;
					pickEyesButton.setBackground(defaultButtonColor);

				}
				else{
					pickEye = true;
					pickEyesButton.setBackground(defaultButtonColor.brighter());
					moveImage = false;
					originalArea.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					moveButton.setBackground(defaultButtonColor);
				}
			}
		});
		pickColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pickColor == true) {
					pickColor = false;
					pickColorButton.setBackground(defaultButtonColor);
					colorLabel.setOpaque(false);
				}
				else{
					pickColor = true;
					pickColorButton.setBackground(defaultButtonColor.brighter());
					colorLabel.setOpaque(true);
					if(!fromImage.isSelected()){
						Color bg = JColorChooser.showDialog(colorLabel, "Choose Color", Color.white);
						colorLabel.setBackground(bg);
						pickedColor = bg;
						pickColor = false;
						pickColorButton.setBackground(defaultButtonColor);
						//colorLabel.setOpaque(false);
					}
				}

			}
		});
		moveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (moveImage == true) {
					originalArea.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					moveImage = false;
					moveButton.setBackground(defaultButtonColor);
				}
				else{
					originalArea.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					moveImage = true;
					moveButton.setBackground(defaultButtonColor.brighter());
					pickEye = false;
					pickEyesButton.setBackground(defaultButtonColor);
				}
			}
		});
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.removeAllElements();
				eyeLocations.clear();
				originalImage.setParam(offsetX,offsetY,locPicked, eyeLocations);
				originalImage.repaint();
			}
		});
		hsvHist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("hsvHist pressed");
				if (!eyeLocations.isEmpty()){
					//EyeLocation eyeLoc = eyeLocations.get(eyeLocations.size()-1);
					if(eyeLoc.width>0 && eyeLoc.height>0){
						int scale = 1000;
						if (hsvHist.isSelected()){
						int[] hHist = new int[128];
						int[] sHist = new int[128];
						int[] vHist = new int[128];

						for(int i = eyeLoc.x; i < eyeLoc.x + eyeLoc.width; i++){
							for(int j = eyeLoc.y; j < eyeLoc.y + eyeLoc.height; j++){
								int intColor = imgOriginal.getRGB(i, j);
								int b = intColor & 0x000000FF;
								int g = (intColor & 0x0000FF00) >> 8;
								int r = (intColor & 0x00FF0000) >> 16;
								float[] hsv = RGB2HSV(r,g,b);
								int h = Math.round(hsv[0]*127);
								int s = Math.round(hsv[1]*127);
								int v = Math.round(hsv[2]*127);
								hHist[h]++;
								sHist[s]++;
								vHist[v]++;
							}
						}
						int[] HistPointsX = new int[128];
						for(int k = 0; k <128; k++)
						{
							HistPointsX[k] = k;
							hHist[k] = scale * hHist[k]/(eyeLoc.width*eyeLoc.height);
							sHist[k] = scale * sHist[k]/(eyeLoc.width*eyeLoc.height);
							vHist[k] = scale * vHist[k]/(eyeLoc.width*eyeLoc.height);

						}
						redHist.drawHist(HistPointsX,hHist);
						greenHist.drawHist(HistPointsX,sHist);
						blueHist.drawHist(HistPointsX,vHist);
						redHist.repaint();
						greenHist.repaint();
						blueHist.repaint();
						}else{
							int[] rHist = new int[256];
							int[] gHist = new int[256];
							int[] bHist = new int[256];

							for(int i = eyeLoc.x; i < eyeLoc.x + eyeLoc.width; i++){
								for(int j = eyeLoc.y; j < eyeLoc.y + eyeLoc.height; j++){
									int intColor = imgOriginal.getRGB(i, j);
									int b = intColor & 0x000000FF;
									int g = (intColor & 0x0000FF00) >> 8;
									int r = (intColor & 0x00FF0000) >> 16;
									rHist[r]++;
									gHist[g]++;
									bHist[b]++;
								}
							}
							int[] HistPointsX = new int[128];
							int[] rHistPointsY = new int[128];
							int[] gHistPointsY = new int[128];
							int[] bHistPointsY = new int[128];
							for(int k = 0; k <128; k++)
							{
								HistPointsX[k] = k;
								rHistPointsY[k] = scale * (rHist[2*k]+rHist[2*k+1])/(eyeLoc.width*eyeLoc.height);
								gHistPointsY[k] = scale * (gHist[2*k]+gHist[2*k+1])/(eyeLoc.width*eyeLoc.height);
								bHistPointsY[k] = scale * (bHist[2*k]+bHist[2*k+1])/(eyeLoc.width*eyeLoc.height);

							}
							redHist.drawHist(HistPointsX,rHistPointsY);
							greenHist.drawHist(HistPointsX,gHistPointsY);
							blueHist.drawHist(HistPointsX,bHistPointsY);
							redHist.repaint();
							greenHist.repaint();
							blueHist.repaint();						
						}
					}
				}
				
			}
		});
		processWhiteEyesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("white eye processed");
				int vTh = 300;
				int diffTh = 150;
				float meanMul = (float)1.1;
				imgProcessed= new BufferedImage(imgOriginal.getWidth(), imgOriginal.getHeight(), imgOriginal.getType());
				imgProcessed.setData(imgOriginal.getData());
				Iterator itr = eyeLocations.iterator();
				while(itr.hasNext()){
					EyeLocation eye =(EyeLocation)itr.next();
					int sum =0;
					for(int i = eye.x; i < eye.x + eye.width; i++){
						for(int j = eye.y; j < eye.y + eye.height; j++){
							int intColor = imgOriginal.getRGB(i, j);
					        int b = intColor & 0x000000FF;
					        int g = (intColor & 0x0000FF00) >> 8;
					        int r = (intColor & 0x00FF0000) >> 16;
					        sum = sum + r + g +b;
						}
					}
				int mean = sum / (eye.width * eye.height);	
				for(int i = eye.x; i < eye.x + eye.width; i++){
					for(int j = eye.y; j < eye.y + eye.height; j++){
						int intColor = imgOriginal.getRGB(i, j);
				        int b = intColor & 0x000000FF;
				        int g = (intColor & 0x0000FF00) >> 8;
				        int r = (intColor & 0x00FF0000) >> 16;
				        int rNew, gNew, bNew;
				        int diff = Math.max(Math.max(r, g),b) - Math.min(Math.min(r, g),b);
				        float[] hsv = RGB2HSV(r, g, b);
						if (((r + g + b) > vTh || (r + g + b) > meanMul*mean) && (diff<diffTh) ){
							float h = hsv[0];
							float s = 0;//hsv[1]/2;
							float v = -(hsv[2]-(float)0.5)*(hsv[2]-(float)0.5) + (float)0.35;
							int[] rgb = HSV2RGB(h,s,v);
							gNew = rgb[0];
							rNew = rgb[1];
							bNew = rgb[2];
						}else{ 
							gNew = g;
							rNew = r;
							bNew = b;
						}
						Color newColorGreen = new Color(rNew,gNew,bNew);
						int newIntColor = newColorGreen.getRGB();	
						/////////////////////////
						imgProcessed.setRGB(i,j,newIntColor);
					}
				}
				}
				processedImage = new RedeyeReduction(imgProcessed,offsetX,offsetY,false, eyeLocations);
				processedArea.removeAll();
				processedArea.add(processedImage);
				saveImageButton.setEnabled(true);
				displayImages();
			}
		});
		
		processGreenEyesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("green eye processed");
				float gMin = (float)0.17;
				float gMax = (float)0.50;
				imgProcessed= new BufferedImage(imgOriginal.getWidth(), imgOriginal.getHeight(), imgOriginal.getType());
				imgProcessed.setData(imgOriginal.getData());
				Iterator itr = eyeLocations.iterator();
				while(itr.hasNext()){
					EyeLocation eye =(EyeLocation)itr.next();

				for(int i = eye.x; i < eye.x + eye.width; i++){
					for(int j = eye.y; j < eye.y + eye.height; j++){
						int intColor = imgOriginal.getRGB(i, j);
				        int b = intColor & 0x000000FF;
				        int g = (intColor & 0x0000FF00) >> 8;
				        int r = (intColor & 0x00FF0000) >> 16;
				        int rNew, gNew, bNew;
				        float[] hsv = RGB2HSV(r, g, b);
						if (hsv[0] > gMin && hsv[0] < gMax){
							//gNew = Math.round((r+b)/2);
							float h = hsv[0];
							float s = 0;//hsv[1]/2;
							float v = -(hsv[2]-(float)0.5)*(hsv[2]-(float)0.5) + (float)0.35;
							int[] rgb = HSV2RGB(h,s,v);
							gNew = rgb[0];
							rNew = rgb[1];
							bNew = rgb[2];
						}else{ 
							gNew = g;
							rNew = r;
							bNew = b;
						}
						Color newColorGreen = new Color(rNew,gNew,bNew);
						int newIntColor = newColorGreen.getRGB();	
						imgProcessed.setRGB(i,j,newIntColor);
					}
				}
				}
				processedImage = new RedeyeReduction(imgProcessed,offsetX,offsetY,false, eyeLocations);
				processedArea.removeAll();
				processedArea.add(processedImage);
				saveImageButton.setEnabled(true);
				displayImages();
			}
		});
		
		processRedEyesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("red eye processed");
				imgProcessed= new BufferedImage(imgOriginal.getWidth(), imgOriginal.getHeight(), imgOriginal.getType());
				imgProcessed.setData(imgOriginal.getData());
				Iterator itr = eyeLocations.iterator();
				while(itr.hasNext()){
					EyeLocation eye =(EyeLocation)itr.next();

				for(int i = eye.x; i < eye.x + eye.width; i++){
					for(int j = eye.y; j < eye.y + eye.height; j++){
						int intColor = imgOriginal.getRGB(i, j);
				        int b = intColor & 0x000000FF;
				        int g = (intColor & 0x0000FF00) >> 8;
				        int r = (intColor & 0x00FF0000) >> 16;
				        int rNew, gNew, bNew;

						if((r > 1.8*g) && (r>b) && (b>10) && (r>40))
							rNew = Math.round((g+b)/2);
						else
							rNew = r;
						
						int newIntColor1 = (rNew << 16) + (intColor&0xFF00FFFF);
						imgProcessed.setRGB(i,j,newIntColor1);
					}
				}
				}
				processedImage = new RedeyeReduction(imgProcessed,offsetX,offsetY,false, eyeLocations);
				processedArea.removeAll();
				processedArea.add(processedImage);
				saveImageButton.setEnabled(true);
				displayImages();
			}
		});
	}//initUI
   
	public static int[] HSV2RGB(float h, float s, float v )
	{
		float r,g,b;
		int i;
		float f, p, q, t, C, X, m;

		float hh = h *6;			// sector 0 to 5
		i = Math.round( hh - (float)0.5 );
		f = hh - i;			// factorial part of h
		p = v * ( 1 - s );
		q = v * ( 1 - s * f );
		t = v * ( 1 - s * ( 1 - f ) );
		C = v * s;
		X = C * (1 - Math.abs(hh%2 - 1));
		switch( i ) {
			case 0:
				r = C;
				g = X;
				b = 0;
				break;
			case 1:
				r = X;
				g = C;
				b = 0;
				break;
			case 2:
				r = 0;
				g = C;
				b = X;
				break;
			case 3:
				r = 0;
				g = X;
				b = C;
				break;
			case 4:
				r = X;
				g = 0;
				b = C;
				break;
			default:		// case 5:
				r = C;
				g = 0;
				b = X;
				break;
		}
		m = v - C;
		float r0 = r + m;
		float g0 = g + m;
		float b0 = b + m;
		if( s == 0 ) 
			r0 = g0 = b0 = v;;

		return new int[]{Math.round(r0*255),Math.round(g0*255),Math.round(b0*255)};
	}
	
	public static float[] RGB2HSV(int ir, int ig, int ib){

		float r = (float)ir;
		float g = (float)ig;
		float b = (float)ib;
		float h, s, v;

		float min, max, delta;

	    min = Math.min(Math.min(r, g), b);
	    max = Math.max(Math.max(r, g), b);

	    // V
	    v = max/256;

	     delta = max - min;

	    // S
	     if( max != 0 )
	        s = delta / max;
	     else {
	        s = 0;
	        h = -1;
	        return new float[]{h,s,v};
	     }

	    // H
	     if( r == max )
	        h = ( g - b ) / delta; // between yellow & magenta mod 6?
	     else if( g == max )
	        h = 2 + ( b - r ) / delta; // between cyan & yellow
	     else
	        h = 4 + ( r - g ) / delta; // between magenta & cyan

	     h *= 60;    // degrees

	    if( h < 0 )
	        h += 360;
	    h = h / 360;

	    return new float[]{h,s,v};
	}
	
	protected static void displayImages() {
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(buttonsPanel,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
								.addComponent(originalArea))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(infoPanel)
										.addComponent(processedArea))
				);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(buttonsPanel,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
								.addComponent(infoPanel,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(originalArea)
								.addComponent(processedArea)
								)
				);
	}

	private App() {
		initParams();
		initUI();
		addEventHandler();
		displayImages();
		f.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				App app = new App();
				System.out.println("test");
			}
		});
	}

}

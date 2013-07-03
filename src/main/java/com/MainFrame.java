package com;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.*;

import com.EyeProcWND;
import com.AlogrithmEvent.ColorEyeMode;

public class MainFrame {
	static BufferedImage imgOriginal;
	static BufferedImage imgProcessed; 
	static JPanel processedArea;
	static JPanel originalArea;
	static Container content;
	static JButton openButton;
	static JButton processAllButton;
	static JTextField textField;
	static JRadioButton experiment1, experiment2, experiment3;
	static JPanel radioPanel;
	static JButton processRedEyesButton;
	static JButton processGreenEyesButton;
	static JButton processWhiteEyesButton;
	static JButton pickEyesButton;
	static JButton pickColorButton;
	static JButton moveButton;
	static JButton clearButton;
	static JButton regionGrowButton;
	static JButton compareImageButton;
	static JLabel colorLabel;
	static boolean pickEye;
	static boolean pickColor;
	static boolean moveImage;
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
	static EyeProcWND originalImage;
	static EyeProcWND processedImage;
	static HistogramWND redHist;
	static HistogramWND greenHist;
	static HistogramWND blueHist;
	static ArrayList<EyeLocation> eyeLocations;
	static ArrayList<Circle> circles;
	static ArrayList<String> sEyeModes;
	static boolean locPicked;
	static EyeLocation eyeLoc;
	static Circle cirLoc;
	static Color pickedColor;
	static JPanel infoPanel;
	static boolean saveOn;
	static Point regionGrowLoc;
	static boolean regionGrowOn;
	static ChildFrame child;
	private void initParams() { 
		locPicked = false;
		saveOn = false;
		eyeLocations = new ArrayList<EyeLocation>();
		circles = new ArrayList<Circle>();
		sEyeModes = new ArrayList<String>();
		//System.out.println(eyeLocations.isEmpty());
		offsetX = 0;
		offsetY = 0;
		startX = 0;
		startY = 0;
		pickEye = false;
		pickColor = false;
		moveImage = false;
		pickedColor = Color.WHITE;
		regionGrowLoc = new Point(0,0);
		regionGrowOn =false;
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
		regionGrowButton = new JButton("Click Eyes");
		compareImageButton = new JButton("Compare Image");
		processRedEyesButton = new JButton("Red Eyes");
		processGreenEyesButton = new JButton("Green Eyes");
		processWhiteEyesButton = new JButton("White Eyes");
		fromImage = new JCheckBox("Choose from image");
		colorLabel = new JLabel();
		processAllButton = new JButton("Process All");
		textField = new JTextField();
		experiment1 = new JRadioButton("1");
		experiment2 = new JRadioButton("2");
		experiment3 = new JRadioButton("RG");
		ButtonGroup group = new ButtonGroup();
		group.add(experiment1);
		group.add(experiment2);
		group.add(experiment3);
		radioPanel = new JPanel();
		radioPanel.add(experiment1);
		radioPanel.add(experiment2);
		radioPanel.add(experiment3);

		buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Color.white);
		GridLayout bLayout = new GridLayout(3,5,5,5);
		buttonsPanel.setLayout(bLayout);
		buttonsPanel.add(openButton);
		buttonsPanel.add(moveButton);
		buttonsPanel.add(processRedEyesButton);
		buttonsPanel.add(pickColorButton);
		buttonsPanel.add(processAllButton);

		buttonsPanel.add(compareImageButton);
		buttonsPanel.add(pickEyesButton);
		buttonsPanel.add(processGreenEyesButton);
		buttonsPanel.add(colorLabel);
		buttonsPanel.add(textField);

		buttonsPanel.add(regionGrowButton);
		buttonsPanel.add(clearButton);
		buttonsPanel.add(processWhiteEyesButton);
		buttonsPanel.add(fromImage);
		buttonsPanel.add(radioPanel);

		defaultButtonColor = pickEyesButton.getBackground();
		pickEyesButton.setEnabled(false);
		pickColorButton.setEnabled(false);
		clearButton.setEnabled(false);
		moveButton.setEnabled(false);
		regionGrowButton.setEnabled(false);
		processRedEyesButton.setEnabled(false);
		processGreenEyesButton.setEnabled(false);
		processWhiteEyesButton.setEnabled(false);
		processAllButton.setEnabled(false);
		textField.setEnabled(false);
		experiment1.setEnabled(false);
		experiment2.setEnabled(false);
		experiment3.setEnabled(false);
		fromImage.setEnabled(false);
		colorLabel.setMaximumSize(new Dimension(30,30));
		Border border = BorderFactory.createLineBorder(Color.black);
		colorLabel.setBorder(border);
		colorLabel.setBackground(Color.white);

		model = new DefaultListModel();
		eyeLocList = new JList(model);
		eyeLocList.setVisibleRowCount(6);
		listPane = new JScrollPane(eyeLocList);	
		redHist = new HistogramWND(Color.RED);
		greenHist = new HistogramWND(Color.GREEN);
		blueHist = new HistogramWND(Color.BLUE);
		hsvHist = new JCheckBox("HSV Histogram");
		infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout(FlowLayout.LEADING,2,0));
		infoPanel.setBackground(Color.WHITE);
		infoPanel.add(listPane);
		infoPanel.add(redHist);
		infoPanel.add(greenHist);
		infoPanel.add(blueHist);
		infoPanel.add(hsvHist);

		originalArea = new JPanel();
		originalArea.setPreferredSize(new Dimension(800, 600));
		processedArea = new JPanel();
		processedArea.setPreferredSize(new Dimension(800, 600));

		layout = new GroupLayout(content);
		content.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
	}//init UI


	private void addEventHandler(){
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("open pressed");
				locPicked = false;
				eyeLocations.clear();
				circles.clear();
				sEyeModes.clear();
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
					regionGrowButton.setEnabled(false);
					processRedEyesButton.setEnabled(true);
					processGreenEyesButton.setEnabled(true);
					processWhiteEyesButton.setEnabled(true);
					fromImage.setEnabled(true);
					processAllButton.setEnabled(true);
					textField.setEnabled(true);
					experiment1.setEnabled(true);
					experiment2.setEnabled(true);
					experiment3.setEnabled(true);
					experiment1.setSelected(true);
					regionGrowButton.setEnabled(true);
					try {
						imgOriginal = ImageIO.read(file);
						originalImage = new EyeProcWND(imgOriginal,offsetX,offsetY,locPicked, eyeLocations);
						mouseEvent = new MouseEvent();
						mouseMotionEvent = new MouseMotionEvent();
						originalImage.addMouseListener(mouseEvent);
						originalImage.addMouseMotionListener(mouseMotionEvent);
						originalArea.removeAll();
						originalArea.add(originalImage);
						processedImage= new EyeProcWND(imgOriginal,offsetX,offsetY,false, eyeLocations);
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
				if(saveOn){
					JFileChooser saveFile = new JFileChooser();
					if (saveFile.showSaveDialog(regionGrowButton) == JFileChooser.APPROVE_OPTION) {
						File outputfile = saveFile.getSelectedFile();
						String fName = outputfile.toString();
						System.out.println(fName);
						try {
							ImageIO.write(imgProcessed, "png", outputfile);
						} catch (IOException ex) {
							System.out.println("wrong location for saving. " + ex.toString());
						}
					}
				}else{
				locPicked = false;
				eyeLocations.clear();
				circles.clear();
				sEyeModes.clear();
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
				regionGrowButton.setEnabled(false);
				processRedEyesButton.setEnabled(false);
				fromImage.setEnabled(false);
				processAllButton.setEnabled(false);
				textField.setEnabled(false);
				experiment1.setEnabled(false);
				experiment2.setEnabled(false);
				experiment3.setEnabled(false);
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
						originalImage = new EyeProcWND(imgOriginal,offsetX,offsetY,false, eyeLocations);
						mouseEvent = new MouseEvent();
						mouseMotionEvent = new MouseMotionEvent();
						originalImage.addMouseListener(mouseEvent);
						originalImage.addMouseMotionListener(mouseMotionEvent);
						originalArea.removeAll();
						originalArea.add(originalImage);
						imgProcessed = ImageIO.read(file2);
						processedImage= new EyeProcWND(imgProcessed,offsetX,offsetY,false, eyeLocations);
						processedArea.removeAll();
						processedArea.add(processedImage);
					} catch (IOException ex) {
						System.out.println("files does not exist" + ex.toString());
					}
					displayImages();
				}
				}
			}
		});
		regionGrowButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (regionGrowOn == true) {
					regionGrowOn = false;
					regionGrowButton.setBackground(defaultButtonColor);
					child.Closeup();
				}
				else{
					pickEye = false;
					pickEyesButton.setBackground(defaultButtonColor);
					moveImage = false;
					originalArea.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					moveButton.setBackground(defaultButtonColor);
					regionGrowOn = true;
					regionGrowButton.setBackground(defaultButtonColor.brighter());
					child = new ChildFrame();
//					f.setSize(new Dimension(900,800));
//					f.repaint();
					child.f.setVisible(true);
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
					regionGrowOn = false;
					regionGrowButton.setBackground(defaultButtonColor);
					if(child!=null) child.Closeup();
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
					regionGrowOn = false;
					regionGrowButton.setBackground(defaultButtonColor);
					if(child!=null)  child.Closeup();
				}
			}
		});
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.removeAllElements();
				eyeLocations.clear();
				circles.clear();
				sEyeModes.clear();
				originalImage.setParam(offsetX,offsetY,false, eyeLocations,sEyeModes,false);
				originalImage.repaint();
			}
		});
		processAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imgProcessed= new BufferedImage(imgOriginal.getWidth(), imgOriginal.getHeight(), imgOriginal.getType());
				imgProcessed.setData(imgOriginal.getData());
				for(int itr = 0; itr < eyeLocations.size(); itr++){
					EyeLocation eye =eyeLocations.get(itr);
					if(regionGrowOn){
					
					Circle circle = circles.get(itr);
					String sEyeMode = sEyeModes.get(itr);
					boolean[][] mask = circle.getMask();
					int sum =0;
					int mSize = 0;
					for(int i = 0; i < ChildFrame.PATCHSIZE*2; i++){
						for(int j = 0; j < ChildFrame.PATCHSIZE*2; j++){
							if(mask[i][j]){
								int intColor = imgOriginal.getRGB(eye.x-ChildFrame.PATCHSIZE+i, eye.y-ChildFrame.PATCHSIZE+j);
								int b = intColor & 0x000000FF;
								int g = (intColor & 0x0000FF00) >> 8;
								int r = (intColor & 0x00FF0000) >> 16;
								sum = sum + r + g +b;
								mSize++;
							}
						}
					}
					int mean = sum / mSize;	
					int rNew=0, gNew=0, bNew=0;	
					for(int i = 0; i < ChildFrame.PATCHSIZE*2; i++){
						for(int j = 0; j < ChildFrame.PATCHSIZE*2; j++){
							if(mask[i][j]){
								int ii = eye.x-ChildFrame.PATCHSIZE+i;
								int jj = eye.y-ChildFrame.PATCHSIZE+j;
							int intColor = imgOriginal.getRGB(ii,jj );
							int b = intColor & 0x000000FF;
							int g = (intColor & 0x0000FF00) >> 8;
							int r = (intColor & 0x00FF0000) >> 16;
							float[] hsv;
							if (sEyeMode=="Red"){
								if((r > 1.8*g) && (r>b) && (b>10) && (r>40)){
									rNew = Math.round((g+b)/2);
									gNew = g;
									bNew = b;
								}else{
									rNew = r;
									gNew = g;
									bNew = b;
								}
							}else{
								hsv = Util.RGB2HSV(r, g, b);
								float h = hsv[0];
								float s = 0;//hsv[1]/2;
								float v = -(hsv[2]-(float)0.5)*(hsv[2]-(float)0.5) + (float)0.35;
								int[] rgb = Util.HSV2RGB(h,s,v);
								rNew = rgb[0];
								gNew = rgb[1];
								bNew = rgb[2];
							}
							Color newColor = new Color(rNew,gNew,bNew);
							int newIntColor = newColor.getRGB();	
							imgProcessed.setRGB(ii,jj,newIntColor);
							}//if mask
						}//j
					}//i
					}//region Grow
					else{
						String sEyeMode = sEyeModes.get(itr);
						ColorEyeMode eyeMode; 
						if (sEyeMode == "Red") eyeMode = ColorEyeMode.RED;
						else if (sEyeMode == "Green") eyeMode = ColorEyeMode.GREEN;
						else if (sEyeMode == "White") eyeMode = ColorEyeMode.WHITE;
						else eyeMode = ColorEyeMode.ELSE;
						
						int sum =0;
						for(int i = eye.x; i < eye.x + eye.width; i++){
							for(int j = eye.y; j < eye.y + eye.height; j++){
								int intColor = MainFrame.imgOriginal.getRGB(i, j);
								int b = intColor & 0x000000FF;
								int g = (intColor & 0x0000FF00) >> 8;
							int r = (intColor & 0x00FF0000) >> 16;
							sum = sum + r + g +b;
							}
						}
						int mean = sum / (eye.width * eye.height);	
						for(int i = eye.x; i < eye.x + eye.width; i++){
							for(int j = eye.y; j < eye.y + eye.height; j++){
								int intColor = MainFrame.imgOriginal.getRGB(i, j);
								int b = intColor & 0x000000FF;
								int g = (intColor & 0x0000FF00) >> 8;
							int r = (intColor & 0x00FF0000) >> 16;
							int rNew=0, gNew=0, bNew=0;	
							Color newColor;
							int newIntColor; 
							float[] hsv;
							switch (eyeMode){
							case RED:
								if(MainFrame.experiment1.isSelected()){
									if((r > 1.8*g) && (r>b) && (b>10) && (r>40)){
										rNew = Math.round((g+b)/2);
										gNew = g;
										bNew = b;
									}else{
										rNew = r;
										gNew = g;
										bNew = b;
									}
									int newIntColor1 = (rNew << 16) + (intColor&0xFF00FFFF);
									MainFrame.imgProcessed.setRGB(i,j,newIntColor1);
								}else if(MainFrame.experiment2.isSelected()){ 
									float rMin= (float)0.9;
									float rMax= (float)1.0;
									hsv = Util.RGB2HSV(r, g, b);
									if (hsv[0] > rMin){
										rNew = Math.round((g+b)/2);
										gNew = g;
										bNew = b;
									}else{ 
										rNew = r;
										gNew = g;
										bNew = b;
									}
								}else{
									float rMin= (float)0.05;
									float rMax= (float)0.9;
									hsv = Util.RGB2HSV(r, g, b);
									if (hsv[0] < rMin || hsv[0] > rMax){
										float h = hsv[0];
										float s = 0;//hsv[1]/2;
										float v = -(hsv[2]-(float)0.5)*(hsv[2]-(float)0.5) + (float)0.35;
										int[] rgb = Util.HSV2RGB(h,s,v);
										rNew = rgb[0];
										gNew = rgb[1];
										bNew = rgb[2];
									}else{ 
										gNew = g;
										rNew = r;
										bNew = b;
									}
								}
								break;
							case GREEN:
								float gMin = (float)0.17;
								float gMax = (float)0.50;

								hsv = Util.RGB2HSV(r, g, b);
								if (hsv[0] > gMin && hsv[0] < gMax){
									//gNew = Math.round((r+b)/2);
									float h = hsv[0];
									float s = 0;//hsv[1]/2;
									float v = -(hsv[2]-(float)0.5)*(hsv[2]-(float)0.5) + (float)0.35;
									int[] rgb = Util.HSV2RGB(h,s,v);
									rNew = rgb[0];
									gNew = rgb[1];
									bNew = rgb[2];
								}else{ 
									gNew = g;
									rNew = r;
									bNew = b;
								}
								break;
							case WHITE:
								int vTh = 300;
								int diffTh = 150;
								float meanMul = (float)1.1;
								int diff = Math.max(Math.max(r, g),b) - Math.min(Math.min(r, g),b);
								hsv = Util.RGB2HSV(r, g, b);
								if (((r + g + b) > vTh || (r + g + b) > meanMul*mean) && (diff<diffTh) ){
									float h = hsv[0];
									float s = 0;//hsv[1]/2;
									float v = -(hsv[2]-(float)0.5)*(hsv[2]-(float)0.5) + (float)0.35;
									int[] rgb = Util.HSV2RGB(h,s,v);
									rNew = rgb[0];
									gNew = rgb[1];
									bNew = rgb[2];
								}else{ 
									gNew = g;
									rNew = r;
									bNew = b;
								}
							break;

							default:

								break;
							}
							newColor = new Color(rNew,gNew,bNew);
							newIntColor = newColor.getRGB();	
							imgProcessed.setRGB(i,j,newIntColor);

							}//j
						}//i
					}//else region grow
				}//iterator
				processedImage.setImage(imgProcessed,false);
				processedImage.repaint();
				displayImages();	
				saveOn =true;
				compareImageButton.setText("Save Image");
			}//actionPerformed
		});//ActionListener
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
									float[] hsv = Util.RGB2HSV(r,g,b);
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

		AlogrithmEvent alogrithms = new AlogrithmEvent();
		processRedEyesButton.addActionListener(alogrithms); 
		processGreenEyesButton.addActionListener(alogrithms); 
		processWhiteEyesButton.addActionListener(alogrithms); 

	}//addEventHandler

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

	static int detectEyeModes(int x,int y){
		int rMax = 0;
		int gMax = 0;
		float h = 0;
		float s = 0;
		float v = 0;
 int iEyeMode;
 int wSize = 3;
		for(int i = x-wSize; i < x+wSize+1; i++){
			for(int j = y-wSize; j < y+wSize+1; j++){
				int intColor = imgOriginal.getRGB(i, j);
				int b = intColor & 0x000000FF;
				int g = (intColor & 0x0000FF00) >> 8;
				int r = (intColor & 0x00FF0000) >> 16;
				float[] hsv = Util.RGB2HSV(r,g,b);
				if(rMax < r) rMax = r;
				if(gMax < g) gMax = g;
				h += hsv[0];
				s += hsv[1];
				v += hsv[2];
			}
		}
		h= h/((wSize*2+1)*(wSize*2+1));
		s = s/((wSize*2+1)*(wSize*2+1));
		v = v/((wSize*2+1)*(wSize*2+1));
		System.out.println("s= "+s+" rMax="+rMax+" gMax="+gMax+" x= "+x+" h="+h+" v="+v);
		if (s<0.3 || h <0.15) iEyeMode = 0 ;
		else if(rMax > gMax) iEyeMode = 1;
		else iEyeMode = 2;
		return iEyeMode;
	}
	MainFrame() {
		initParams();
		initUI();
		addEventHandler();
		displayImages();
		f.setVisible(true);
	}

}

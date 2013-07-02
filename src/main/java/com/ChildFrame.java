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

public class ChildFrame {

	static BufferedImage imgRegion; 
	static JPanel processedArea;

	static Container content;
	static JButton growButton;

	static JTextField textField;
	static JRadioButton experiment1, experiment2, experiment3;
	static JPanel radioPanel;

	static JButton regionGrowButton;

	static JFrame f;

	static JPanel buttonsPanel;
	static JCheckBox checkBox;

	static EyeProcWND processedImage;
	static int zoom = 4;
	static int gap =10;
	static int pSize = 16;

	private void initUI() { 
		f = new JFrame("Redeye Reduction");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(800, 700);
		content = f.getContentPane();
		content.setBackground(Color.white);

		growButton = new JButton("Grow");
		
		checkBox = new JCheckBox("");

		textField = new JTextField("0.2");
		experiment1 = new JRadioButton("1");
		experiment2 = new JRadioButton("2");
		experiment3 = new JRadioButton("3");
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
		GridLayout bLayout = new GridLayout(0,2,5,5);
		buttonsPanel.setLayout(bLayout);
		buttonsPanel.add(growButton);

		buttonsPanel.add(textField);

	
		//buttonsPanel.add(checkBox);
		//buttonsPanel.add(radioPanel);
		imgRegion= new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < 800; i++){
			for(int j = 0; j < 600; j++){
			imgRegion.setRGB(i,j,0xFFFFFFFF);
			}
		}


		processedArea = new JPanel();
		processedArea.setPreferredSize(new Dimension(800, 600));
		processedImage= new EyeProcWND(imgRegion,0,0,false, null);
		processedArea.removeAll();
		processedArea.add(processedImage);
		
		content.setLayout(new BorderLayout());
		f.add(buttonsPanel,BorderLayout.NORTH);
		f.add(processedArea,BorderLayout.CENTER);
		f.setLocation(1000, 300);
	}//init UI

	public void drawRegion(Point loc) {
		BufferedImage eyePatch = new BufferedImage(pSize*2, pSize*2, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < MainFrame.originalImage.getWidth(); i++){
			for(int j = 0; j < MainFrame.originalImage.getHeight(); j++){
				if ((i>=loc.x-pSize && i<loc.x+pSize) &&(j>=loc.y-pSize && j<loc.y+pSize) ){
					int intColor = MainFrame.imgOriginal.getRGB(i, j);
					int b = intColor & 0x000000FF;
					int g = (intColor & 0x0000FF00) >> 8;
					int r = (intColor & 0x00FF0000) >> 16;
			eyePatch.setRGB(i-loc.x+pSize, j-loc.y+pSize, intColor);
			float[] hsv = Util.RGB2HSV(r, g, b);
					for(int iz = 0; iz < zoom; iz++){
						for(int jz = 0; jz < zoom; jz++){
							int x1 = gap + (i-loc.x+pSize)*zoom + iz;
							int y1 = gap + (j-loc.y+pSize)*zoom + jz;
							imgRegion.setRGB(x1,y1,intColor);
							Color newColor = new Color(r,r,r);
							int newIntColor = newColor.getRGB();	
							imgRegion.setRGB(gap+x1+zoom*pSize*2,y1,newIntColor);
							newColor = new Color(g,g,g);
							newIntColor = newColor.getRGB();
							imgRegion.setRGB(x1+2*(gap+zoom*pSize*2),y1,newIntColor);
							newColor = new Color(b,b,b);
							newIntColor = newColor.getRGB();
							imgRegion.setRGB(x1+3*(gap+zoom*pSize*2),y1,newIntColor);
							
							int y2 = y1 + gap+zoom*pSize*2;
							
							int iH = Math.round(hsv[0]*255);
							newColor = new Color(iH,iH,iH);
							newIntColor = newColor.getRGB();
							imgRegion.setRGB(gap+x1+zoom*pSize*2,y2,newIntColor);
							int iS= Math.round(hsv[1]*255);
							newColor = new Color(iS,iS,iS);
							newIntColor = newColor.getRGB();
							imgRegion.setRGB(x1+2*(gap+zoom*pSize*2),y2,newIntColor);
							int iV= Math.round(hsv[2]*255);
							newColor = new Color(iV,iV,iV);
							newIntColor = newColor.getRGB();
							imgRegion.setRGB(x1+3*(gap+zoom*pSize*2),y2,newIntColor);
						}//jz
					}//iz
				}//if
			}//j
		}//i
		grow(eyePatch);
		processedImage.setImage(imgRegion, false);
		processedImage.repaint();
	}
	void grow(BufferedImage eyePatch){//regiongrowing
		Boolean[][] J = new Boolean[32][32];
		float maxDist = Float.parseFloat(textField.getText());
		float pixDist = (float)0.0;
		int x= 15;
		int y =15;
		J[x][y]=true;
		int pos=0;
		ArrayList<Pixel> pixs = new ArrayList<Pixel>();
		int intColor;
		float fI = getPixelColor(eyePatch, x, y, ColorComponents.VALUE);
		float pMin = 1;
		int index = 0;
		float regMean;
		for(int i = 0;i<32;i++){
			for(int j=0;j<32;j++){
				J[i][j]=false;
			}
		}
		pixs.add(new Pixel(x,y,fI));
		regMean = fI;
		int regSize = 0;
		while(pixDist<maxDist){
			if(x>0 && x<pSize*2-1 && y>0 && y<pSize*2-1){
				System.out.println("x="+x+" y="+y+" J="+J[x-1][y]+" pos="+pos+ " pixDist="+pixDist + " maxDist="+maxDist);
			if (!J[x-1][y]){
				fI = getPixelColor(eyePatch, x-1, y, ColorComponents.VALUE);
				pos++;
				pixs.add(new Pixel(x-1,y,fI));
				J[x-1][y] = true;
			}
			if (!J[x+1][y]){
				fI = getPixelColor(eyePatch, x+1, y, ColorComponents.VALUE);
				pos++;
				pixs.add(new Pixel(x+1,y,fI));
				J[x+1][y] = true;
			}
			if (!J[x][y-1]){
				fI = getPixelColor(eyePatch, x, y-1, ColorComponents.VALUE);
				pos++;
				pixs.add(new Pixel(x,y-1,fI));
				J[x][y-1] = true;
			}
			if (!J[x][y+1]){
				fI = getPixelColor(eyePatch, x, y+1, ColorComponents.VALUE);
				pos++;
				pixs.add(new Pixel(x,y+1,fI));
				J[x][y+1] = true;
			}
			}
			pMin = (float)1.0;
			for(int i = 0; i< pos; i++){
				if(pixs.get(i).I<pMin){
					pMin = pixs.get(i).I;
					index = i;
					pixDist = Math.abs(pixs.get(i).I - regMean);
				}
			}//for i
			regSize++;
			regMean = (regMean*regSize + pixs.get(index).I)/(regSize+1);
			x = pixs.get(index).x;
			y = pixs.get(index).y;
			pixs.set(index,pixs.get(pos));
			pixs.remove(pos);
			pos--;
			if(pos==0) break;
		}//while
		for(int i = 0; i< pSize*2; i++){
			for(int j = 0; j< pSize*2; j++){
				intColor = eyePatch.getRGB(i, j);
				int b = intColor & 0x000000FF;
				Color newColor = new Color(b,b,b);
				int newIntColor = newColor.getRGB();
				Color newColor2 = new Color(255,0,0);
				int newIntColor2 = newColor2.getRGB();

				if(J[i][j]){
					for(int iz = 0; iz < zoom; iz++){
						for(int jz = 0; jz < zoom; jz++){
							int x1 = gap + i*zoom + iz;
							int y1 = gap + j*zoom + jz;
							int y2 = y1 + 2*(gap+zoom*pSize*2);
							imgRegion.setRGB(x1,y2,newIntColor2);
							
						}//jz
					}//iz
				}else{
					for(int iz = 0; iz < zoom; iz++){
						for(int jz = 0; jz < zoom; jz++){
							int x1 = gap + i*zoom + iz;
							int y1 = gap + j*zoom + jz;
							int y2 = y1 + 2*(gap+zoom*pSize*2);
							imgRegion.setRGB(x1,y2,newIntColor);
							
						}//jz
					}//iz
				}
			}//j
		}//i
		
	}//grow
	enum ColorComponents{
		RED, GREEN, BLUE, HUE, SATUATION, VALUE
	}
	private float getPixelColor(BufferedImage eyePatch, int x, int y, ColorComponents comp) {
		int intColor = eyePatch.getRGB(x, y);
		int b = intColor & 0x000000FF;
		int g = (intColor & 0x0000FF00) >> 8;
		int r = (intColor & 0x00FF0000) >> 16;
		float[] hsv = Util.RGB2HSV(r,g,b);
		float fRe =0;				
		switch(comp){
			case RED: fRe = (float)r/(float)256.0;
			break;
			case GREEN: fRe = (float)g/(float)256.0;
			break;
			case BLUE: fRe = (float)b/(float)256.0;
			break;
			case HUE: fRe = hsv[0];
			break;
			case SATUATION: fRe = hsv[1];
			break;
			case VALUE: fRe = hsv[2];
			break;
			default: fRe = hsv[2];
			break;
		}
		return fRe;
	}
	void Closeup() {
		f.setVisible(false);
		f.dispose();
	}
	ChildFrame() {
		initUI();
		f.setVisible(true);
	}

}

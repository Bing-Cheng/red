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
import com.Util.ColorComponents;

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
	static final int ZOOM = 4;
	static final int GAP =10;
	static final int PATCHSIZE = 16;
	static final float PI = (float)3.14;
	public Circle circle;
	private void initUI() { 
		f = new JFrame("Redeye Reduction");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(800, 700);
		content = f.getContentPane();
		content.setBackground(Color.white);

		growButton = new JButton("Grow");
		
		checkBox = new JCheckBox("");

		textField = new JTextField("1");
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

	public void drawRegion(Point loc, ColorComponents comp) {
		BufferedImage eyePatch = new BufferedImage(PATCHSIZE*2, PATCHSIZE*2, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < MainFrame.originalImage.getWidth(); i++){
			for(int j = 0; j < MainFrame.originalImage.getHeight(); j++){
				if ((i>=loc.x-PATCHSIZE && i<loc.x+PATCHSIZE) &&(j>=loc.y-PATCHSIZE && j<loc.y+PATCHSIZE) ){
					int intColor = MainFrame.imgOriginal.getRGB(i, j);
					int b = intColor & 0x000000FF;
					int g = (intColor & 0x0000FF00) >> 8;
					int r = (intColor & 0x00FF0000) >> 16;
			eyePatch.setRGB(i-loc.x+PATCHSIZE, j-loc.y+PATCHSIZE, intColor);
			float[] hsv = Util.RGB2HSV(r, g, b);
					for(int iz = 0; iz < ZOOM; iz++){
						for(int jz = 0; jz < ZOOM; jz++){
							int x1 = GAP + (i-loc.x+PATCHSIZE)*ZOOM + iz;
							int y1 = GAP + (j-loc.y+PATCHSIZE)*ZOOM + jz;
							imgRegion.setRGB(x1,y1,intColor);
							Color newColor = new Color(r,r,r);
							int newIntColor = newColor.getRGB();	
							imgRegion.setRGB(GAP+x1+ZOOM*PATCHSIZE*2,y1,newIntColor);
							newColor = new Color(g,g,g);
							newIntColor = newColor.getRGB();
							imgRegion.setRGB(x1+2*(GAP+ZOOM*PATCHSIZE*2),y1,newIntColor);
							newColor = new Color(b,b,b);
							newIntColor = newColor.getRGB();
							imgRegion.setRGB(x1+3*(GAP+ZOOM*PATCHSIZE*2),y1,newIntColor);
							
							int y2 = y1 + GAP+ZOOM*PATCHSIZE*2;
							
							int iH = Math.round(hsv[0]*255);
							newColor = new Color(iH,iH,iH);
							newIntColor = newColor.getRGB();
							imgRegion.setRGB(GAP+x1+ZOOM*PATCHSIZE*2,y2,newIntColor);
							int iS= Math.round(hsv[1]*255);
							newColor = new Color(iS,iS,iS);
							newIntColor = newColor.getRGB();
							imgRegion.setRGB(x1+2*(GAP+ZOOM*PATCHSIZE*2),y2,newIntColor);
							int iV= Math.round(hsv[2]*255);
							newColor = new Color(iV,iV,iV);
							newIntColor = newColor.getRGB();
							imgRegion.setRGB(x1+3*(GAP+ZOOM*PATCHSIZE*2),y2,newIntColor);
						}//jz
					}//iz
				}//if
			}//j
		}//i
		grow(eyePatch, comp);
		processedImage.setImage(imgRegion, false);
		processedImage.repaint();
	}
	void grow(BufferedImage eyePatch, ColorComponents comp){//regiongrowing

		int x= 15;
		int y =15;
		int gridx = 4;
		int gridy = 0;
		circle = new Circle(x,y,1);
		
		BufferedImage eyePatchSmoothed = lowPassFilter(eyePatch, comp);
		int[] shift = new int[3];
		shift[0]=0;
		shift = expend(eyePatchSmoothed, circle);
		while(shift[0]==0&&(!(shift[1]==0&&shift[2]==0))){
			shift = expend(eyePatchSmoothed, circle);
			circle.ix += shift[1];
			circle.fx += (float)shift[1];
			circle.iy += shift[2];
			circle.fy += (float)shift[2];
			//System.out.println("shift[0]="+shift[0]+" shift[1]="+shift[1]+" shift[2]="+shift[2]);
		}
		//System.out.println("circle.ir="+circle.ir + " circle.fr"+circle.fr);
		boolean overlay = true;
		addPatch(eyePatchSmoothed, circle, gridx, gridy, overlay);
		addMask(circle, gridx, gridy+1);
		
	}//grow
	private int[] expend(BufferedImage eyePatch, Circle circle) {
		float scale =  Float.parseFloat(textField.getText());
		int[] shift = new int[3];
		shift[0]=0;
		int cx=0;
		int cy=0;
		int x = circle.ix;
		int y = circle.iy;
		int radius = circle.ir;
		//System.out.println("x="+x+" y="+y+" r="+radius);
		float fI = Util.getPixelColor(eyePatch, x, y, ColorComponents.VALUE);
		float regMean, var;
		regMean = circle.getMean(eyePatch, ColorComponents.VALUE);
		var = circle.getVariance(eyePatch, ColorComponents.VALUE);
		//System.out.println("regMean="+regMean+" var="+var+" r="+radius);
		boolean stop = false;
		
		while((!stop)&&(radius<10)){
			double theta = 0;
			double delta = 1/(double)radius;
			ArrayList<Integer> outsides = new ArrayList<Integer>();
			while(theta<2*PI){
				cx = (int)Math.round((double)radius * Math.cos(theta));
				cy = (int)Math.round((double)radius * Math.sin(theta));
				int dx = x + cx;
				int dy = y + cy;
				if (dx<0) dx=0;
				if (dx>PATCHSIZE*2-1) dx=PATCHSIZE*2-1;
				if (dy<0) dy=0;
				if (dy>PATCHSIZE*2-1) dy=PATCHSIZE*2-1;
				fI = Util.getPixelColor(eyePatch, dx, dy, ColorComponents.VALUE);
				//System.out.println("fI="+fI+" theta="+theta+" cx="+cx+" cy="+cy);
				if (Math.abs(fI - regMean) > var *scale){
					stop = true;
					dx = x - cx;
					dy = y - cy;
					if (dx<0) dx=0;
					if (dx>PATCHSIZE*2-1) dx=PATCHSIZE*2-1;
					if (dy<0) dy=0;
					if (dy>PATCHSIZE*2-1) dy=PATCHSIZE*2-1;
					float fI180 = Util.getPixelColor(eyePatch, dx, dy, ColorComponents.VALUE);
					if((Math.abs(fI180 - regMean) > var *scale) && (Math.signum(fI-regMean)==Math.signum(fI180-regMean))){
						shift[0] = 1;
					}else if(Math.abs(cx)>Math.abs(cy)){
						shift[1] = -cx/Math.abs(cx);
						shift[2] = -Math.round(cy/Math.abs(cx));
					}else{
						shift[1] = -Math.round(cx/Math.abs(cy));;
						shift[2] = -cy/Math.abs(cy);;
					}
					break;
				}
				
				theta += delta;
			}//theta
			if(shift[1]==0&&shift[2]==0)
			{
				int tmp=1;
				int tmp2= tmp;
			}
			radius++;
			circle.ir = radius;
			circle.fr = (float)radius;
			regMean = circle.getMean(eyePatch, ColorComponents.VALUE);
			var = circle.getVariance(eyePatch, ColorComponents.VALUE);
			//System.out.println("regMean="+regMean+" var="+var+" r="+radius);
		}//radius
		return shift;
	}
	private BufferedImage lowPassFilter(BufferedImage eyePatch, ColorComponents comp){
		BufferedImage eyePatchSmoothed = new BufferedImage(PATCHSIZE*2, PATCHSIZE*2, BufferedImage.TYPE_INT_RGB);
		
		for(int i = 1; i< PATCHSIZE*2-1; i++){
			for(int j = 1; j< PATCHSIZE*2-1; j++){
				float fI=0;
				for (int ii = -1; ii < 2; ii++){
					for (int jj = -1; jj <2; jj++){
						fI += Util.getPixelColor(eyePatch, i+ii, j+jj, comp);
						
					}
				}
				int iI = Math.round(fI*255/9);
				//System.out.println("fI="+fI+" iI="+iI);
				Color newColor = new Color(iI,iI,iI);
				int newIntColor = newColor.getRGB();
				eyePatchSmoothed.setRGB(i, j, newIntColor);
			}//j
		}//i
		return eyePatchSmoothed;
	}

	private void addPatch(BufferedImage eyePatch, Circle circle, int gridx, int gridy, boolean overlay) {
		for(int i = 0; i< PATCHSIZE*2; i++){
			for(int j = 0; j< PATCHSIZE*2; j++){
				float fI = Util.getPixelColor(eyePatch, i, j, ColorComponents.VALUE);
				int iI = Math.round(fI*255);
				Color newColor = new Color(iI,iI,iI);
				setPixelColor(gridx, gridy, i, j, newColor);
			}//j
		}//i
		if (overlay){
			Color boundaryColor = new Color(255,0,0);
			ArrayList<Pixel> boundary = circle.getBoundary();
			for (int i = 0; i < boundary.size(); i++) {
				Pixel pix = boundary.get(i);
				setPixelColor(gridx, gridy, pix.getX(), pix.getY(), boundaryColor);
				//System.out.println("pix.getX()="+pix.getX()+" pix.getY()="+pix.getY()+" boundary"+boundary.size());
			}
		}
	}
	private void addMask(Circle circle, int gridx, int gridy) {
		boolean[][] mask = circle.getMask();
		for(int i = 0; i< PATCHSIZE*2; i++){
			for(int j = 0; j< PATCHSIZE*2; j++){
				if(mask[i][j])
					setPixelColor(gridx, gridy, i, j, new Color(255,0,0));
				else
					setPixelColor(gridx, gridy, i, j, new Color(110,150,150));
			}//j
		}//i
	}

	private void setPixelColor(int gridx, int gridy, int i, int j,
			Color newColor) {
		int newIntColor = newColor.getRGB();
		for(int iz = 0; iz < ZOOM; iz++){
			for(int jz = 0; jz < ZOOM; jz++){
				int x1 = GAP + i*ZOOM + iz;
				int y1 = GAP + j*ZOOM + jz;
				int x2 = x1 + gridx*(GAP+ZOOM*PATCHSIZE*2);
				int y2 = y1 + gridy*(GAP+ZOOM*PATCHSIZE*2);
				imgRegion.setRGB(x2,y2,newIntColor);
			}//jz
		}//iz
	}

	

	
	void Closeup() {
		f.setVisible(false);
		f.dispose();
	}
	ChildFrame() {
		initUI();
		//f.setVisible(true);
	}

}

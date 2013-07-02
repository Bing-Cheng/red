package com;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.util.Iterator;

public class AlogrithmEvent implements ActionListener{

	public enum ColorEyeMode {
		WHITE, GREEN, RED, ELSE
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		System.out.println(command);
		ColorEyeMode eyeMode; 
		if (command == "Red Eyes") eyeMode = ColorEyeMode.RED;
		else if (command == "Green Eyes") eyeMode = ColorEyeMode.GREEN;
		else if (command == "White Eyes") eyeMode = ColorEyeMode.WHITE;
		else eyeMode = ColorEyeMode.ELSE;
		MainFrame.imgProcessed= new BufferedImage(MainFrame.imgOriginal.getWidth(), MainFrame.imgOriginal.getHeight(), MainFrame.imgOriginal.getType());
		MainFrame.imgProcessed.setData(MainFrame.imgOriginal.getData());
		Iterator itr = MainFrame.eyeLocations.iterator();
		while(itr.hasNext()){
			EyeLocation eye =(EyeLocation)itr.next();
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
				MainFrame.imgProcessed.setRGB(i,j,newIntColor);

				}//j
			}//i
		}//iterator
		MainFrame.processedImage.setImage(MainFrame.imgProcessed,false);
		MainFrame.processedImage.repaint();
		MainFrame.displayImages();	
		MainFrame.saveOn =true;
		MainFrame.compareImageButton.setText("Save Image");
	}//ActionListener
}//class
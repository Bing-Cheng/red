package com;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.*;
import javax.swing.*;
import com.EyeLocation; 
import com.Circle;
/**
 * This class demonstrates how to load an Image from an external file
 */
public class EyeProcWND extends Component {

	BufferedImage img;
	int offsetX;
	int offsetY;
	ArrayList<String> sEyeModes;
	boolean overlay = false;
	boolean circleOn = false;
	ArrayList<EyeLocation> eyeLocations;
	ArrayList<Circle> circles;
	public void paint(Graphics g) {
		g.drawImage(img, offsetX, offsetY, null);
		if (overlay){
			for(int i = 0; i < eyeLocations.size(); i++){
				EyeLocation eye =eyeLocations.get(i);
				String sEyeMode = sEyeModes.get(i);
				if(sEyeMode=="Red")
					g.setColor(new Color(255,0,0));
				else if(sEyeMode=="Green")
					g.setColor(new Color(0,255,0));
				else
					g.setColor(new Color(255,255,255));
				if (circleOn){
					Circle cir = circles.get(i); 
					ArrayList<Pixel> boundary = cir.getBoundary();
					int x = eye.x + offsetX-ChildFrame.PATCHSIZE;
					int y = eye.y + offsetY-ChildFrame.PATCHSIZE;
					for(int j =0;j<boundary.size(); j++){
						g.drawLine(x+boundary.get(j).x, y+boundary.get(j).y, x+boundary.get(j).x, y+boundary.get(j).y);
					}
					
				}else{
					
					g.drawRect(eye.x + offsetX, eye.y + offsetY, eye.width, eye.height);
				}
			}

		}
	}

	public EyeProcWND(BufferedImage image, int x, int y, boolean overlay, ArrayList<EyeLocation> eyeLocations) {
		this.img = image;
		this.offsetX = x;
		this.offsetY = y;
		this.overlay = overlay;
		this.eyeLocations = eyeLocations;
	}

	void setParam(int x, int y, boolean overlay, ArrayList<EyeLocation> eyeLocations) {
		this.offsetX = x;
		this.offsetY = y;
		this.overlay = overlay;
		this.eyeLocations = eyeLocations;
	}
	
	void setCircle(ArrayList<Circle> circles, boolean circleOn, boolean overlay, ArrayList<String> sEyeModes) {
		this.circles = circles;
		this.circleOn = circleOn;
		this.overlay = overlay;
		this.sEyeModes = sEyeModes;
	}
	
	void setImage(BufferedImage image, boolean overlay) {
		this.img = image;
		this.overlay = overlay;
	} 
	
	public Dimension getPreferredSize() {
		if (img == null) {
			return new Dimension(100,100);
		} else {
			return new Dimension(img.getWidth(null), img.getHeight(null));
		}
	}

}
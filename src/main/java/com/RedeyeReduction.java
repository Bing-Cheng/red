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
/**
 * This class demonstrates how to load an Image from an external file
 */
public class RedeyeReduction extends Component {

	BufferedImage img;
	int offsetX;
	int offsetY;
	Boolean overlay = false;
	ArrayList<EyeLocation> eyeLocations;
	public void paint(Graphics g) {
		g.drawImage(img, offsetX, offsetY, null);
		if (overlay){
			Iterator itr = eyeLocations.iterator();
			while(itr.hasNext()){
				EyeLocation eye =(EyeLocation)itr.next();
				g.drawRect(eye.x + offsetX, eye.y + offsetY, eye.width, eye.height);
			}

		}
	}

	public RedeyeReduction(BufferedImage image, int x, int y, Boolean overlay, ArrayList<EyeLocation> eyeLocations) {
		this.img = image;
		this.offsetX = x;
		this.offsetY = y;
		this.overlay = overlay;
		this.eyeLocations = eyeLocations;
	}

	void setParam(int x, int y, Boolean overlay, ArrayList<EyeLocation> eyeLocations) {
		this.offsetX = x;
		this.offsetY = y;
		this.overlay = overlay;
		this.eyeLocations = eyeLocations;
	}
	public Dimension getPreferredSize() {
		if (img == null) {
			return new Dimension(100,100);
		} else {
			return new Dimension(img.getWidth(null), img.getHeight(null));
		}
	}

}
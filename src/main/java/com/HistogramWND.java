package com;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.*;
import javax.swing.*;

public class HistogramWND extends JLabel {

	int xPoints[];
	int yPoints[];
	int nPoints;
	int max;
	Color color;
	boolean drawOn;
	Dimension windowSize;
	public void paint(Graphics g) {

			
				if (drawOn){
					g.setColor(color);
					g.drawPolyline(xPoints, yPoints, nPoints);
					g.setColor(Color.BLACK);
					g.drawRect(0, 0, windowSize.width-1, windowSize.height-1);
				}

	}

	public HistogramWND(Color color) {
		this.color = color;
		drawOn = false;
		windowSize = this.getSize();
//		System.out.println("width="+windowSize.width + " height=" +windowSize.height + " max="+this.max);
	}
	
	public void drawHist(int[] xPoints, int[] yPoints) {
		drawOn = true;
		this.xPoints = xPoints;
		this.yPoints = yPoints;
		this.max = 0;
		this.nPoints = yPoints.length;
	    for (int i=0; i<this.nPoints; i++) {
	        if (yPoints[i] > this.max) {
	        	this.max = yPoints[i];   // new maximum
	        }
	        this.yPoints[i] = 100 - yPoints[i];
	    }
		windowSize = this.getSize();
//		System.out.println("width="+windowSize.width + " height=" +windowSize.height + " max="+this.max);
	}
	public Dimension getPreferredSize() {

			return new Dimension(128,100);

	}

}
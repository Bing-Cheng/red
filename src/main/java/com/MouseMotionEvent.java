package com;

import java.awt.event.MouseMotionListener;

public class MouseMotionEvent implements MouseMotionListener {

	public void mouseDragged(java.awt.event.MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("drag X = " + (e.getX()-MainFrame.offsetX) +"   Y = " + (e.getY()-MainFrame.offsetY));
		if (MainFrame.moveImage == true) {
			int X = e.getX()- MainFrame.offsetX - MainFrame.startX;
			int Y = e.getY() - MainFrame.offsetY - MainFrame.startY;
			if (Math.abs(X)>10||Math.abs(Y)>10){
				MainFrame.startX = e.getX() - MainFrame.offsetX;
				MainFrame.startY = e.getY() - MainFrame.offsetY;
				MainFrame.offsetX = MainFrame.offsetX + X;
				MainFrame.offsetY = MainFrame.offsetY + Y;
				MainFrame.originalImage.setParam(MainFrame.offsetX,MainFrame.offsetY,MainFrame.locPicked, MainFrame.eyeLocations,MainFrame.sEyeModes,false);
				MainFrame.originalImage.repaint();
				MainFrame.processedImage.setParam(MainFrame.offsetX,MainFrame.offsetY,false, MainFrame.eyeLocations,MainFrame.sEyeModes,false);
				MainFrame.processedImage.repaint();
			}
		}else if (MainFrame.pickEye == true) {
			int xMin = ((e.getX()-MainFrame.offsetX) > MainFrame.startX) ? MainFrame.startX : e.getX() - MainFrame.offsetX;
			int width = Math.abs(MainFrame.startX - e.getX() + MainFrame.offsetX);
			int yMin = ((e.getY()-MainFrame.offsetY) > MainFrame.startY) ? MainFrame.startY : e.getY() - MainFrame.offsetY;
			int height = Math.abs(MainFrame.startY - e.getY() + MainFrame.offsetY);

			MainFrame.eyeLoc.set(xMin,yMin,width,height);
			MainFrame.originalImage.setParam(MainFrame.offsetX,MainFrame.offsetY,MainFrame.locPicked, MainFrame.eyeLocations,MainFrame.sEyeModes,false);
			MainFrame.originalImage.repaint();
		}//pickEye
	}

	public void mouseMoved(java.awt.event.MouseEvent e) {
		//System.out.println("move X = " + (e.getX()-offsetX) +"   Y = " + (e.getY()-offsetY));
	}

}

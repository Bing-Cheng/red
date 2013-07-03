package com;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseListener;

public class MouseEvent implements MouseListener {

	public void mouseClicked(java.awt.event.MouseEvent e) {
		System.out.println("mouseClicked");
		if (MainFrame.regionGrowOn){
			int x = e.getX() - MainFrame.offsetX;
			int y = e.getY() - MainFrame.offsetY;
			int iEyeMode = MainFrame.detectEyeModes(x,y);
			MainFrame.child.drawRegion(new Point(x,y));
			MainFrame.eyeLoc = new EyeLocation(x,y,0,0);
			MainFrame.cirLoc = new Circle(MainFrame.child.circle.ix, MainFrame.child.circle.iy, MainFrame.child.circle.ir);
			if(!MainFrame.eyeLocations.isEmpty()){
				for (int i = 0; i < MainFrame.eyeLocations.size(); i++){
					int oldx = MainFrame.eyeLocations.get(i).x;
					int oldy = MainFrame.eyeLocations.get(i).y;
					int radius2 = (oldx - MainFrame.eyeLoc.x)*(oldx - MainFrame.eyeLoc.x) + (oldy - MainFrame.eyeLoc.y)*(oldy - MainFrame.eyeLoc.y);
					if(radius2 < 200){
						MainFrame.circles.remove(i);
						MainFrame.eyeLocations.remove(i);
						MainFrame.model.remove(i);
						MainFrame.sEyeModes.remove(i);
					}
				}
			}
			MainFrame.circles.add(MainFrame.cirLoc);
			MainFrame.eyeLocations.add(MainFrame.eyeLoc);
			
			String sEyeMode;
			if (iEyeMode==1) sEyeMode = "Red";
			else if (iEyeMode==2) sEyeMode = "Green";
			else sEyeMode = "White";
			MainFrame.sEyeModes.add(sEyeMode);
			MainFrame.model.addElement(sEyeMode + ": x = " + MainFrame.eyeLoc.x + ";  y = " + MainFrame.eyeLoc.y);
			MainFrame.originalImage.setCircle(MainFrame.circles, true, true, MainFrame.sEyeModes);
			MainFrame.originalImage.repaint();
			
		}else if (MainFrame.pickColor == true && MainFrame.fromImage.isSelected()) {
			int rgb = MainFrame.imgOriginal.getRGB(e.getX() - MainFrame.offsetX, e.getY() - MainFrame.offsetY);
			Color bg = new Color(rgb);
			MainFrame.colorLabel.setBackground(bg);
			MainFrame.pickedColor = bg;
			System.out.println("r="+bg.getRed()+" g="+bg.getGreen()+" b="+bg.getBlue());
			float[] hsv = Util.RGB2HSV(bg.getRed(),bg.getGreen(),bg.getBlue());
			System.out.println("h="+hsv[0]+" s="+hsv[1]+" v="+hsv[2]);
		}
	}

	public void mousePressed(java.awt.event.MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("mousePressed");
		MainFrame.startX = e.getX() - MainFrame.offsetX;
		MainFrame.startY = e.getY() - MainFrame.offsetY;
		if (MainFrame.pickEye){
			MainFrame.eyeLoc = new EyeLocation(MainFrame.startX,MainFrame.startY,0,0);
			MainFrame.eyeLocations.add(MainFrame.eyeLoc);
			MainFrame.locPicked = true;
		}
	}

	public void mouseReleased(java.awt.event.MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("mouseReleased");

		if (MainFrame.pickEye){
			MainFrame.model.addElement("x = " + MainFrame.eyeLoc.x + ";  y = " + MainFrame.eyeLoc.y + ";  w = " + MainFrame.eyeLoc.width + ";  h = " + MainFrame.eyeLoc.height);
			if (MainFrame.eyeLoc.width>0 && MainFrame.eyeLoc.height >0){
				int histScale = 1000;
				if (MainFrame.hsvHist.isSelected()){
				int[] hHist = new int[128];
				int[] sHist = new int[128];
				int[] vHist = new int[128];

				for(int i = MainFrame.eyeLoc.x; i < MainFrame.eyeLoc.x + MainFrame.eyeLoc.width; i++){
					for(int j = MainFrame.eyeLoc.y; j < MainFrame.eyeLoc.y + MainFrame.eyeLoc.height; j++){
						int intColor = MainFrame.imgOriginal.getRGB(i, j);
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
					hHist[k] = histScale * hHist[k]/(MainFrame.eyeLoc.width*MainFrame.eyeLoc.height);
					sHist[k] = histScale * sHist[k]/(MainFrame.eyeLoc.width*MainFrame.eyeLoc.height);
					vHist[k] = histScale * vHist[k]/(MainFrame.eyeLoc.width*MainFrame.eyeLoc.height);

				}
				MainFrame.redHist.drawHist(HistPointsX,hHist);
				MainFrame.greenHist.drawHist(HistPointsX,sHist);
				MainFrame.blueHist.drawHist(HistPointsX,vHist);
				MainFrame.redHist.repaint();
				MainFrame.greenHist.repaint();
				MainFrame.blueHist.repaint();
				}else{
					int[] rHist = new int[256];
					int[] gHist = new int[256];
					int[] bHist = new int[256];

					for(int i = MainFrame.eyeLoc.x; i < MainFrame.eyeLoc.x + MainFrame.eyeLoc.width; i++){
						for(int j = MainFrame.eyeLoc.y; j < MainFrame.eyeLoc.y + MainFrame.eyeLoc.height; j++){
							int intColor = MainFrame.imgOriginal.getRGB(i, j);
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
						rHistPointsY[k] = histScale * (rHist[2*k]+rHist[2*k+1])/(MainFrame.eyeLoc.width*MainFrame.eyeLoc.height);
						gHistPointsY[k] = histScale * (gHist[2*k]+gHist[2*k+1])/(MainFrame.eyeLoc.width*MainFrame.eyeLoc.height);
						bHistPointsY[k] = histScale * (bHist[2*k]+bHist[2*k+1])/(MainFrame.eyeLoc.width*MainFrame.eyeLoc.height);

					}
					MainFrame.redHist.drawHist(HistPointsX,rHistPointsY);
					MainFrame.greenHist.drawHist(HistPointsX,gHistPointsY);
					MainFrame.blueHist.drawHist(HistPointsX,bHistPointsY);
					MainFrame.redHist.repaint();
					MainFrame.greenHist.repaint();
					MainFrame.blueHist.repaint();						
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
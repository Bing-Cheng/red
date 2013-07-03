package com;

public class Pixel {
	int x;
	int y;
	float I;
	Pixel(int x, int y, float I){
		this.x = x;
		this.y = y;
		this.I = I;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setI(float i) {
		I = i;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public float getI() {
		return I;
	}

}

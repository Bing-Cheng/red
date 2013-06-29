package com;

public class EyeLocation {
	int x;
	int y;
	int width;
	int height;
	EyeLocation(){
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
	}
EyeLocation(int x,int y,int width,int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
void set(int x,int y,int width,int height){
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
}

}

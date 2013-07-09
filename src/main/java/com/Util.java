package com;

import java.awt.image.BufferedImage;


public class Util {

	public static int[] HSV2RGB(float h, float s, float v )
	{
		float r,g,b;
		int i;
		float f, p, q, t, C, X, m;
	
		float hh = h *6;			// sector 0 to 5
		i = Math.round( hh - (float)0.5 );
		f = hh - i;			// factorial part of h
		p = v * ( 1 - s );
		q = v * ( 1 - s * f );
		t = v * ( 1 - s * ( 1 - f ) );
		C = v * s;
		X = C * (1 - Math.abs(hh%2 - 1));
		switch( i ) {
			case 0:
				r = C;
				g = X;
				b = 0;
				break;
			case 1:
				r = X;
				g = C;
				b = 0;
				break;
			case 2:
				r = 0;
				g = C;
				b = X;
				break;
			case 3:
				r = 0;
				g = X;
				b = C;
				break;
			case 4:
				r = X;
				g = 0;
				b = C;
				break;
			default:		// case 5:
				r = C;
				g = 0;
				b = X;
				break;
		}
		m = v - C;
		float r0 = r + m;
		float g0 = g + m;
		float b0 = b + m;
		if( s == 0 ) 
			r0 = g0 = b0 = v;;
	
		return new int[]{Math.round(r0*255),Math.round(g0*255),Math.round(b0*255)};
	}

	public static float[] RGB2HSV(int ir, int ig, int ib){
	
		float r = (float)ir;
		float g = (float)ig;
		float b = (float)ib;
		float h, s, v;
	
		float min, max, delta;
	
	    min = Math.min(Math.min(r, g), b);
	    max = Math.max(Math.max(r, g), b);
	
	    // V
	    v = max/256;
	
	     delta = max - min;
	
	    // S
	     if( max != 0 )
	        s = delta / max;
	     else {
	        s = 0;
	        h = 0;
	        return new float[]{h,s,v};
	     }
	
	    // H
	     if( r == max )
	        h = ( g - b ) / delta; // between yellow & magenta mod 6?
	     else if( g == max )
	        h = 2 + ( b - r ) / delta; // between cyan & yellow
	     else
	        h = 4 + ( r - g ) / delta; // between magenta & cyan
	
	     h *= 60;    // degrees
	
	    if( h < 0 )
	        h += 360;
	    h = h / 360;
	
	    return new float[]{h,s,v};
	}
	enum ColorComponents{
		RED, GREEN, BLUE, HUE, SATUATION, VALUE
	}
	static float getPixelColor(BufferedImage eyePatch, int x, int y, ColorComponents comp) {
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
}

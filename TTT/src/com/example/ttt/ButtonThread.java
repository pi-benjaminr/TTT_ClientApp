package com.example.ttt;

public class ButtonThread extends Thread {
	
	private int i=0;
	
	public ButtonThread(int x) {
		i = x;
	}
	
	
	@Override
	public void run() {
		MainActivity.send("MOVE " + MainActivity.num + " " + i + " " + MainActivity.ch);
	}

}

package com.example.ttt;

import android.util.Log;
import android.view.View;

public class ButtonListener implements View.OnClickListener {
	
	private int i = 0;
	
	public ButtonListener(int x) {
		i = x;
	}

	@Override
	public void onClick(View v) {
		Log.i("ttt", "testing");

		if (MainActivity.myTurn) {
			new ButtonThread(i).start();
			MainActivity.board = MainActivity.board.substring(0, i) + MainActivity.ch + MainActivity.board.substring(i+1, 9);
			Log.i("ttt", "It is no longer your turn");

			MainActivity.myTurn = false;
		}

	}
}

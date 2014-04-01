package com.example.ttt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	protected static String serverAddress = "ec2-54-184-192-113.us-west-2.compute.amazonaws.com";
	protected static int serverPort = 20000;
	protected static int num = 1;
	protected static char ch = 'x';
	protected static String board = "---------\n";
	protected static boolean myTurn = false;

	public static final int MAX_PACKET_SIZE = 512;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button[] b = new Button[9];
		b[0] = (Button) findViewById(R.id.button1);
		b[1] = (Button) findViewById(R.id.button2);
		b[2] = (Button) findViewById(R.id.button3);
		b[3] = (Button) findViewById(R.id.button4);
		b[4] = (Button) findViewById(R.id.button5);
		b[5] = (Button) findViewById(R.id.button6);
		b[6] = (Button) findViewById(R.id.button7);
		b[7] = (Button) findViewById(R.id.button8);
		b[8] = (Button) findViewById(R.id.button9);

		Button reg = (Button) findViewById(R.id.button10);
		Button play = (Button) findViewById(R.id.button11);
		Button update = (Button) findViewById(R.id.button12);

		play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("ttt", "play button clicked");

				new Thread() {
					@Override
					public void run() {
						String resp = send("PLAY " + num);
						Log.i("ttt", resp);
						if (resp.startsWith("Game")) {
							myTurn = true;
							ch = 'o';
						}
					}
				}.start();

			}
		});

		reg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("ttt", "register button clicked");

				new Thread() {
					@Override
					public void run() {
						String resp = send("REGISTER");
						Log.i("ttt", resp);
						num = Integer.parseInt(resp.substring(0,
								resp.length() - 1));
					}
				}.start();

			}
		});

		update.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("ttt", "update button clicked");

				Thread t = new Thread() {
					@Override
					public void run() {
						String resp = send("UPDATE " + num);
						if (!board.substring(0,9).equals(resp.substring(0,9))) {
							Log.i("ttt", "It is now your turn");
							myTurn = true;
						}
						board = resp;
					}
				};

				t.start();
				try {
					t.join();
				} catch (InterruptedException e) {
				}
				drawBoard(b, board);

			}

		});

		for (int i = 0; i < 9; i++) {
			b[i].setOnClickListener(new ButtonListener(i));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	protected static String send(String command) {
		String payload = "";
		try {
			InetSocketAddress serverSocketAddress = new InetSocketAddress(
					serverAddress, serverPort);
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket txPacket = new DatagramPacket(command.getBytes(),
					command.length(), serverSocketAddress);
			socket.send(txPacket);

			byte[] buf = new byte[MAX_PACKET_SIZE];
			DatagramPacket rxPacket = new DatagramPacket(buf, buf.length);
			socket.receive(rxPacket);
			payload = new String(rxPacket.getData(), 0, rxPacket.getLength());

			socket.close();
		} catch (SocketException e) {
		} catch (IOException e) {
		}
		return payload;
	}

	
	protected void drawBoard(Button[] b, String board) {
		Log.i("ttt", board);
		for (int i = 0; i < 9; i++) {
			char c = board.charAt(i);
			String text = "" + c;
			b[i].setText(text);
			if (myTurn) {
				b[i].setEnabled(c == '-');
			} else {
				b[i].setEnabled(false);
			}
		}
		if (isOver()) {
			new Thread() {
				@Override
				public void run() {
					send("END " + num);
				}
			}.start();
			Log.i("ttt", "Game over");
			myTurn = false;
			
			
		}
	}
	
	protected boolean isOver() {
		return (board.charAt(0)!='-' && board.charAt(0) == board.charAt(3) && board.charAt(3)==board.charAt(6)) ||
				(board.charAt(1)!='-' && board.charAt(1) == board.charAt(4) && board.charAt(4)==board.charAt(7)) ||
				(board.charAt(2)!='-' && board.charAt(2) == board.charAt(5) && board.charAt(5)==board.charAt(8)) ||
				(board.charAt(0)!='-' && board.charAt(0) == board.charAt(1) && board.charAt(1)==board.charAt(2)) ||
				(board.charAt(3)!='-' && board.charAt(3) == board.charAt(4) && board.charAt(4)==board.charAt(5)) ||
				(board.charAt(6)!='-' && board.charAt(6) == board.charAt(7) && board.charAt(7)==board.charAt(8)) ||
				(board.charAt(0)!='-' && board.charAt(0) == board.charAt(4) && board.charAt(4)==board.charAt(8)) ||
				(board.charAt(2)!='-' && board.charAt(2) == board.charAt(4) && board.charAt(4)==board.charAt(6));
	}

}

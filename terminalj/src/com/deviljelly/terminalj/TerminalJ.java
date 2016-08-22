package com.deviljelly.terminalj;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import com.deviljelly.terminalj.terminal.Color;
import com.deviljelly.terminalj.terminal.Intensity;
import com.deviljelly.terminalj.terminal.Terminal;
import com.deviljelly.terminalj.terminal.TerminalFactory;

public class TerminalJ {
	
	public static void main(String[] args) {
		
		
		
		
		try {
			String[] cmd = {"/bin/sh", "-c", "stty raw -echo </dev/tty"};
			Runtime.getRuntime().exec(cmd);
			
		} catch(Exception e) {
			
		}
		
		System.out.println("hello git world");

	
		long stop=0;
		long start = 0;
		
		Random r = new Random();
		
		Terminal terminal  = TerminalFactory.getTerminal();
		
		
		terminal.deviceStatusReport();
		
		
		
		
		terminal.hideCursor();
		
		for(int j=0;j<100;j++) {
			
			terminal.setBackgroundColor(Color.BLACK, Intensity.NORMAL);
			terminal.eraseFullDisplay();
			
			int col = r.nextInt(7);
			Color c = Color.BLACK;
			
			switch (col) {
			case 0:
				c = Color.BLACK;
				break;
			case 1:
				c = Color.RED;
				break;
			case 2:
				c = Color.GREEN;
				break;
			case 3:
				c = Color.YELLOW;
				break;
			case 4:
				c = Color.BLUE;
				break;
			case 5:
				c = Color.MAGENTA;
				break;
			case 6:
				c = Color.CYAN;
				break;
			case 7:
				c = Color.WHITE;
				break;

			default:
				break;
			}
			
			start = System.nanoTime();
			
			terminal.setBackgroundColor(c, Intensity.BRIGHT);
			for(int i=0;i<1000	;i++) {
				terminal.cursorTo(r.nextInt(70), r.nextInt(200));
				terminal.write((byte)' ');
				
			}
			stop = System.nanoTime();
			terminal.flush();
			try {
				Thread.sleep(100);
			}catch(InterruptedException ie) {
				
			}
		}
		

		terminal.setBackgroundColor(Color.BLACK, Intensity.NORMAL);
		terminal.eraseFullDisplay();
		terminal.cursorTo(1, 1);
		terminal.showCursor();
		
		
		
		

		for(int i=0;i<10;i++) {
			terminal.cursorForward(1);
			terminal.flush();
			try {
				Thread.sleep(50);
			}catch(InterruptedException ie) {
				
			}
			
			
		}

		for(int i=0;i<10;i++) {
			terminal.cursorDown(1);
			terminal.flush();
			try {
				Thread.sleep(50);
			}catch(InterruptedException ie) {
				
			}
		}


		for(int i=0;i<10;i++) {
			terminal.cursorBack(1);
			terminal.flush();
			try {
				Thread.sleep(50);
			}catch(InterruptedException ie) {
				
			}
		}


		for(int i=0;i<10;i++) {
			terminal.cursorUp(1);
			terminal.flush();
			try {
				Thread.sleep(50);
			}catch(InterruptedException ie) {
				
			}
		}

		

	//	try {
	//		Thread.sleep(2000);
	//	}catch(InterruptedException ie) {
			
	//	} 
		
		terminal.showCursor();
		try {
			String[] cmd = {"/bin/sh", "-c", "stty cooked echo </dev/tty"};
			Runtime.getRuntime().exec(cmd);
			
		} catch(Exception e) {
			
		}

		
		
	//	System.out.println((stop-start)/1000);
	}

}

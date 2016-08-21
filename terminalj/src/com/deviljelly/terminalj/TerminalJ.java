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
		System.out.println("hello git world");

	
		long stop=0;
		long start = 0;
		
		Random r = new Random();
		
		Terminal terminal  = TerminalFactory.getTerminal();
		
		terminal.hideCursor();
		
		for(int j=0;j<30;j++) {
			
			terminal.setBackgroundColor(Color.BLACK, Intensity.NORMAL);
			terminal.clearScreen();
			
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
				terminal.moveTo(r.nextInt(70), r.nextInt(200));
				terminal.write((byte)'*');
				
			}
			stop = System.nanoTime();
			terminal.flush();
			try {
				Thread.sleep(100);
			}catch(InterruptedException ie) {
				
			}
		}
		
		terminal.setBackgroundColor(Color.BLACK, Intensity.NORMAL);
		terminal.clearScreen();
		terminal.moveTo(1, 1);
		terminal.showCursor();
		System.out.println((stop-start)/1000);
	}

}

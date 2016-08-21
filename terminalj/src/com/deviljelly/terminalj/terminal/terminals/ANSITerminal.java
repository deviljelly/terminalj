package com.deviljelly.terminalj.terminal.terminals;


import java.io.IOException;

import com.deviljelly.terminalj.TerminalInputStream;
import com.deviljelly.terminalj.TerminalOutputStream;
import com.deviljelly.terminalj.terminal.AbstractTerminal;
import com.deviljelly.terminalj.terminal.Terminal;
import com.deviljelly.terminalj.terminal.Type;
import com.deviljelly.terminalj.terminal.codepage.ASCII;

public class ANSITerminal extends AbstractTerminal implements Terminal {
	
	
	public static final byte[] CSI = {ASCII.ESC, ASCII.LEFT_SQUARE_BRACKET};
	
	

	public ANSITerminal(TerminalInputStream terminalInputStream, TerminalOutputStream terminalOutputStream) {
		super(terminalInputStream, terminalOutputStream);
	}

	@Override
	public void moveTo(int x, int y) {
		
		try {
			
			terminalOutputStream.write(CSI);
			terminalOutputStream.write('1');
			terminalOutputStream.write('9');
			terminalOutputStream.write(ASCII.SEMI_COLON);
			terminalOutputStream.write('9');
			terminalOutputStream.write(ASCII.CAPITAL_H);
			terminalOutputStream.flush();
		
			System.out.println(956/100);
			System.out.println(956/10);
			System.out.println(956/1);
			
		} catch(IOException ioe) {
			
		}
		
		//ESC[#;#H
		
	}

	@Override
	public Type getType() {
		return Type.ANSI;
	}


	
	
}

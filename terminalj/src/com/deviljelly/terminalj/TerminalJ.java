package com.deviljelly.terminalj;

import java.io.IOException;
import java.io.OutputStream;

import com.deviljelly.terminalj.terminal.Terminal;
import com.deviljelly.terminalj.terminal.TerminalFactory;

public class TerminalJ {
	
	public static void main(String[] args) {
		System.out.println("hello git world");

	
		Terminal terminal  = TerminalFactory.getTerminal();
		terminal.moveTo(1, 1);
		
	}

}

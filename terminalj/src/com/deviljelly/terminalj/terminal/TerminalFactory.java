package com.deviljelly.terminalj.terminal;

import com.deviljelly.terminalj.TerminalInputStream;
import com.deviljelly.terminalj.TerminalOutputStream;
import com.deviljelly.terminalj.terminal.terminals.ANSITerminal;

public class TerminalFactory {

		
	public static Terminal getTerminal() {
		
		return getTerminal(getEnvironmentTerminalType());
		
	}
	
	public static Terminal getTerminal(Type type) {

		Terminal terminal = null;
		
		switch (type) {
		case ANSI:
			terminal = new ANSITerminal(TerminalInputStream.getDefault(), TerminalOutputStream.getDefault());
			break;

		case VT52:
			terminal = new ANSITerminal(TerminalInputStream.getDefault(), TerminalOutputStream.getDefault());
			break;

		case VT100:
			terminal = new ANSITerminal(TerminalInputStream.getDefault(), TerminalOutputStream.getDefault());
			break;

		case XTERM:
			terminal = new ANSITerminal(TerminalInputStream.getDefault(), TerminalOutputStream.getDefault());
			break;

		case LINUX:
			terminal = new ANSITerminal(TerminalInputStream.getDefault(), TerminalOutputStream.getDefault());
			break;

		default:
			break;
		}
		
		return terminal;

	}
	
	private static Type getEnvironmentTerminalType() {
			
		Type terminalType = Type.UNKNOWN;

		String termEnvVar = System.getenv("TERM");
		
		if(termEnvVar!=null) {

			terminalType = Type.valueOf(termEnvVar.toUpperCase());
			
			if(terminalType==null) {
				terminalType = Type.UNKNOWN;
			}
			
		}
		
		return terminalType;

	}
	
}

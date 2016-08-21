package com.deviljelly.terminalj.terminal.terminals;


import com.deviljelly.terminalj.TerminalInputStream;
import com.deviljelly.terminalj.TerminalOutputStream;
import com.deviljelly.terminalj.terminal.AbstractTerminal;
import com.deviljelly.terminalj.terminal.Terminal;
import com.deviljelly.terminalj.terminal.Type;

public class VT100Terminal extends AbstractTerminal implements Terminal {

	public VT100Terminal(TerminalInputStream terminalInputStream, TerminalOutputStream terminalOutputStream) {
		super(terminalInputStream, terminalOutputStream);
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void moveTo(int x, int y) {
		// TODO Auto-generated method stub
		
	}

}

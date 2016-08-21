package com.deviljelly.terminalj.terminal.terminals;

import com.deviljelly.terminalj.TerminalInputStream;
import com.deviljelly.terminalj.TerminalOutputStream;
import com.deviljelly.terminalj.terminal.AbstractTerminal;
import com.deviljelly.terminalj.terminal.Terminal;
import com.deviljelly.terminalj.terminal.Type;

public class XTERMTerminal extends AbstractTerminal implements Terminal {

	public XTERMTerminal(TerminalInputStream terminalInputStream, TerminalOutputStream terminalOutputStream) {
		super(terminalInputStream, terminalOutputStream);
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void moveTo(int x, int y) {
		System.out.println("hello");
		
	}
}

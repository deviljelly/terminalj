package com.deviljelly.terminalj.terminal;

import com.deviljelly.terminalj.TerminalInputStream;
import com.deviljelly.terminalj.TerminalOutputStream;

public class AbstractTerminal {

	protected TerminalInputStream terminalInputStream;
	protected TerminalOutputStream terminalOutputStream;
	
	public AbstractTerminal(TerminalInputStream terminalInputStream, TerminalOutputStream terminalOutputStream) {
		this.terminalInputStream = terminalInputStream;
		this.terminalOutputStream = terminalOutputStream;
	}
	
}

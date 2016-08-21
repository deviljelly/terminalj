package com.deviljelly.terminalj.terminal;

import com.deviljelly.terminalj.TerminalInputStream;
import com.deviljelly.terminalj.TerminalOutputStream;

public interface Terminal {

	public Type getType();
	
	public void moveTo(int x, int y);
	

}

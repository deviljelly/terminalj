package com.deviljelly.terminalj.terminal;

import com.deviljelly.terminalj.TerminalInputStream;
import com.deviljelly.terminalj.TerminalOutputStream;

public interface Terminal {

	public Type getType();
	
	public void moveTo(int x, int y);

	public void clearScreen();
	
	public void write(byte character);

	public void write(String string);
	
	public void flush();

	public void hideCursor();
	
	public void showCursor();
	
	public void setForgroundColor(Color color, Intensity intensity);

	public void setBackgroundColor(Color color, Intensity intensity);
	
}

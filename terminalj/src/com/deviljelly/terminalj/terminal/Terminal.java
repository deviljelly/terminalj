package com.deviljelly.terminalj.terminal;

public interface Terminal {

	public Type getType();
	
	public void cursorTo(int row, int column);

	public void eraseFullDisplay();
	
	public void eraseDisplayToEnd();
	
	public void eraseDisplayToBeginning();
	
	public void write(byte character);

	public void write(String string);
	
	public void flush();

	public void hideCursor();
	
	public void showCursor();
	
	public void setForgroundColor(Color color, Intensity intensity);

	public void setBackgroundColor(Color color, Intensity intensity);
	
	public void cursorUp(int lines);

	public void cursorDown(int lines);
	
	public void cursorForward(int columns);

	public void cursorBack(int columns);

	public void cursorNextLine(int lines);
	
	public void cursorPreviousLine(int lines);

	public void scrollUp(int lines);

	public void scrollDown(int lines);
	
	public DeviceStatus deviceStatusReport();
	
}

package com.deviljelly.terminalj.terminal.terminals;

import com.deviljelly.terminalj.TerminalInputStream;
import com.deviljelly.terminalj.TerminalOutputStream;
import com.deviljelly.terminalj.terminal.AbstractTerminal;
import com.deviljelly.terminalj.terminal.Color;
import com.deviljelly.terminalj.terminal.Intensity;
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
	@Override
	public void write(byte character) {

	}

	@Override
	public void flush() {
		
	}

	@Override
	public void clearScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideCursor() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showCursor() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setForgroundColor(Color color, Intensity intensity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBackgroundColor(Color color, Intensity intensity) {
		// TODO Auto-generated method stub
		
	}

	
}

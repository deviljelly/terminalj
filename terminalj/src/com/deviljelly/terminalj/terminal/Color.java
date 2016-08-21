package com.deviljelly.terminalj.terminal;

public enum Color {
	
	BLACK(0), RED(1), GREEN(2), YELLOW(3), BLUE(4), MAGENTA(5), CYAN(6), WHITE(7), DEFAULT(9), FOREGROUND(30), BACKGROUND(40);
	
	private int value;
	 
	private Color(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}

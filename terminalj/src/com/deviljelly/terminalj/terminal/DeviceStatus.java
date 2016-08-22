package com.deviljelly.terminalj.terminal;

public class DeviceStatus {

	private int column;
	private int row;
	
	public DeviceStatus(int column, int row) {
		this.column = column;
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}
	
}

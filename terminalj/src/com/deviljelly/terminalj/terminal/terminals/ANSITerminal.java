package com.deviljelly.terminalj.terminal.terminals;


import java.io.IOException;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import com.deviljelly.terminalj.TerminalInputStream;
import com.deviljelly.terminalj.TerminalOutputStream;
import com.deviljelly.terminalj.terminal.AbstractTerminal;
import com.deviljelly.terminalj.terminal.Color;
import com.deviljelly.terminalj.terminal.Constants;
import com.deviljelly.terminalj.terminal.Intensity;
import com.deviljelly.terminalj.terminal.Terminal;
import com.deviljelly.terminalj.terminal.Type;
import com.deviljelly.terminalj.terminal.Utils;
import com.deviljelly.terminalj.terminal.codepage.ASCII;

public class ANSITerminal extends AbstractTerminal implements Terminal {
	

	public ANSITerminal(TerminalInputStream terminalInputStream, TerminalOutputStream terminalOutputStream) {
		super(terminalInputStream, terminalOutputStream);
	}

	@Override
	public void moveTo(int x, int y) {
		
		try {
			
			byte[] workArray = new byte[24];
			
			int pointer = 0;

			workArray[pointer++] = ASCII.ESC;
			workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;
			pointer += Utils.intToString(x, workArray, pointer);
			workArray[pointer++] = ASCII.SEMI_COLON;
			pointer += Utils.intToString(y, workArray, pointer);
			workArray[pointer++] = ASCII.CAPITAL_H;
			
			terminalOutputStream.write(workArray, 0, pointer);
			
		} catch(IOException ioe) {
			
		}

	}

	@Override
	public Type getType() {
		return Type.ANSI;
	}

	@Override
	public void write(byte character) {
		
		try {

			terminalOutputStream.write(character);
			
		} catch(IOException ioe) {
			
		}
		
	}

	
	@Override
	public void flush() {
		try {

			terminalOutputStream.flush();
			
		} catch(IOException ioe) {
			
		}
		
	}

	@Override
	public void clearScreen() {
		
	try {
			
			byte[] workArray = new byte[4];
			
			workArray[0] = ASCII.ESC;
			workArray[1] = ASCII.LEFT_SQUARE_BRACKET;
			workArray[2] = ASCII.TWO;
			workArray[3] = ASCII.CAPITAL_J;
			
			terminalOutputStream.write(workArray, 0, 4);
			
		} catch(IOException ioe) {
			
		}
		
	}

	@Override
	public void write(String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideCursor() {
		
	try {
			
			byte[] workArray = new byte[6];
			
			workArray[0] = ASCII.ESC;
			workArray[1] = ASCII.LEFT_SQUARE_BRACKET;
			workArray[2] = ASCII.QUESTION_MARK;
			workArray[3] = ASCII.TWO;
			workArray[4] = ASCII.FIVE;
			workArray[5] = ASCII.LOWER_CASE_L;
			
			terminalOutputStream.write(workArray, 0, 6);
			
		} catch(IOException ioe) {
			
		}
		
	}

	@Override
	public void showCursor() {
		
	try {
			
			byte[] workArray = new byte[6];
			
			workArray[0] = ASCII.ESC;
			workArray[1] = ASCII.LEFT_SQUARE_BRACKET;
			workArray[2] = ASCII.QUESTION_MARK;
			workArray[3] = ASCII.TWO;
			workArray[4] = ASCII.FIVE;
			workArray[5] = ASCII.LOWER_CASE_H;
			
			terminalOutputStream.write(workArray, 0, 6);
			
		} catch(IOException ioe) {
			
		}
		
	}

	@Override
	public void setForgroundColor(Color color, Intensity intensity) {

		try {

			if (intensity == Intensity.BRIGHT) {

				byte[] workArray = new byte[24];
				int pointer = 0;

				workArray[pointer++] = ASCII.ESC;
				workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

				pointer += Utils.intToString(Color.FOREGROUND.getValue() + color.getValue(), workArray, pointer);

				workArray[pointer++] = ASCII.SEMI_COLON;
				workArray[pointer++] = ASCII.ONE;
				workArray[pointer++] = ASCII.LOWER_CASE_M;

				terminalOutputStream.write(workArray, 0, pointer);

			} else {

				
				byte[] workArray = new byte[24];
				int pointer = 0;

				workArray[pointer++] = ASCII.ESC;
				workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

				pointer += Utils.intToString(Color.FOREGROUND.getValue()  + color.getValue(), workArray, pointer);

				workArray[pointer++] = ASCII.LOWER_CASE_M;

				terminalOutputStream.write(workArray, 0, pointer);

				
			}

		} catch (IOException ioe) {

		}
		
	}

	@Override
	public void setBackgroundColor(Color color, Intensity intensity) {

		try {

			if (intensity == Intensity.BRIGHT) {

				byte[] workArray = new byte[24];
				int pointer = 0;

				workArray[pointer++] = ASCII.ESC;
				workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

				pointer += Utils.intToString(Color.BACKGROUND.getValue() + color.getValue(), workArray, pointer);

				workArray[pointer++] = ASCII.SEMI_COLON;
				workArray[pointer++] = ASCII.ONE;
				workArray[pointer++] = ASCII.LOWER_CASE_M;

				terminalOutputStream.write(workArray, 0, pointer);

			} else {

				
				byte[] workArray = new byte[24];
				int pointer = 0;

				workArray[pointer++] = ASCII.ESC;
				workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

				pointer += Utils.intToString(Color.BACKGROUND.getValue()  + color.getValue(), workArray, pointer);

				workArray[pointer++] = ASCII.LOWER_CASE_M;

				terminalOutputStream.write(workArray, 0, pointer);

				
			}

		} catch (IOException ioe) {

		}
		
	}

}

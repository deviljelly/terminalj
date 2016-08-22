package com.deviljelly.terminalj.terminal.terminals;


import java.io.IOException;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import com.deviljelly.terminalj.TerminalInputStream;
import com.deviljelly.terminalj.TerminalOutputStream;
import com.deviljelly.terminalj.terminal.AbstractTerminal;
import com.deviljelly.terminalj.terminal.Color;
import com.deviljelly.terminalj.terminal.Constants;
import com.deviljelly.terminalj.terminal.DeviceStatus;
import com.deviljelly.terminalj.terminal.Intensity;
import com.deviljelly.terminalj.terminal.Terminal;
import com.deviljelly.terminalj.terminal.Type;
import com.deviljelly.terminalj.terminal.Utils;
import com.deviljelly.terminalj.terminal.codepage.ASCII;

public class ANSITerminal extends AbstractTerminal implements Terminal {
	



	public ANSITerminal(TerminalInputStream terminalInputStream, TerminalOutputStream terminalOutputStream) {
		super(terminalInputStream, terminalOutputStream);
	}

	
	//BOX drawing The escape sequence Esc ( 0 switched the codes for lower-case ASCII letters to draw this set, and the sequence Esc ( B switched back:
	//
	

//0	1	2	3	4	5	6	7	8	9	A	B	C	D	E	F
//6											┘	┐	┌	└	┼	
//7		─			├	┤	┴	┬	│							

	/* UNICODE
	 * 
	 *  	0	1	2	3	4	5	6	7	8	9	A	B	C	D	E	F
U+250x		─	━	│	┃	┄	┅	┆	┇	┈	┉	┊	┋	┌	┍	┎	┏
U+251x		┐	┑	┒	┓	└	┕	┖	┗	┘	┙	┚	┛	├	┝	┞	┟
U+252x		┠	┡	┢	┣	┤	┥	┦	┧	┨	┩	┪	┫	┬	┭	┮	┯
U+253x		┰	┱	┲	┳	┴	┵	┶	┷	┸	┹	┺	┻	┼	┽	┾	┿
U+254x		╀	╁	╂	╃	╄	╅	╆	╇	╈	╉	╊	╋	╌	╍	╎	╏
U+255x		═	║	╒	╓	╔	╕	╖	╗	╘	╙	╚	╛	╜	╝	╞	╟
U+256x		╠	╡	╢	╣	╤	╥	╦	╧	╨	╩	╪	╫	╬	╭	╮	╯
U+257x		╰	╱	╲	╳	╴	╵	╶	╷	╸	╹	╺	╻	╼	╽	╾	╿
	 * 
	 */
	
	@Override
	public void cursorTo(int row, int column) {


		byte[] workArray = new byte[24];

		int pointer = 0;

		workArray[pointer++] = ASCII.ESC;
		workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;
		pointer += Utils.intToString(row, workArray, pointer);
		workArray[pointer++] = ASCII.SEMI_COLON;
		pointer += Utils.intToString(column, workArray, pointer);
		workArray[pointer++] = ASCII.CAPITAL_H;

		try {

			terminalOutputStream.write(workArray, 0, pointer);

		} catch (IOException ioe) {

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

		} catch (IOException ioe) {

		}

	}

	@Override
	public void flush() {

		try {

			terminalOutputStream.flush();

		} catch (IOException ioe) {

		}

	}


	@Override
	public void write(String string) {

		try {

			terminalOutputStream.write(string.getBytes());
	
		} catch (IOException ioe) {
	
		}

	}

	
	@Override
	public void hideCursor() {

		byte[] workArray = new byte[6];

		workArray[0] = ASCII.ESC;
		workArray[1] = ASCII.LEFT_SQUARE_BRACKET;
		workArray[2] = ASCII.QUESTION_MARK;
		workArray[3] = ASCII.TWO;
		workArray[4] = ASCII.FIVE;
		workArray[5] = ASCII.LOWER_CASE_L;

		try {

			terminalOutputStream.write(workArray, 0, 6);

		} catch (IOException ioe) {

		}

	}

	
	@Override
	public void showCursor() {

		byte[] workArray = new byte[6];

		workArray[0] = ASCII.ESC;
		workArray[1] = ASCII.LEFT_SQUARE_BRACKET;
		workArray[2] = ASCII.QUESTION_MARK;
		workArray[3] = ASCII.TWO;
		workArray[4] = ASCII.FIVE;
		workArray[5] = ASCII.LOWER_CASE_H;

		try {

			terminalOutputStream.write(workArray, 0, 6);

		} catch (IOException ioe) {

		}

	}

	
	@Override
	public void setForgroundColor(Color color, Intensity intensity) {


		byte[] workArray = new byte[24];
		int pointer = 0;
		
		workArray[pointer++] = ASCII.ESC;
		workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

		if (intensity == Intensity.BRIGHT) {

			pointer += Utils.intToString(Color.FOREGROUND.getValue() + color.getValue(), workArray, pointer);
			workArray[pointer++] = ASCII.SEMI_COLON;
			workArray[pointer++] = ASCII.ONE;

		} else {

			pointer += Utils.intToString(Color.FOREGROUND.getValue() + color.getValue(), workArray, pointer);

		}

		workArray[pointer++] = ASCII.LOWER_CASE_M;

		try {
			
			terminalOutputStream.write(workArray, 0, pointer);

		} catch (IOException ioe) {

		}

	}

	
	@Override
	public void setBackgroundColor(Color color, Intensity intensity) {

		byte[] workArray = new byte[24];
		int pointer = 0;

		workArray[pointer++] = ASCII.ESC;
		workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

		if (intensity == Intensity.BRIGHT) {

			pointer += Utils.intToString(Color.BACKGROUND.getValue() + color.getValue(), workArray, pointer);

			workArray[pointer++] = ASCII.SEMI_COLON;
			workArray[pointer++] = ASCII.ONE;

			} else {

			pointer += Utils.intToString(Color.BACKGROUND.getValue() + color.getValue(), workArray, pointer);


		}

		workArray[pointer++] = ASCII.LOWER_CASE_M;

		try {

			terminalOutputStream.write(workArray, 0, pointer);

		} catch (IOException ioe) {

		}

	}

	
	@Override
	public void cursorUp(int lines) {

		byte[] workArray = new byte[16];

		int pointer = 0;

		workArray[pointer++] = ASCII.ESC;
		workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

		pointer += Utils.intToString(lines, workArray, pointer);

		workArray[pointer++] = ASCII.CAPITAL_A;

		try {

			terminalOutputStream.write(workArray, 0, 6);

		} catch (IOException ioe) {

		}

	}

	
	@Override
	public void cursorDown(int lines) {

		byte[] workArray = new byte[16];

		int pointer = 0;

		workArray[pointer++] = ASCII.ESC;
		workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

		pointer += Utils.intToString(lines, workArray, pointer);

		workArray[pointer++] = ASCII.CAPITAL_B;

		try {

			terminalOutputStream.write(workArray, 0, 6);

		} catch (IOException ioe) {

		}

	}

	
	@Override
	public void cursorForward(int columns) {

		byte[] workArray = new byte[16];

		int pointer = 0;

		workArray[pointer++] = ASCII.ESC;
		workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

		pointer += Utils.intToString(columns, workArray, pointer);

		workArray[pointer++] = ASCII.CAPITAL_C;

		try {

			terminalOutputStream.write(workArray, 0, 6);

		} catch (IOException ioe) {

		}

	}

	
	@Override
	public void cursorBack(int columns) {

		byte[] workArray = new byte[16];

		int pointer = 0;

		workArray[pointer++] = ASCII.ESC;
		workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

		pointer += Utils.intToString(columns, workArray, pointer);

		workArray[pointer++] = ASCII.CAPITAL_D;

		try {

			terminalOutputStream.write(workArray, 0, 6);

		} catch (IOException ioe) {

		}

	}

	
	@Override
	public void cursorNextLine(int lines) {

		byte[] workArray = new byte[16];

		int pointer = 0;

		workArray[pointer++] = ASCII.ESC;
		workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

		pointer += Utils.intToString(lines, workArray, pointer);

		workArray[pointer++] = ASCII.CAPITAL_E;

		try {

			terminalOutputStream.write(workArray, 0, 6);

		} catch (IOException ioe) {

		}
	}

	
	@Override
	public void cursorPreviousLine(int lines) {

		byte[] workArray = new byte[16];

		int pointer = 0;

		workArray[pointer++] = ASCII.ESC;
		workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

		pointer += Utils.intToString(lines, workArray, pointer);

		workArray[pointer++] = ASCII.CAPITAL_F;

		try {

			terminalOutputStream.write(workArray, 0, 6);

		} catch (IOException ioe) {

		}

	}


	@Override
	public void eraseFullDisplay() {

		byte[] workArray = new byte[4];

		workArray[0] = ASCII.ESC;
		workArray[1] = ASCII.LEFT_SQUARE_BRACKET;
		workArray[2] = ASCII.TWO;
		workArray[3] = ASCII.CAPITAL_J;

		try {
			
			terminalOutputStream.write(workArray, 0, 4);

		} catch (IOException ioe) {

		}
		
	}


	@Override
	public void eraseDisplayToEnd() {

		byte[] workArray = new byte[4];

		workArray[0] = ASCII.ESC;
		workArray[1] = ASCII.LEFT_SQUARE_BRACKET;
		workArray[2] = ASCII.ZERO;
		workArray[3] = ASCII.CAPITAL_J;

		try {
			
			terminalOutputStream.write(workArray, 0, 4);

		} catch (IOException ioe) {

		}
		
	}


	@Override
	public void eraseDisplayToBeginning() {

		byte[] workArray = new byte[4];

		workArray[0] = ASCII.ESC;
		workArray[1] = ASCII.LEFT_SQUARE_BRACKET;
		workArray[2] = ASCII.ONE;
		workArray[3] = ASCII.CAPITAL_J;

		try {
			
			terminalOutputStream.write(workArray, 0, 4);

		} catch (IOException ioe) {

		}
		
	}


	@Override
	public void scrollUp(int lines) {

		byte[] workArray = new byte[16];

		int pointer = 0;

		workArray[pointer++] = ASCII.ESC;
		workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

		pointer += Utils.intToString(lines, workArray, pointer);

		workArray[pointer++] = ASCII.CAPITAL_S;

		try {

			terminalOutputStream.write(workArray, 0, 6);

		} catch (IOException ioe) {

		}
		
	}


	@Override
	public void scrollDown(int lines) {

		byte[] workArray = new byte[16];

		int pointer = 0;

		workArray[pointer++] = ASCII.ESC;
		workArray[pointer++] = ASCII.LEFT_SQUARE_BRACKET;

		pointer += Utils.intToString(lines, workArray, pointer);

		workArray[pointer++] = ASCII.CAPITAL_T;

		try {

			terminalOutputStream.write(workArray, 0, 6);

		} catch (IOException ioe) {

		}
		
	}

	
	@Override
	public DeviceStatus deviceStatusReport() {

		byte[] workArray = new byte[4];

		workArray[0] = ASCII.ESC;
		workArray[1] = ASCII.LEFT_SQUARE_BRACKET;
		workArray[2] = ASCII.SIX;
		workArray[3] = ASCII.LOWER_CASE_N;

		try {

			terminalOutputStream.write(workArray, 0, 4);

		} catch (IOException ioe) {

		}

		// ESC[n;mR
		
		
		
		workArray = new byte[6];
		
		try {

			int pointer = 0;
			byte theByte = (byte)terminalInputStream.read();
			while(theByte!=ASCII.CAPITAL_R) {
				System.err.println(theByte);
				workArray[pointer++] = theByte;
				theByte = (byte)terminalInputStream.read();
			}
			
//			theByte = (byte)terminalInputStream.read();
//			int read = terminalInputStream.read(workArray, 0, 6);
//			System.out.println(read);
			
			

		} catch (IOException ioe) {

		}
		
		return null;

	}
	
}

package VASSAL.build.module;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import VASSAL.command.Command;
import VASSAL.tools.WriteErrorDialog;

public class TextLogger extends BasicLogger {

	File textFile = null;
	PrintWriter textFileWriter = null;
		

	@Override
	protected void beginOutput() {
		super.beginOutput();
		
		// Change VLOG filename to TXT
		int index = outputFile.getName().lastIndexOf(".");
		String name = outputFile.getName().substring(0,index);
		textFile = new File(outputFile.getParent(), name + ".txt");
		try {
			textFileWriter = new PrintWriter(textFile);
		} catch (IOException ex) {

	        WriteErrorDialog.error(ex, textFile);
		}
	}

	@Override
	public void log(Command c) {
		super.log(c);
		
		// Turn the command into a more readable text string and prepend a date/time
		String commandText = c.toString();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss+");
		commandText = dateFormat.format(new Date()) + commandText;
		
		// Write to the text file
		textFileWriter.println(commandText);
		textFileWriter.flush();
	}

	@Override
	public void setup(boolean startingGame) {
		super.setup(startingGame);
		
		// Close the existing files
		textFileWriter = null;
		textFile = null;
	}
	
	
}

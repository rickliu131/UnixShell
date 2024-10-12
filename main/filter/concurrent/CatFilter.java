package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.brandeis.cs.cs131.pa2.filter.CurrentWorkingDirectory;
import edu.brandeis.cs.cs131.pa2.filter.Filter;
import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * Implements cat command - includes parsing cat command, detecting if input
 * filter was linked, as well as overriding necessary behavior of
 * SequentialFilter.
 * 
 * By Yuxuan Liu
 *
 */
public class CatFilter extends ConcurrentFilter {

	/**
	 * file to be read
	 */
	private File file;

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * Constructs a CatFilter given a cat command.
	 * 
	 * @param cmd cmd is guaranteed to either be "cat" or "cat" followed by a space.
	 * @throws IllegalArgumentException if the file in the command cannot be found
	 *                                  or if a file parameter was not provided
	 */
	public CatFilter(String cmd) {
		super();

		// save command as a field, we need it when we throw an exception in
		// setPrevFilter
		command = cmd;

		// find index of space, if there isn't a space that means we got just "cat" =>
		// cat needs a parameter so throw IAE with the appropriate message
		int spaceIdx = cmd.indexOf(" ");
		if (spaceIdx == -1) {
			throw new IllegalArgumentException(Message.REQUIRES_PARAMETER.with_parameter(cmd));
		}

		// we have a space, filename will be trimmed string after space
		String dest = cmd.substring(spaceIdx + 1).trim();

		// create a File with the path to the file from the current working directory
		// since we interpret dest as a relative path
		file = new File(CurrentWorkingDirectory.get() + CurrentWorkingDirectory.FILE_SEPARATOR + dest);

		// if this is not a valid File, throw an IAE with the appropriate message
		if (!file.isFile()) {
			throw new IllegalArgumentException(Message.FILE_NOT_FOUND.with_parameter(cmd));
		}
	}

	/**
	 * Overrides SequentialFilterprocessLine() - doesn't do anything.
	 */
	@Override
	protected String processLine(String line) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overrides SequentialFilter.setPrevFilter() to not allow a
	 * {@link Filter} to be placed before {@link CatFilter} objects.
	 * 
	 * @throws IllegalArgumentException - always
	 */
	@Override
	public void setPrevFilter(Filter prevFilter) {

		// as specified in the PDF throw an IAE with the appropriate message if we try
		// to link a Filter before this one (since cat doesn't take input)
		throw new IllegalArgumentException(Message.CANNOT_HAVE_INPUT.with_parameter(command));

	}

	@Override
	/**
	 * Used by Java Thread to run the filter concurrently 
	 * This replaces process() 
	 */
	public void run() {
		try {
			Scanner s = new Scanner(file);
			while (s.hasNextLine()) {
				this.output.writeAndWait(s.nextLine());
			}
			s.close();
			this.output.writePoisonPill();
		} catch (FileNotFoundException e) {}
		  catch (InterruptedException e) {
			 try {
				this.output.writePoisonPill();
			} catch (InterruptedException e1) {}
		}
	}
}

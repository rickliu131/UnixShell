package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import java.io.File;

import edu.brandeis.cs.cs131.pa2.filter.CurrentWorkingDirectory;
import edu.brandeis.cs.cs131.pa2.filter.Filter;
import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * Implements cd command - includes parsing cd command, detecting if input or
 * output filter was linked, as well as overriding necessary behavior of
 * SequentialFilter.
 * 
 * By Yuxuan Liu
 *
 */
public class ChangeDirectoryFilter extends ConcurrentFilter {

	/**
	 * absolute path to directory cd will cause cwd to be changed to
	 */
	private String dest;

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * Constructs a ChangeDirectoryFilter given a cd command.
	 * 
	 * @param cmd cmd is guaranteed to either be "cd" or "cd" followed by a space.
	 * @throws IllegalArgumentException if the directory in the command cannot be
	 *                                  found or if a directory parameter was not
	 *                                  provided
	 */
	public ChangeDirectoryFilter(String cmd) {
		super();

		// save command as a field, we need it when we throw an exception in
		// setPrevFilter and setNextFilter
		command = cmd;

		// find index of space, if there isn't a space that means we got just "cd" =>
		// cd needs a parameter so throw IAE with the appropriate message
		int spaceIdx = cmd.indexOf(" ");
		if (spaceIdx == -1) {
			throw new IllegalArgumentException(Message.REQUIRES_PARAMETER.with_parameter(cmd));
		}

		// we have a space, directory will be trimmed string after space
		String relativeDest = cmd.substring(spaceIdx + 1).trim();

		// if we have a non-special destination directory, append it to cwd and set it
		// to dest
		if (!relativeDest.equals(".") && !relativeDest.equals("..")) {
			dest = CurrentWorkingDirectory.get() + CurrentWorkingDirectory.FILE_SEPARATOR + relativeDest;

			// make sure that this is a valid directory, if not throw appropriate IAE
			File destFile = new File(dest);
			if (!destFile.isDirectory()) {
				throw new IllegalArgumentException(Message.DIRECTORY_NOT_FOUND.with_parameter(cmd));
			}

			// if specified relative destination is . or .., just set that as dest
		} else {
			dest = relativeDest;
		}

	}

	/**
	 * Overrides SequentialFilter.processLine() - doesn't do anything.
	 */
	@Override
	protected String processLine(String line) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overrides SequentialFilter.setPrevFilter() to not allow a {@link Filter} to
	 * be placed before {@link ChangeDirectoryFilter} objects.
	 * 
	 * @throws IllegalArgumentException - always
	 */
	@Override
	public void setPrevFilter(Filter prevFilter) {
		// as specified in the PDF throw an IAE with the appropriate message if we try
		// to link a Filter before this one (since cd doesn't take input)
		throw new IllegalArgumentException(Message.CANNOT_HAVE_INPUT.with_parameter(command));
	}

	/**
	 * Overrides SequentialFilter.setNextFilter() to not allow a {@link Filter} to
	 * be placed after {@link ChangeDirectoryFilter} objects.
	 * 
	 * @throws IllegalArgumentException - always
	 */
	@Override
	public void setNextFilter(Filter nextFilter) {
		// as specified in the PDF throw an IAE with the appropriate message if we try
		// to link a Filter after this one (since cd doesn't make output)
		throw new IllegalArgumentException(Message.CANNOT_HAVE_OUTPUT.with_parameter(command));
	}

	@Override
	/**
	 * Used by Java Thread to run the filter concurrently 
	 * This replaces process() 
	 */
	public void run() {
		//already made sure that there won't be any input/output
		try {
			if (dest.equals("..")) {
				String parent = new File(CurrentWorkingDirectory.get()).getParent();
				if (parent != null) {
					CurrentWorkingDirectory.setTo(parent);
				}
			} else if (!dest.equals(".")) {
				CurrentWorkingDirectory.setTo(dest);
			}
			//no output made
			this.output.writePoisonPill(); //normal one
		} catch (InterruptedException e) {
			//kill
			try {
				this.output.writePoisonPill(); //interrupt one
			} catch (InterruptedException e1) {}
		}
	}
}

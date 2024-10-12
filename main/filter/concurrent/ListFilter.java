package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import java.io.File;

import edu.brandeis.cs.cs131.pa2.filter.CurrentWorkingDirectory;
import edu.brandeis.cs.cs131.pa2.filter.Filter;
import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * Implements ls command - overrides necessary behavior of SequentialFilter
 * 
 * By Yuxuan Liu
 *
 */
public class ListFilter extends ConcurrentFilter {

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * Constructs an ListFilter from an exit command
	 * 
	 * @param cmd - exit command, will be "ls" or "ls" surrounded by whitespace
	 */
	public ListFilter(String cmd) {
		super();
		command = cmd;
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
	 * Overrides SequentialFilter.setPrevFilter() to not allow a
	 * {@link Filter} to be placed before {@link ListFilter} objects.
	 * 
	 * @throws IllegalArgumentException - always
	 */
	@Override
	public void setPrevFilter(Filter prevFilter) {
		throw new IllegalArgumentException(Message.CANNOT_HAVE_INPUT.with_parameter(command));
	}

	@Override
	/**
	 * Used by Java Thread to run the filter concurrently 
	 * This replaces process() 
	 */
	public void run() {
		//no input
		try {
			File cwd = new File(CurrentWorkingDirectory.get());
			File[] files = cwd.listFiles();
			for (File f : files) {
				this.output.writeAndWait(f.getName());
			}
			this.output.writePoisonPill();
		} catch (InterruptedException e) {
			try {
				this.output.writePoisonPill();
			} catch (InterruptedException e2) {}
		}
		
	}

}

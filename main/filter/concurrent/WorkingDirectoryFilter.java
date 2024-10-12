package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import edu.brandeis.cs.cs131.pa2.filter.CurrentWorkingDirectory;
import edu.brandeis.cs.cs131.pa2.filter.Filter;
import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * Implements pwd command - overrides necessary behavior of SequentialFilter
 * 
 * By Yuxuan Liu
 *
 */
public class WorkingDirectoryFilter extends ConcurrentFilter {

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * Constructs a pwd filter.
	 * @param cmd cmd is guaranteed to either be "pwd" or "pwd" surrounded by whitespace
	 */
	public WorkingDirectoryFilter(String cmd) {
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
	 * Overrides equentialFilter.setPrevFilter() to not allow a {@link Filter} to be
	 * placed before {@link WorkingDirectoryFilter} objects.
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
		try {
			this.output.writeAndWait(CurrentWorkingDirectory.get());
			this.output.writePoisonPill();
		} catch (InterruptedException e) {
			try {
				this.output.writePoisonPill();
			} catch (InterruptedException e1) {}
		}
	}
}

package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * Implements grep command - includes parsing grep command by overriding
 * necessary behavior of SequentialFilter.
 * 
 * By Yuxuan Liu
 *
 */
public class GrepFilter extends ConcurrentFilter {

	/**
	 * holds the grep query
	 */
	private String query;

	/**
	 * constructs GrepFilter given grep command
	 * 
	 * @param cmd cmd is guaranteed to either be "grep" or "grep" followed by a
	 *            space.
	 * @throws IllegalArgumentException if query parameter was not provided
	 */
	public GrepFilter(String cmd) {

		// find index of space, if there isn't a space that means we got just "grep" =>
		// grep needs a parameter so throw IAE with the appropriate message
		int spaceIdx = cmd.indexOf(" ");
		if (spaceIdx == -1) {
			throw new IllegalArgumentException(Message.REQUIRES_PARAMETER.with_parameter(cmd));
		}

		// we have a space, query will be trimmed string after space
		query = cmd.substring(spaceIdx + 1).trim();
	}

	/**
	 * Overrides  SequentialFilter.processLine() - only returns lines to
	 * {@link SequentialFilter#process()} that contain the query parameter specified
	 * in the command passed to the constructor.
	 */
	@Override
	protected String processLine(String line) {

		// only have SequentialFilter:process() add lines to the output queue that
		// include the query string
		if (line.contains(query)) {
			return line;
		}

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * Used by Java Thread to run the filter concurrently 
	 * This replaces process() 
	 */
	public void run() {
		while (true) {
			try {
				String line = this.input.readAndWait();
				if (line == null) {
					//receive poison pill
					this.output.writePoisonPill();
					break;
				} else {
					String processedLine = processLine(line); 
					//be careful of the null value here, if it's null it doesn't mean it's a poison pill! It's different!
					if (processedLine != null) {
						this.output.writeAndWait(processedLine);
					}
					continue;
				}
			} catch (InterruptedException e) {
				try {
					this.output.writePoisonPill();
				} catch (InterruptedException e1) {}
			}
		}
	}
	
}

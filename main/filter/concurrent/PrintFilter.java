package edu.brandeis.cs.cs131.pa2.filter.concurrent;


/**
 * Implements printing as a {@link SequentialFilter} - overrides necessary
 * behavior of SequentialFilter
 * 
 * By Yuxuan Liu
 *
 */
public class PrintFilter extends ConcurrentFilter {

	/**
	 * Overrides SequentialFilter.processLine() to just print the line to stdout.
	 */
	@Override
	protected String processLine(String line) {

		System.out.println(line);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * Used by Java Thread to run the filter concurrently
	 * Print out result
	 */
	public void run() {
		while (true) {
			try {
				String line = this.input.readAndWait();
				if (line == null) {
					break;
				} else {
					processLine(line);
					continue;
				}
			} catch (InterruptedException e) {}
		}
	}

}

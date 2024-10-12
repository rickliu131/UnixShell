package edu.brandeis.cs.cs131.pa2.filter.concurrent;

/**
 * Implements uniq command - overrides necessary behavior of SequentialFilter
 * 
 * By Yuxuan Liu
 *
 */
public class UniqFilter extends ConcurrentFilter {

	/**
	 * Stores previous line
	 */
	private String prevLine;

	/**
	 * Constructs a uniq filter.
	 */
	public UniqFilter() {
		super();
		prevLine = null;
	}

	/**
	 * Overrides SequentialFilter.processLine() - only returns lines to
	 * {@link SequentialFilter#process()} that are not repetitions of the previous
	 * line.
	 */
	@Override
	protected String processLine(String line) {
		String output = null;
		if (prevLine == null || !prevLine.equals(line)) {
			output = line;
		}

		prevLine = line;
		return output;
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

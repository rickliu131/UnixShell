package edu.brandeis.cs.cs131.pa2.filter.concurrent;


/**
 * Implements head command - overrides necessary behavior of SequentialFilter
 * 
 * By Yuxuan Liu
 *
 */
public class HeadFilter extends ConcurrentFilter {

	/**
	 * number of lines read so far
	 */
	private int numRead;

	/**
	 * number of lines passed to output via head
	 */
	private static int LIMIT = 10;

	/**
	 * Constructs a head filter.
	 */
	public HeadFilter() {
		super();
		numRead = 0;
	}

	/**
	 * Overrides SequentialFilter.processLine() - doesn't do anything.
	 */
	@Override
	protected String processLine(String line) {
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
				if (this.numRead >= LIMIT) {
					this.output.writePoisonPill();
					break;
				} else {
					String line = this.input.readAndWait();
					if (line == null) {
						//poison pill
						this.output.writePoisonPill();
						break;
					} else {
						this.output.writeAndWait(line);
						this.numRead++;
						continue;
					}
				}
			} catch (InterruptedException e) {
				try {
					this.output.writePoisonPill();
				} catch (InterruptedException e1) {}
			}
		}
	}
}

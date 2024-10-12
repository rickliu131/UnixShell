package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements tail command - overrides necessary behavior of SequentialFilter
 * 
 * By Yuxuan Liu
 *
 */
public class TailFilter extends ConcurrentFilter {

	/**
	 * number of lines passed to output via tail
	 */
	private static int LIMIT = 10;

	/**
	 * line buffer
	 */
	private List<String> buf;

	/**
	 * Constructs a tail filter.
	 */
	public TailFilter() {
		super();
		buf = new LinkedList<String>();
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
				String line = this.input.readAndWait();
				if (line == null) {
					//finally reach the poison pill!
					//start
					while (!this.buf.isEmpty()) {
						this.output.writeAndWait(this.buf.remove(0));
					} //would take some time for the input side to consume all...
					this.output.writePoisonPill();
					break;
				} else {
					this.buf.add(line);
					if (this.buf.size() > LIMIT) {
						this.buf.remove(0);
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

package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import edu.brandeis.cs.cs131.pa2.filter.Filter;

/**
 * An abstract class that extends the Filter and implements the basic
 * functionality of all filters. Each filter should extend this class and
 * implement functionality that is specific for this filter.
 * 
 * By Yuxuan Liu
 */
public abstract class ConcurrentFilter extends Filter implements Runnable {
	/**
	 * The input pipe for this filter
	 */
	protected ConcurrentPipe input;
	/**
	 * The output pipe for this filter
	 */
	protected ConcurrentPipe output;

	@Override
	public void setPrevFilter(Filter prevFilter) {
		prevFilter.setNextFilter(this);
	}

	@Override
	public void setNextFilter(Filter nextFilter) {
		if (nextFilter instanceof ConcurrentFilter) {
			ConcurrentFilter concurrentNext = (ConcurrentFilter) nextFilter;
			this.next = concurrentNext;
			concurrentNext.prev = this;
			if (this.output == null) {
				this.output = new ConcurrentPipe();
			}
			concurrentNext.input = this.output;
		} else {
			throw new RuntimeException("Should not attempt to link dissimilar filter types.");
		}
	}

	/**
	 * Called by the {@link #process()} method for every encountered line in the
	 * input queue. It then performs the processing specific for each filter and
	 * returns the result. Each filter inheriting from this class must implement its
	 * own version of processLine() to take care of the filter-specific processing.
	 * 
	 * @param line the line got from the input queue
	 * @return the line after the filter-specific processing
	 */
	protected abstract String processLine(String line);


}

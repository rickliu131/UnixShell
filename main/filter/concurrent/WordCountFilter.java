package edu.brandeis.cs.cs131.pa2.filter.concurrent;


/**
 * Implements wc command - overrides necessary behavior of SequentialFilter
 * 
 * By Yuxuan Liu
 *
 */
public class WordCountFilter extends ConcurrentFilter {

	/**
	 * word count in input - words are strings separated by space in the input
	 */
	private int wordCount;

	/**
	 * character count in input - includes ws
	 */
	private int charCount;

	/**
	 * line count in input
	 */
	private int lineCount;

	/**
	 * Constructs a wc filter.
	 */
	public WordCountFilter() {
		super();
		wordCount = 0;
		charCount = 0;
		lineCount = 0;
	}

	/**
	 * Overrides SequentialFilter.processLine() - updates the line, word, and
	 * character counts from the current input line
	 */
	@Override
	protected String processLine(String line) {
		lineCount++;
		wordCount += line.split(" ").length;
		charCount += line.length();

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
					//reach poison pill
					this.output.writeAndWait(lineCount + " " + wordCount + " " + charCount);
					this.output.writePoisonPill();
					break;
				} else {
					//no output until poison pill
					processLine(line);
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

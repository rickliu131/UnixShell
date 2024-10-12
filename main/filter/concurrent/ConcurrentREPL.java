package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.brandeis.cs.cs131.pa2.filter.Message;


/**
 * The main implementation of the REPL loop (read-eval-print loop). It reads
 * commands from the user, parses them, executes them and displays the result.
 * 
 * By Yuxuan Liu
 */
public class ConcurrentREPL {

	/**
	 * pipe string
	 */
	static final String PIPE = "|";

	/**
	 * redirect string
	 */
	static final String REDIRECT = ">";

	/**
	 * The main method that will execute the REPL loop
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {

		Scanner consoleReader = new Scanner(System.in);
		System.out.print(Message.WELCOME);
		
		List<List<Thread>> bgRunningThreadSets = new ArrayList<>();
		List<String> bgRunningThreadNames = new ArrayList<>();

		while (true) {
			System.out.print(Message.NEWCOMMAND);
			
			String cmd = consoleReader.nextLine().trim();

			// exit the REPL if user specifies it
			if (cmd.equals("exit")) {
				break;
			}
			
			// read user command, if its just whitespace, skip to next command
			if (cmd.isEmpty()) {
				continue;
			}
			
			if (cmd.equals("repl_jobs")) {
				for (int i=0; i<bgRunningThreadSets.size(); i++) {
					if (!bgRunningThreadSets.get(i).get(0).isAlive()) {
						bgRunningThreadSets.remove(i);
						bgRunningThreadNames.remove(i);
					}
				}
				for (int i=0; i<bgRunningThreadSets.size(); i++) {
					System.out.println(bgRunningThreadNames.get(i));
				}
				continue;
			}
			
			if (cmd.startsWith("kill")) {
				int index = Integer.valueOf(cmd.split(" ")[1])-1;
				bgRunningThreadNames.remove(index);
				List<Thread> threads = bgRunningThreadSets.remove(index);
				for (Thread thread : threads) {
					thread.interrupt();
				}
				continue;
			}

			try {
				boolean bg = false;
				if (cmd.charAt(cmd.length()-1) == '&') {
					bg = true;
					bgRunningThreadSets.add(new ArrayList<Thread>());
					bgRunningThreadNames.add("\t"+bgRunningThreadSets.size()+". "+cmd);
					cmd = cmd.substring(0, cmd.length()-1); //remove '&'
				}
				//when reach here, the foreground command must have already finished execution
				List<ConcurrentFilter> bgCmdFilters = ConcurrentCommandBuilder.createFiltersFromCommand(cmd);
				for (ConcurrentFilter bgCmdFilter : bgCmdFilters) {
					Thread thread = new Thread(bgCmdFilter);
					thread.start();
					if (bg) {
						bgRunningThreadSets.get(bgRunningThreadSets.size()-1).add(thread);
					} else {
						try {
							thread.join();
						} catch (InterruptedException e) {}
					}
				}
			} catch (IllegalArgumentException e) {
				System.out.print(e.getMessage());
			}
		}
		System.out.print(Message.GOODBYE);
		consoleReader.close();
	}

}

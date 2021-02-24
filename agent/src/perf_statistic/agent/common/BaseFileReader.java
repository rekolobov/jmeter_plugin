package perf_statistic.agent.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class BaseFileReader {
	private static final String FILE_NOT_FOUND = "file_not_found";
	private static final String FILE_CAN_NOT_READ = "file_cant_read";
	private static final String FILE_CAN_NOT_CLOSE = "file_cant_close";

	protected final PerformanceLogger myLogger;

	protected BaseFileReader(PerformanceLogger logger) {
		myLogger = logger;
	}

	protected abstract void processLine(String line) throws FileFormatException;

	protected abstract void processGatlingLogLine(String line) throws FileFormatException;

	public abstract void logProcessingResults();

	public void processFile(String file) throws FileFormatException {
		processFile(file, false);
	}

	public void processGatlingLogFile(String file) throws FileFormatException {
		if (file != null) {
			processFile(file, true);
		}
	}

	public void processFile(String file, boolean isGatlingLog) throws FileFormatException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));

			String line;

			if (isGatlingLog) {
				while (reader.ready() && !(line = reader.readLine()).isEmpty()) {
					processGatlingLogLine(line);
				}
			} else {
				while (reader.ready() && !(line = reader.readLine()).isEmpty()) {
					processLine(line);
				}
			}
		} catch (FileNotFoundException e) {
			myLogger.logBuildProblem(FILE_NOT_FOUND, FILE_NOT_FOUND, "Not found log file! Path - " + file);
		} catch (IOException e) {
			myLogger.logBuildProblem(FILE_CAN_NOT_READ, FILE_CAN_NOT_READ, "Can not read log file! Path - " + file);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					myLogger.logBuildProblem(FILE_CAN_NOT_CLOSE, FILE_CAN_NOT_CLOSE, "Can not close log file! Path - " + file);
				}
			}
		}
	}

	public static class FileFormatException extends Exception {
		public FileFormatException(String msg) {
			super(msg);
		}
	}
}

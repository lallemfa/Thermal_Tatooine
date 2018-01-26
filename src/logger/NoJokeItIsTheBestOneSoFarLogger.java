package logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoJokeItIsTheBestOneSoFarLogger {

    private static List<String> logLines = new ArrayList<>();
    private static boolean mLogToConsole = true;
    private static boolean mLogToFile = true;

    public static void setLogToConsole(boolean logToConsole) {
        mLogToConsole = logToConsole;
    }

    public static void setLogToFile(boolean logToFile) {
        mLogToFile = logToFile;
    }

    public static void end() {
        if (mLogToFile) {
            File directory = new File("logs");
            if (! directory.exists()) {
                directory.mkdir();
            }
            Path file = Paths.get("logs/Simulation.log");
            try {
                Files.write(file, logLines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                System.out.println("Can't log to log file: " + e.getMessage());
            }
        }
    }

    public static void log(LogType type, ZonedDateTime date, String message) {
        String logMessage = "[" + type.flag + "]";
        logMessage += " [" + date.toLocalDateTime() + "]";
        logMessage += " " + message;
        if (mLogToConsole) {
            System.out.println(logMessage);
        }
        if (mLogToFile) {
            logLines.add(logMessage);
        }
    }
}

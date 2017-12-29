package engine;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

public class SimTime {
	
	// DateTimeFormatter dd/MM/yyyy hh:mm
	public static final DateTimeFormatter logicalDateTimeFormatter;
	public static final DateTimeFormatter logicalTimeFormatter;
	public static final DateTimeFormatter logicalDateFormatter;
	
	static {
		logicalTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		logicalDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		DateTimeFormatterBuilder dtfb = new DateTimeFormatterBuilder();
		
		dtfb.parseCaseInsensitive();
		dtfb.append(logicalDateFormatter);
		dtfb.appendLiteral(" ");		
		dtfb.append(logicalTimeFormatter);
		
		logicalDateTimeFormatter = dtfb.toFormatter();
	}
	
	private LocalDateTime initialTime;
	private LocalDateTime finalTime;
	
	private LocalDateTime now;
	
	private Scenario[] scenarios;
	
	// Constructor
	public SimTime(String startTime, String endTime, Scenario[] scenarioList)
	{
		try {
			initialTime = LocalDateTime.parse(startTime, logicalDateTimeFormatter);
		}
		catch(DateTimeParseException e) {
			System.err.println(e.getMessage());
			System.err.println("Well formatted date/time dd/MM/yyyy HH:mm (e.g. 01/01/2000 12:00");
		}
		
		try {
			finalTime = LocalDateTime.parse(endTime, logicalDateTimeFormatter);
		}
		catch(DateTimeParseException e) {
			System.err.println(e.getMessage());
			System.err.println("Well formatted date/time dd/MM/yyyy HH:mm (e.g. 01/01/2000 12:00");
		}
		
		System.out.println("Duration of " + Duration.between(initialTime, finalTime).toMinutes() + " minutes.");
		
		now = initialTime;
		
		scenarios = scenarioList;
	}
	
	// Print
	public String toString()
	{
		String msg = "Initial Time\t" + initialTime + "\n" +
					"Final Time\t" + finalTime + "\n" +
					"Scenarios\n";
		
		for(Scenario scene : scenarios)
		{
			msg += "\t" + scene.toString() + "\n";
		}
		
		return msg;
	}
	
	// Running simulation
	public void run()
	{
		do
		{
			now = now.plusMinutes(1);
		} while(now.compareTo(finalTime) < 0);
		
		System.out.println("End !");
	}
	
}

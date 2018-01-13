package scenario;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import engine.Engine;
import engine.SortedListScheduler;
import spa.resort.SpaResort;

public class ScenarioTest {

	public static void main(String[] args) {
		
		SortedListScheduler scheduler = new SortedListScheduler();
		
		List<Month> openingMonths = new ArrayList<Month>();
		openingMonths.add(Month.APRIL);
		openingMonths.add(Month.AUGUST);
		
		List<DayOfWeek> openingDays = new ArrayList<DayOfWeek>();
		openingDays.add(DayOfWeek.FRIDAY);
		
		LocalTime openTime 		= LocalTime.parse("07:00:00");
		LocalTime closureTime 	= LocalTime.parse("14:00:00");
		LocalTime[][] openingHours = {{openTime, openTime,  openTime, openTime, openTime, openTime,  openTime},
										{closureTime, closureTime, closureTime, closureTime, closureTime, closureTime, closureTime}};
		
		float[] inflowMonth = {};
		
		SpaResort spa = new SpaResort(scheduler, openingMonths, openingDays, openingHours, 180, inflowMonth);
		
		Scenario scenario = new Scenario(spa);
		
		
		ZonedDateTime startTime 	= ZonedDateTime.parse("2018-01-01T00:00:00+01:00[Europe/Paris]");
		ZonedDateTime endTime 		= ZonedDateTime.parse("2019-01-01T00:00:00+01:00[Europe/Paris]");
		
		scenario.initScenario(startTime, endTime);
		
		
		Engine engine = new Engine(scheduler);
		engine.simulateUntil(startTime, endTime);

	}

}

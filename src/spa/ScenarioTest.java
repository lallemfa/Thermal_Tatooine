package spa;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import engine.Engine;
import logger.LoggerWrap;
import spa.resort.SpaResort;
import spa.scenario.Scenario;
import spa.treatment.Treatment;

public class ScenarioTest {

	public static void main(String[] args) {

		Engine engine = new Engine();
		
		LoggerWrap.setDateProvider(engine);
		
		List<Month> openingMonths = new ArrayList<>();
		openingMonths.add(Month.APRIL);
        openingMonths.add(Month.MAY);
        openingMonths.add(Month.JUNE);
		openingMonths.add(Month.JULY);
		openingMonths.add(Month.AUGUST);

		List<DayOfWeek> openingDays = new ArrayList<>();
		openingDays.add(DayOfWeek.MONDAY);
		openingDays.add(DayOfWeek.TUESDAY);
		openingDays.add(DayOfWeek.WEDNESDAY);
		openingDays.add(DayOfWeek.THURSDAY);
		openingDays.add(DayOfWeek.FRIDAY);

		LocalTime openTime = LocalTime.parse("07:00:00");
		LocalTime closureTime = LocalTime.parse("14:00:00");
		LocalTime[][] openingHours = {{openTime, openTime,  openTime, openTime, openTime, openTime,  openTime},
										{closureTime, closureTime, closureTime, closureTime, closureTime, closureTime, closureTime}};

		float[] inflowMonth = new float[] {0f, 0f, 0.5f, 0.6f, 0.7f, 0.8f, 0.95f, 0.9f, 0.65f, 0f, 0f, 0f};

		Treatment[] treatments = {Treatment.BainsAnciens, Treatment.BainsModernes, Treatment.Douches, Treatment.Etuves,
				Treatment.Filiformes, Treatment.SoinVisage, Treatment.TerresChaudes};

		SpaResort spa = new SpaResort(openingMonths, openingDays, openingHours, treatments, 50, inflowMonth);
		
		Scenario scenario = new Scenario(spa);

		ZonedDateTime startTime = ZonedDateTime.parse("2018-01-01T00:00:00+01:00[Europe/Paris]");
		ZonedDateTime endTime 	= ZonedDateTime.parse("2019-01-01T00:00:00+01:00[Europe/Paris]");
		
		engine.simulateUntil(scenario, startTime, endTime);
		LoggerWrap.Terminate();
		
	}

}

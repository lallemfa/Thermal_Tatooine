package spa.resort;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import engine.SortedListScheduler;

public class SpaTest {

	public static void main(String[] args) {
		System.out.println("\t\tTEST ABOUT SPA RESORT INSTANCIATION");
		
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
		
		System.out.println(spa);
	}

}

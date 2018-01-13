package spa;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.List;

import engine.IEvent;
import engine.MessageEvent;
import engine.SortedListScheduler;

public class SpaResort implements ISpaResort {
	
	private static final int[][] distances = {{0, 1, 2, 4, 1, 2, 3},
											{1, 0, 1, 2, 2, 2, 4},
											{2, 1, 0, 1, 3, 3, 3},
											{4, 2, 1, 0, 4, 4, 2},
											{1, 2, 3, 4, 0, 1, 2},
											{2, 2, 3, 4, 1, 0, 1},
											{3, 4, 3, 2, 2, 1, 0}};
										
	private final SortedListScheduler scheduler;
	
	private final List<Month> openingMonths;
	private final List<DayOfWeek> openingDays;
	private final LocalTime[][] openingHours;
	
	private final Treatment[] treatments;
	
	private final int maxClients;
	private final float[] inflowMonth;
	
	public SpaResort(SortedListScheduler scheduler, List<Month> openingMonths, List<DayOfWeek> openingDays, LocalTime[][] openingHours, Treatment[] treatments,
			int maxClients, float[] inflowMonth) {
		this.openingMonths = openingMonths;
		this.openingDays = openingDays;
		this.openingHours = openingHours;
		this.maxClients = maxClients;
		this.inflowMonth = inflowMonth;
		this.treatments = treatments;
		this.scheduler = scheduler;
	}

	@Override
	public float[] getInflowMonth() {
		return inflowMonth;
	}
	
	@Override
	public float getInflowMonth(Month month) {
		return inflowMonth[month.getValue()];
	}

	@Override
	public float getInflowMonth(ZonedDateTime time) {
		return getInflowMonth(time.getMonth());
	}

	@Override
	public List<Month> getOpeningMonths() {
		return openingMonths;
	}

	@Override
	public List<DayOfWeek> getOpeningDays() {
		return openingDays;
	}
	
	@Override
	public LocalTime getOpeningHour(ZonedDateTime time) {
		if( openingMonths.contains(time.getMonth()) & openingDays.contains(time.getDayOfWeek()) ) {
			return openingHours[0][time.getDayOfWeek().getValue()];
		} else {
			return null;
		}
	}

	@Override
	public LocalTime getClosingHour(ZonedDateTime time) {
		if( openingMonths.contains(time.getMonth()) & openingDays.contains(time.getDayOfWeek()) ) {
			return openingHours[1][time.getDayOfWeek().getValue()];
		} else {
			return null;
		}
	}

	@Override
	public boolean isOpen(ZonedDateTime time) {
		if( openingMonths.contains(time.getMonth()) & openingDays.contains(time.getDayOfWeek()) ) {
			return true;
		}
		return false;
	}
	
	private ZonedDateTime nextOpenDay(ZonedDateTime time) {
		ZonedDateTime nextDay = time.plusDays(1);
		
		while(!isOpen(nextDay)) {
			while( !openingMonths.contains(nextDay.getMonth()) ) {
				nextDay = nextDay.plusMonths(1);
				nextDay = nextDay.withDayOfMonth(1);
			}
			
			while( !openingDays.contains(nextDay.getDayOfWeek()) ) {
				nextDay = nextDay.plusDays(1);
			}
		}
		
		return nextDay;
	}
	
	@Override
	public Duration distanceBetween(Treatment treatment1, Treatment treatment2) {
		int duration = distances[treatment1.id][treatment2.id];
		return Duration.ofMinutes( duration );
	}

	@Override
	public void initEvents(ZonedDateTime startTime, ZonedDateTime endTime) {
		ZonedDateTime currDay = startTime;
		
		LocalTime openHour;
		LocalTime closeHour;
		
		IEvent openEvent;
		IEvent closeEvent;
		
		if( !isOpen(currDay) ) {
			currDay = nextOpenDay(currDay);
		}
		
		while(currDay.compareTo(endTime) < 0) {
			openHour 	= getOpeningHour(currDay);
			closeHour 	= getClosingHour(currDay);
			
			openEvent 	= new MessageEvent(currDay.with(openHour), "Spa opens");
			closeEvent 	= new MessageEvent(currDay.with(closeHour), "Spa closes");
			
			scheduler.postEvent(openEvent);
			scheduler.postEvent(closeEvent);
			
			currDay = nextOpenDay(currDay);
		}
	}

	@Override
	public String toString() {
		String msg = "=============== Spa Resort ===============\n\n" +
				"Max patients : " + maxClients + "\n" +
				"Opening months -> " + openingMonths.toString() + "\n" +
				"Opening days   -> " + openingDays.toString() + "\n" + 
				"Opening hours  -> " + openingHours.toString() + "\n\n" +
				"Treatments available";
		
		int compteur = 1;
		
		for(Treatment treatment : treatments) {
			msg += "\n" + (compteur++) + " - " + treatment.toString();
		}
		
		msg += "\n==========================================\n\n";
		
		return msg;
	}
	
}

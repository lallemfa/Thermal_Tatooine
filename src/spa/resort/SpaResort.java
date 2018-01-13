package spa.resort;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.List;

import engine.IEvent;
import engine.MessageEvent;
import engine.SortedListScheduler;

public class SpaResort implements ISpaResort {

	private final List<Month> openingMonths;
	private final List<DayOfWeek> openingDays;
	private final LocalTime[][] openingHours;
	
	private final int maxClients;
	private final float[] inflowMonth;
	
	private final SortedListScheduler scheduler;

	public SpaResort(SortedListScheduler scheduler, List<Month> openingMonths, List<DayOfWeek> openingDays, LocalTime[][] openingHours, int maxClients,
			float[] inflowMonth) {
		this.scheduler 		= scheduler;
		this.openingMonths 	= openingMonths;
		this.openingDays 	= openingDays;
		this.openingHours 	= openingHours;
		this.maxClients 	= maxClients;
		this.inflowMonth 	= inflowMonth;
	}

	@Override
	public float[] getInflowMonth() {
		return inflowMonth;
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
		return "\tSpa Resort\n\n" +
				"Max patients : " + maxClients + "\n" +
				"Opening months -> " + openingMonths.toString() + "\n" +
				"Opening days   -> " + openingDays.toString() + "\n" + 
				"Opening hours  -> " + openingHours.toString() + "\n";
	}
	
}

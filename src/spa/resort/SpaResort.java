package spa.resort;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import engine.event.IEvent;
import engine.event.IEventScheduler;
import engine.event.MessageEvent;
import enstabretagne.base.logger.IRecordable;
import spa.event.CloseSpaEvent;
import spa.event.CreatePatientsEvent;
import spa.person.Patient;
import spa.treatment.Treatment;

public class SpaResort implements ISpaResort, IRecordable {
	
	private static final int[][] distances = {{0, 1, 2, 4, 1, 2, 3},
											{1, 0, 1, 2, 2, 2, 4},
											{2, 1, 0, 1, 3, 3, 3},
											{4, 2, 1, 0, 4, 4, 2},
											{1, 2, 3, 4, 0, 1, 2},
											{2, 2, 3, 4, 1, 0, 1},
											{3, 4, 3, 2, 2, 1, 0}};
										
	private final List<Month> openingMonths;
	private final List<DayOfWeek> openingDays;
	private final LocalTime[][] openingHours;
	
	private final Treatment[] treatments;
	
	private final int maxClients;
	private final float[] inflowMonth;

	private List<Patient> patients;

	public SpaResort(List<Month> openingMonths, List<DayOfWeek> openingDays, LocalTime[][] openingHours, Treatment[] treatments,
			int maxClients, float[] inflowMonth) {
		this.openingMonths = openingMonths;
		this.openingDays = openingDays;
		this.openingHours = openingHours;
		this.maxClients = maxClients;
		this.inflowMonth = inflowMonth;
		this.treatments = treatments;
		this.patients = new ArrayList<>();
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
	public Duration getMaxDistanceDuration() {
		int max = distances[0][0];
	    for (int i = 0; i < distances.length; i++) {
	    	for (int j = 0; j < distances[0].length; j++) {
	    		if (distances[i][j] > max) {
		            max = distances[i][j];
		        }
	    	}
	    }
	    return Duration.ofMinutes( max );
	}

	@Override
	public void addPatient(Patient patient) {
		patients.add(patient);
	}

	@Override
	public int getNbPatientsOfWeek(ZonedDateTime time) {

		return 0;
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
	public Treatment[] getTreatments() {
		return treatments;
	}
	
	@Override
	public boolean isOpen(ZonedDateTime time) {
		if( openingMonths.contains(time.getMonth()) & openingDays.contains(time.getDayOfWeek()) ) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isOpenForThreeWeeks(ZonedDateTime time) {
		boolean isOpen = isOpen(time);
		time = time.plusWeeks(1);
		isOpen = isOpen && isOpen(time);
		time = time.plusWeeks(1);
		isOpen = isOpen && isOpen(time);
		return isOpen;
	}
	
	public ZonedDateTime nextOpenDay(ZonedDateTime time) {
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
	public void initEvents(IEventScheduler scheduler, ZonedDateTime startTime, ZonedDateTime endTime) {
		ZonedDateTime currDay = startTime;
		LocalTime openHour;
		LocalTime closeHour;
		IEvent openEvent;
		IEvent closeSpaEvent;
		if( !isOpen(currDay) ) {
			currDay = nextOpenDay(currDay);
		}
		while(currDay.compareTo(endTime) < 0) {
			if (currDay.getDayOfWeek() == DayOfWeek.MONDAY && isOpenForThreeWeeks(currDay)) {
				ZonedDateTime previousSunday = currDay.minusDays(1);
				scheduler.postEvent(new CreatePatientsEvent(this, previousSunday, this));
			}
			openHour 	= getOpeningHour(currDay);
			closeHour 	= getClosingHour(currDay);
			openEvent 	= new MessageEvent(this, currDay.with(openHour), "Spa opens");
			closeSpaEvent = new CloseSpaEvent(this, currDay.with(closeHour), this);
			scheduler.postEvent(openEvent);
			scheduler.postEvent(closeSpaEvent);
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

	// Next 3 methods for the Logger
	
	@Override
	public String[] getTitles() {
		return new String[] {"Classe"};
	}

	@Override
	public String[] getRecords() {
		return new String[] {this.getClass().getName()};
	}

	@Override
	public String getClassement() {
		return "Resort";
	}
	
}

package spa.resort;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import engine.event.IEvent;
import engine.event.IEventScheduler;
import engine.event.MessageEvent;
import enstabretagne.base.logger.IRecordable;
import spa.entity.Entity;
import spa.event.CloseSpaEvent;
import spa.person.Patient;
import spa.treatment.Treatment;

public class SpaResort extends Entity implements ISpaResort, IRecordable {
	
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
	private int newPatientId;
	
	private List<List<ZonedDateTime>> mondayOfOpenWeeksByYear = new ArrayList<List<ZonedDateTime>>();

	public SpaResort(List<Month> openingMonths, List<DayOfWeek> openingDays, LocalTime[][] openingHours, Treatment[] treatments,
			int maxClients, float[] inflowMonth) {
		super();
		this.openingMonths = openingMonths;
		this.openingDays = openingDays;
		this.openingHours = openingHours;
		this.maxClients = maxClients;
		this.inflowMonth = inflowMonth;
		this.treatments = treatments;
		this.patients = new ArrayList<>();
		this.newPatientId = 0;
		super.endConstructor();
	}

	@Override
	public float[] getInflowMonth() {
		return inflowMonth;
	}
	
	@Override
	public float getInflowMonth(Month month) {
		return maxClients * inflowMonth[month.getValue()];
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
		int year = time.getYear();
		int relativeWeek = dayToWeek(time);
		int nbPatients = 0;
		for (Patient patient : patients) {
			if (patient.getCure().hasCureDuringWeek(year, relativeWeek)) {
				nbPatients++;
			}
		}
		return nbPatients;
	}
	
	@Override
	public int getNewPatientId() {
		return this.newPatientId;
	}

	@Override
	public void setNewPatientId(int id) {
		this.newPatientId = id;
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
	public Duration distanceBetween(Treatment treatment1, Treatment treatment2) {
		int duration = distances[treatment1.id][treatment2.id];
		return Duration.ofMinutes( duration );
	}
	
	@Override
	public boolean isOpen(ZonedDateTime time) {
		return openingMonths.contains(time.getMonth()) & openingDays.contains(time.getDayOfWeek());
	}

	@Override
	public boolean isOpenForThreeWeeks(ZonedDateTime time) {
		return isWeekOpen(time) && isWeekOpen(time.plusWeeks(1))  && isWeekOpen(time.plusWeeks(2));
	}
	
	private int idOfWeek(ZonedDateTime time) {
		return (int)Math.floor(time.getDayOfYear()/7);
	}
	
	private ZonedDateTime firstOpenDayOfWeek(ZonedDateTime time) {
		DayOfWeek firstDay = openingDays.get(0);
		if( idOfWeek(time) != idOfWeek(time.with(firstDay)) ) {
			return time.with(firstDay).minusDays(7);
		} else {
			return time.with(firstDay);
		}
	}
	
	private boolean isWeekOpen(ZonedDateTime time) {
		ZonedDateTime firstDay = firstOpenDayOfWeek(time);
		return isOpen(firstDay) && isOpen( firstDay.plusDays(openingDays.size() - 1) );
	}
	
	private int minWeeksOpen(ZonedDateTime startTime, ZonedDateTime endTime) {
		ZonedDateTime currDay = startTime;
		
		int currYear;
		int endYear = endTime.getYear();
		
		int minWeeks = 60;
		int tempWeeks = 0;
		
		if(!isOpen(currDay)) {
			currDay = nextOpenDay(currDay);
		}
		currYear = currDay.getYear();
		currDay = firstOpenDayOfWeek(currDay);
		
		while(currYear <= endYear) {
			while(currDay.getYear() == currYear) {
				tempWeeks += ( isWeekOpen(currDay) ) ? 1 : 0;
				currDay = currDay.plusWeeks(1);
			}
			
			if(tempWeeks < minWeeks) {
				minWeeks = tempWeeks;
			}
			tempWeeks = 0;
			currDay = currDay.withDayOfYear(1);
			currYear += 1;
		}
		
		return minWeeks;
	}
	
	public int dayToWeek(ZonedDateTime day) {
		ZonedDateTime firstDay = firstOpenDayOfWeek(day);
		for(List<ZonedDateTime> sublist : mondayOfOpenWeeksByYear) {
			if(sublist.contains(firstDay)) {
				return sublist.indexOf(firstDay);
			}
		}
		return -1;
	}
	
	public ZonedDateTime weekToDay(int year, int week) {
		for(List<ZonedDateTime> sublist : mondayOfOpenWeeksByYear) {
			if(year == sublist.get(0).getYear()) {
				if(week < sublist.size()) {
					return sublist.get(week);
				}
			}
		}
		return null;
	}
	
	public ZonedDateTime nextOpenDay(ZonedDateTime time) {
		ZonedDateTime nextDay = time.plusDays(1);

		while(!isOpen(nextDay) || !isWeekOpen(nextDay)) {
			while( !openingMonths.contains(nextDay.getMonth()) ) {
				nextDay = nextDay.plusMonths(1);
				nextDay = nextDay.withDayOfMonth(1);
			}
			
			while( !openingDays.contains(nextDay.getDayOfWeek()) ) {
				nextDay = nextDay.plusDays(1);
			}
			if(!isWeekOpen(nextDay)) {
				nextDay = nextDay.plusWeeks(1);
			}
		}
		
		return nextDay;
	}
	
	public ZonedDateTime nextOpenableWeek(ZonedDateTime time) {
		ZonedDateTime nextDay = time.plusWeeks(1);
		nextDay = firstOpenDayOfWeek(nextDay);
		
		while(!isWeekOpen(nextDay)) {
			while( !openingMonths.contains(nextDay.getMonth()) ) {
				nextDay = nextDay.plusMonths(1);
				nextDay = nextDay.withDayOfMonth(1);
			}
			
			nextDay = nextDay.plusWeeks(1);
		}
		
		return firstOpenDayOfWeek(nextDay);
	}
	



	private void postEventForWeek (IEventScheduler scheduler, ZonedDateTime currDay) {
		LocalTime openHour;
		LocalTime closeHour;
		IEvent openEvent;
		IEvent closeSpaEvent;
		
		for(DayOfWeek day : openingDays) {
			currDay = currDay.with(day);
			
			openHour 	= getOpeningHour(currDay);
			closeHour 	= getClosingHour(currDay);
			openEvent 	= new MessageEvent(this, currDay.with(openHour), "Spa opens");
			closeSpaEvent = new CloseSpaEvent(this, currDay.with(closeHour), this);
			scheduler.postEvent(openEvent);
			scheduler.postEvent(closeSpaEvent);
		}
	}
	
	@Override
	public void initEvents(IEventScheduler scheduler, ZonedDateTime startTime, ZonedDateTime endTime) {
		ZonedDateTime currDay = startTime;
		
		int minWeek = minWeeksOpen(startTime, endTime);
		int compteur = 0;
		
		List<ZonedDateTime> subList = new ArrayList<ZonedDateTime>();
		
		if( !isWeekOpen(currDay) ) {
			currDay = nextOpenableWeek(currDay);
		}
		
		int currYear = currDay.getYear();
		
		while(currDay.compareTo(endTime) < 0) {
			subList = new ArrayList<ZonedDateTime>();
			while(compteur < minWeek && currDay.getYear() == currYear) {
				subList.add(currDay);
				postEventForWeek (scheduler, currDay);
				currDay = nextOpenableWeek(currDay);
				compteur += 1;
			}
			
			if(currDay.getYear() == currYear) {
				currDay = currDay.plusYears(1).withDayOfYear(1);
			} else {
				currDay = currDay.withDayOfYear(1);
			}
			
			mondayOfOpenWeeksByYear.add(subList);
			
			compteur = 0;
			currYear = currDay.getYear();
			currDay = nextOpenableWeek(currDay);
		}
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

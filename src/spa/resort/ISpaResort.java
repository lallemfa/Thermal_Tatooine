package spa.resort;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.List;

import engine.event.IEventScheduler;
import spa.person.Patient;
import spa.treatment.Treatment;

public interface ISpaResort {
	
	List<Month> getOpeningMonths();
	List<DayOfWeek> getOpeningDays();
	
	LocalTime getOpeningHour(ZonedDateTime time);
	LocalTime getClosingHour(ZonedDateTime time);
	Treatment[] getTreatments();
	
	float[] getInflowMonth();
	float getInflowMonth(Month month);
	float getInflowMonth(ZonedDateTime time);
	
	Duration distanceBetween(Treatment treatment1, Treatment treatment2);
	
	boolean isOpen(ZonedDateTime time);
	boolean isOpenForThreeWeeks(ZonedDateTime time);
	
	void initEvents(IEventScheduler scheduler, ZonedDateTime startTime, ZonedDateTime endTime);
	Duration getMaxDistanceDuration();

	void addPatient(Patient patient);
	int getNbPatientsOfWeek(ZonedDateTime time);
	int getNewPatientId();
	void setNewPatientId(int id);
	List<Patient> getPatients();
	
}

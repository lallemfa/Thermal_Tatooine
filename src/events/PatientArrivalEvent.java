package events;

import java.time.ZonedDateTime;

import engine.Engine;
import logger.LogType;
import logger.Logger;
import person.Patient;
import spa.SpaResort;

public class PatientArrivalEvent implements IEvent {

	private final ZonedDateTime scheduledTime;
	private Patient patient;
	private SpaResort spa;
	
	public PatientArrivalEvent(ZonedDateTime scheduledTime, SpaResort spa, Patient patient) {
		this.scheduledTime = scheduledTime;
		this.patient = patient;
		this.spa = spa;
	}

	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process() {
		Logger.log(LogType.INFO, this.scheduledTime, "Patient" + this.patient.getId() + "arrived");
		IEvent searchEvent;
		searchEvent = new SearchForActionEvent(this.scheduledTime, this.spa, this.patient);
		Engine.addEvent(searchEvent);
	}
	
}

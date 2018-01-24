package spa.event;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import engine.Engine;
import engine.event.IEvent;
import logger.LogType;
import logger.Logger;
import spa.cure.Cure;
import spa.person.Patient;
import spa.resort.SpaResort;

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
		Cure patientCure = this.patient.getCure();
		this.patient.getCure().resetDoneTreatments(); 
		Logger.log(LogType.INFO, this.scheduledTime, "Patient" + this.patient.getId() + "arrived");
		IEvent searchEvent;
		searchEvent = new SearchForActionEvent(this.scheduledTime, this.spa, this.patient);
		Engine.addEvent(searchEvent);
	}
	
}

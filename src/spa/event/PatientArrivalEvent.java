package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import enstabretagne.base.logger.Logger;
import logger.LogType;
import logger.NoJokeItIsTheBestOneSoFarLogger;
import spa.cure.Cure;
import spa.person.Patient;
import spa.resort.SpaResort;

public class PatientArrivalEvent extends Event implements IEvent {

	private final ZonedDateTime scheduledTime;
	private Patient patient;
	private SpaResort spa;
	
	public PatientArrivalEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa, Patient patient) {
		super(parent);
		this.scheduledTime = scheduledTime;
		this.patient = patient;
		this.spa = spa;
	}

	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process(IEventScheduler scheduler) {
		Cure patientCure = this.patient.getCure();
		patientCure.resetDoneTreatments(); 
		NoJokeItIsTheBestOneSoFarLogger.log(LogType.INFO, this.scheduledTime, "Patient" + this.patient.getId() + "arrived");
		Logger.Information(getParent(), "Process", "Patient" + this.patient.getId() + "arrived");
		IEvent searchEvent;
		searchEvent = new SearchForActionEvent(getParent(), this.scheduledTime, this.spa, this.patient);
		scheduler.postEvent(searchEvent);
	}
	
}

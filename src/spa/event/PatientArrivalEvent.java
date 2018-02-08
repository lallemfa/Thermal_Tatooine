package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.LoggerWrap;
import spa.cure.Cure;
import spa.person.Patient;
import spa.person.PersonState;
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
		this.patient.setPersonState(PersonState.Moving);
		this.patient.nbRecursiveSearch = 0;
		Cure patientCure = this.patient.getCure();
		patientCure.resetDoneTreatments(); 
		LoggerWrap.Log(this.patient, "Patient " + this.patient.getId() + " arrived");
		IEvent searchEvent = new SearchForActionEvent(this.patient, this.scheduledTime, this.spa, this.patient);
		scheduler.postEvent(searchEvent);
	}
	
}

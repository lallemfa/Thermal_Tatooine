package spa.event;

import java.time.Duration;
import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import enstabretagne.base.logger.Logger;
import spa.person.Patient;
import spa.person.PersonState;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class AppointmentTimeoutEvent extends Event implements IEvent {
	
	private ZonedDateTime scheduledTime;
	private Patient patient;
	private Treatment treatment;
	private SpaResort spa;

	public AppointmentTimeoutEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa, Patient patient, Treatment treatment) {
		super(parent);
		this.spa = spa;
		this.scheduledTime = scheduledTime;
		this.patient = patient;
		this.treatment = treatment;
	}
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process(IEventScheduler scheduler) {
		Logger.Information(getParent(), "Process", "Patient " + this.patient.getId() + " got an appointment in " + this.treatment);
		Duration duration = selectDuration(this.patient.getPersonState(), this.treatment);

		if (this.patient.getPersonState() == PersonState.Treatment) {
			scheduler.removeEvent(this.patient.nextEndTreatment);
			IEvent endTreatmentEvent = new EndTreatmentEvent(getParent(), this.scheduledTime, this.spa, this.patient);
			scheduler.postEvent(endTreatmentEvent);
		}

		this.patient.setPersonState(PersonState.Appointment);
		IEvent arrivedTreatmentEvent;
		ZonedDateTime arrivedTime = this.scheduledTime.plus(duration);
		arrivedTreatmentEvent = new ArrivedTreatmentEvent(getParent(), arrivedTime, this.spa, this.treatment, this.patient);
		scheduler.postEvent(arrivedTreatmentEvent);
	}
	
	private Duration selectDuration(PersonState state,Treatment appointmentTreatment) {
		Duration duration;
		if (state != PersonState.Treatment) {
			duration = this.spa.getMaxDistanceDuration();
		} else {
			Treatment treatment = this.patient.getTreatment();
			duration = this.spa.distanceBetween(treatment, appointmentTreatment);
		}
		return duration;
	}

}

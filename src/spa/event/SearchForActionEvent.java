package spa.event;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import enstabretagne.base.logger.Logger;
import spa.person.Patient;
import spa.person.PersonState;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class SearchForActionEvent extends Event implements IEvent {

	private ZonedDateTime scheduledTime;
	private Patient patient;
	private SpaResort spa;

	public SearchForActionEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa, Patient patient) {
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
		PersonState state = this.patient.getState();
		Treatment choosenTreatment = selectNextTreatment(state);
		Duration duration = selectDuration(state, choosenTreatment);
		Logger.Information(getParent(), "Process", "Patient" + this.patient.getId() + "starts looking for an available treatment");
		
		
		IEvent arrivedTreatmentEvent;
		ZonedDateTime arrivedTime = this.scheduledTime.plus(duration);
		arrivedTreatmentEvent = new ArrivedTreatmentEvent(getParent(), arrivedTime, this.spa, choosenTreatment, this.patient);
		scheduler.postEvent(arrivedTreatmentEvent);
	}
	
	private Treatment selectNextTreatment(PersonState state) {
		List<Treatment> dailyTreatments = this.patient.getCure().getDailyTreatments();
		List<Boolean> doneTreatments = this.patient.getCure().getDoneTreatments();
		
		Treatment choosenTreatment = null;
		Duration durationMoving = Duration.ofMinutes(100);
		
		for(int i=0; i < dailyTreatments.size(); i++) {
			Treatment tempTreatment = dailyTreatments.remove(dailyTreatments.size()-1);
			if (!doneTreatments.get(i) && !tempTreatment.isWithAppointment() && state != PersonState.Treatment) {
				return tempTreatment;
			}
			if (!doneTreatments.get(i) && !tempTreatment.isWithAppointment() && state == PersonState.Treatment) {
				Treatment treatment = this.patient.getTreatment();
				Duration duration = this.spa.distanceBetween(treatment, tempTreatment);
				if (duration.compareTo(durationMoving) < 0) {
					choosenTreatment = tempTreatment;
				}
			}
		}
		return choosenTreatment;
	}
	
	private Duration selectDuration(PersonState state,Treatment choosenTreatment) {
		Duration duration = null;
		if (state != PersonState.Treatment) {
			duration = this.spa.getMaxDistanceDuration();
		} else {
			Treatment treatment = this.patient.getTreatment();
			duration = this.spa.distanceBetween(treatment, choosenTreatment);
		}
		return duration;
	}
}
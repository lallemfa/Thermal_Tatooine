package spa.event;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.LoggerWrap;
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
		PersonState state = this.patient.getPersonState();
		Treatment chosenTreatment = selectNextTreatment(state);
		if (chosenTreatment == null) {
			IEvent leaveEvent;
			leaveEvent = new LeaveSpaEvent(this.patient, this.scheduledTime, this.spa, this.patient);
			scheduler.postEvent(leaveEvent);
		} else {
			Duration duration = selectDuration(state, chosenTreatment);
			this.patient.setPersonState(PersonState.Moving);
			ZonedDateTime arrivedTime = this.scheduledTime.plus(duration);
			if (arrivedTime.toLocalTime().isAfter(this.spa.getClosingHour(arrivedTime))) {
				IEvent leaveSpaEvent = new LeaveSpaEvent(this.patient, this.scheduledTime, spa, patient);
				scheduler.postEvent(leaveSpaEvent);
			} else {
				LoggerWrap.Log(this.patient, "Patient " + this.patient.getId() + " starts looking for an available treatment");
				IEvent arrivedTreatmentEvent = new ArrivedTreatmentEvent(this.patient, arrivedTime, this.spa, chosenTreatment, this.patient);
				scheduler.postEvent(arrivedTreatmentEvent);
				this.patient.nextMovingEvent = arrivedTreatmentEvent;
			}
		}
	}
	
	private Treatment selectNextTreatment(PersonState state) {
		List<Treatment> dailyTreatments = this.patient.getCure().getDailyTreatments();
		List<Boolean> doneTreatments = this.patient.getCure().getDoneTreatments();
		List<Treatment> availableTreatments = new ArrayList<>();
		Treatment lastTreatment = this.patient.getTreatment();

		for (int i = 0; i < dailyTreatments.size(); i++) {
			Treatment treatment = dailyTreatments.get(i);
			if (!doneTreatments.get(i) && !treatment.isWithAppointment() && !treatment.getBrokenState()
				&& (lastTreatment == null || !lastTreatment.equals(treatment))) {
					availableTreatments.add(treatment);
			}
		}
		if (availableTreatments.size() == 0) {
			return null;
		}
		return availableTreatments.get((int)Math.round(Math.random() * (availableTreatments.size() - 1)));
	}
	
	private Duration selectDuration(PersonState state,Treatment choosenTreatment) {
		Duration duration;
		if (state != PersonState.Treatment) {
			duration = this.spa.getMaxDistanceDuration();
		} else {
			Treatment treatment = this.patient.getTreatment();
			duration = this.spa.distanceBetween(treatment, choosenTreatment);
		}
		return duration;
	}
}
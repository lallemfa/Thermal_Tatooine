package spa.event;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.IRecordableWrapper;
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
		Treatment choosenTreatment = selectNextTreatment(state);
		if (choosenTreatment == null) {
			IEvent leaveEvent;
			leaveEvent = new LeaveSpaEvent(getParent(), this.scheduledTime, this.spa, this.patient);
			scheduler.postEvent(leaveEvent);
		} else {
			Duration duration = selectDuration(state, choosenTreatment);
			LoggerWrap.Log((IRecordableWrapper) getParent(), "Patient " + this.patient.getId() + " starts looking for an available treatment");
			
			this.patient.setPersonState(PersonState.Moving);
			IEvent arrivedTreatmentEvent;
			ZonedDateTime arrivedTime = this.scheduledTime.plus(duration);
			arrivedTreatmentEvent = new ArrivedTreatmentEvent(getParent(), arrivedTime, this.spa, choosenTreatment, this.patient);
			scheduler.postEvent(arrivedTreatmentEvent);
		}		
	}
	
	private Treatment selectNextTreatment(PersonState state) {
		List<Treatment> dailyTreatments = this.patient.getCure().getDailyTreatments();
		List<Boolean> doneTreatments = this.patient.getCure().getDoneTreatments();

		if (doneTreatments.stream().allMatch(t -> t)) {
			return null;
		}

		Treatment choosenTreatment = null;
		Duration durationMoving = Duration.ofMinutes(100);

		for (int i = 0; i < dailyTreatments.size(); i++) {
			Treatment tempTreatment = dailyTreatments.get(i);
			boolean condition = !doneTreatments.get(i) && !tempTreatment.isWithAppointment() && !tempTreatment.getBrokenState();
			if (condition && state != PersonState.Treatment  && state != PersonState.WaitingQueue) {
				return tempTreatment;
			}
			if (condition) {
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
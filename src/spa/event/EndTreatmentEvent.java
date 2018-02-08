package spa.event;

import java.time.Duration;
import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.LoggerWrap;
import spa.person.Patient;
import spa.person.PersonState;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class EndTreatmentEvent extends Event implements IEvent {

	private ZonedDateTime scheduledTime;
	private Patient patient;
	private SpaResort spa;

	public EndTreatmentEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa, Patient patient) {
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
		this.patient.nextEndTreatment = null;

		Duration duration = getTimeInTreatment(this.patient.getStartTreatment());
		Treatment treatment = this.patient.getTreatment();
		Duration treatmentDuration = treatment.getDuration();
		float ratio = (float)duration.toMinutes() / (float)treatmentDuration.toMinutes();
		float newRatio = this.patient.getCure().getDoneTreatmentRatio(treatment) + ratio;
		this.patient.getCure().setDoneTreatments(treatment, newRatio);

		int points = (int)Math.floor(Math.min(ratio * treatment.getMaxPoints(), treatment.getMaxPoints()));
		this.patient.addCurePoints(points);

		treatment.removeCurrentPatients(this.patient);
		LoggerWrap.Log(this.patient, "Patient " + this.patient.getId() + " finished " + treatment.name + " and earned " + points + " points");

		if (this.patient.getPersonState() == PersonState.Appointment) {
			return;
		}

		this.patient.setPersonState(PersonState.Moving);
		if (this.spa.getClosingHour(this.scheduledTime).isBefore(this.scheduledTime.toLocalTime())) {
			scheduler.postEvent(new LeaveSpaEvent(getParent(), this.scheduledTime, this.spa, this.patient));
		} else {
			IEvent searchEvent = new SearchForActionEvent(getParent(), this.scheduledTime, this.spa, this.patient);
			scheduler.postEvent(searchEvent);
			this.patient.nextMovingEvent = searchEvent;
			IEvent availableTreatmentEvent = new AvailableTreatmentEvent(treatment, this.scheduledTime, spa, treatment);
			scheduler.postEvent(availableTreatmentEvent);
		}
	}
	
	private Duration getTimeInTreatment(ZonedDateTime startTreatment) {
		return Duration.between(startTreatment, this.scheduledTime);
	}
}

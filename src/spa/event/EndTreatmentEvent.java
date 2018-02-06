package spa.event;

import java.time.Duration;
import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.IRecordableWrapper;
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
		this.patient.addCurePoints(getPointPatient());
		updateDoneTreatmentList();
		Treatment treatment = this.patient.getTreatment();
		treatment.removeCurrentPatients(this.patient);
		LoggerWrap.Log(this.patient, "Patient " + this.patient.getId() + " finished " + treatment.name);

		if (this.patient.getPersonState() == PersonState.Appointment) {
			return;
		}

		if (this.spa.getClosingHour(this.scheduledTime).isBefore(this.scheduledTime.toLocalTime())) {
			scheduler.postEvent(new LeaveSpaEvent(getParent(), this.scheduledTime, this.spa, this.patient));
		} else {
			IEvent searchEvent = new SearchForActionEvent(getParent(), this.scheduledTime, this.spa, this.patient);
			scheduler.postEvent(searchEvent);
			IEvent availableTreatmentEvent = new AvailableTreatmentEvent(treatment, this.scheduledTime, spa, treatment);
			scheduler.postEvent(availableTreatmentEvent);
		}
	}
	
	private Duration getTimeInTreatment(ZonedDateTime startTreatment) {
		return Duration.between(startTreatment, this.scheduledTime);
	}

	private int getPointPatient() {
		Duration duration = getTimeInTreatment(this.patient.getStartTreatment());
		Treatment treatment = this.patient.getTreatment();
		Duration treatmentDuration = treatment.getDuration();
		return (int) Math.min(duration.toMinutes() * treatment.getMaxPoints() / treatmentDuration.toMinutes(), treatment.getMaxPoints());
	}
	
	private void updateDoneTreatmentList() {
		Treatment patientTreatment = this.patient.getTreatment();
		this.patient.getCure().setDoneTreatments(patientTreatment);
	}
}

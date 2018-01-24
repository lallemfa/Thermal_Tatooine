package events;

import java.time.Duration;
import java.time.ZonedDateTime;

import engine.Engine;
import person.Patient;
import spa.SpaResort;
import spa.Treatment;

public class EndTreatmentEvent implements IEvent {

	private ZonedDateTime scheduledTime;
	private Patient patient;
	private SpaResort spa;

	public EndTreatmentEvent(ZonedDateTime scheduledTime, SpaResort spa, Patient patient) {
        this.scheduledTime = scheduledTime;
        this.patient = patient;
        this.spa = spa;
    }
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return scheduledTime;
	}

	@Override
	public void process() {
		this.patient.addCurePoints(getPointPatient());
		IEvent searchEvent;
		searchEvent = new SearchForActionEvent(this.scheduledTime, this.spa, this.patient);
		Engine.addEvent(searchEvent);
	}
	
	private Duration getTimeInTreatment(ZonedDateTime startTreatment) {
		Duration duration = Duration.between(this.scheduledTime, startTreatment);
		return duration;
	}

	private int getPointPatient() {
		Duration duration = getTimeInTreatment(this.patient.getStartTreatment());
		Treatment treatment = this.patient.getTreatment();
		Duration treatmentDuration = treatment.getDuration();
		int point = (int) (treatmentDuration.toMinutes() * treatment.getMaxPoints() / duration.toMinutes());
		return point;
	}
}

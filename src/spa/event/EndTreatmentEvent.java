package spa.event;

import java.time.Duration;
import java.time.ZonedDateTime;
import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import enstabretagne.base.logger.Logger;
import spa.person.Patient;
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
		this.patient.addCurePoints(getPointPatient());
		updateDoneTreatmentList();
		Treatment treatment = this.patient.getTreatment();
		treatment.removeCurrentPatients(this.patient);
		Logger.Information(getParent(), "Process", "Patient" + this.patient.getId() + "finished " + treatment.name);
		
		int actualHour 	= this.scheduledTime.getHour();
		int closingHour = this.spa.getClosingHour(this.scheduledTime).getHour();
		if(actualHour >= closingHour) {
			scheduler.postEvent(new LeaveSpaEvent(getParent(), this.scheduledTime, this.spa, this.patient));
		}
			
		IEvent searchEvent;
		searchEvent = new SearchForActionEvent(getParent(), this.scheduledTime, this.spa, this.patient);
		scheduler.postEvent(searchEvent);
		IEvent availableTreatmentEvent;
		availableTreatmentEvent = new AvailableTreatmentEvent(getParent(), this.scheduledTime, this.spa, treatment);
		scheduler.postEvent(availableTreatmentEvent);
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
	
	private void updateDoneTreatmentList() {
		Treatment patientTreatment = this.patient.getTreatment();
		this.patient.getCure().setDoneTreatments(patientTreatment);
	}
}

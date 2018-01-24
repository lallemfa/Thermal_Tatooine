package spa.event;

import java.time.ZonedDateTime;

import engine.event.IEvent;
import engine.event.IEventScheduler;
import spa.person.Patient;
import spa.treatment.Treatment;

public class ArrivedTreatmentEvent implements IEvent {
	
	private ZonedDateTime scheduledTime;
	private Patient patient;
	private Treatment treatment;

	public ArrivedTreatmentEvent(ZonedDateTime scheduledTime, Treatment treatment, Patient patient) {
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
		
		
	}

}

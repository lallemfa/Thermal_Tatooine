package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import enstabretagne.base.logger.Logger;
import spa.person.Patient;
import spa.resort.SpaResort;

public class AppointmentTimeoutEvent extends Event implements IEvent {
	
	private ZonedDateTime scheduledTime;
	private Patient patient;

	public AppointmentTimeoutEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa, Patient patient) {
		super(parent);
		this.scheduledTime = scheduledTime;
		this.patient = patient;
	}
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override

	public void process(IEventScheduler scheduler) {
		Logger.Information(getParent(), "Process", "Appointment Patient " + this.patient.getId());
	}

}

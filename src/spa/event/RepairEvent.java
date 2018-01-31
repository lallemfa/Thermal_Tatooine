package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import enstabretagne.base.logger.Logger;
import spa.treatment.Treatment;

public class RepairEvent extends Event implements IEvent {

	private ZonedDateTime scheduledTime;
	private Treatment treatment;

	public RepairEvent(Object parent, ZonedDateTime scheduledTime, Treatment treatment) {
		super(parent);
        this.scheduledTime = scheduledTime;
        this.treatment = treatment;
    }
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process(IEventScheduler scheduler) {
		Logger.Information(getParent(), "Process", "Repair of treatment: " + this.treatment.name);
		this.treatment.setBrokenState(false);
	}
}

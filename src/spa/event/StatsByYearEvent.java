package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.IRecordableWrapper;
import logger.LoggerWrap;
import spa.person.Patient;
import spa.resort.SpaResort;

public class StatsByYearEvent extends Event implements IEvent {

    private ZonedDateTime scheduledTime;
    private SpaResort spa;
    
	public StatsByYearEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa) {
		super(parent);
        this.scheduledTime = scheduledTime;
        this.spa = spa;
	}

	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process(IEventScheduler scheduler) {
		for(Patient patient : this.spa.getPatients()) {
			LoggerWrap.Log((IRecordableWrapper) patient, "Happy New Year");
			patient.resetPointsByYear();
		}
	}
}

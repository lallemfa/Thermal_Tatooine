package spa.event;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import logger.IRecordableWrapper;
import logger.LoggerWrap;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class OpenSpaEvent extends Event implements IEvent {

	private ZonedDateTime scheduledTime;
	private SpaResort spa;

	public OpenSpaEvent(Object parent, ZonedDateTime scheduledTime, SpaResort spa) {
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
		LoggerWrap.Log((IRecordableWrapper) getParent(), "Spa opens");
		for (Treatment treatment : this.spa.getTreatments()) {
			treatment.clearQueues();
		}
	}

}

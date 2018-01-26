package spa.event;

import java.time.ZonedDateTime;

import engine.event.IEvent;
import logger.LogType;
import logger.NoJokeItIsTheBestOneSoFarLogger;

public class OpenSpaEvent implements IEvent {

	private ZonedDateTime scheduledTime;

	public OpenSpaEvent(ZonedDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process() {
		NoJokeItIsTheBestOneSoFarLogger.log(LogType.INFO, this.scheduledTime, "Spa opened");
	}

}

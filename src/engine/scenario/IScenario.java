package engine.scenario;

import engine.event.IEventScheduler;

import java.time.ZonedDateTime;

public interface IScenario {

	void initScenario(IEventScheduler scheduler, ZonedDateTime startTime, ZonedDateTime endTime);
	
}

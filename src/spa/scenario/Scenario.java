package spa.scenario;

import java.time.ZonedDateTime;

import engine.event.IEventScheduler;
import engine.scenario.IScenario;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class Scenario implements IScenario {

	private final SpaResort spa;
	
	private final ZonedDateTime startTime;
	private final ZonedDateTime endTime;
	
	public Scenario(SpaResort spa, ZonedDateTime startTime, ZonedDateTime endTime) {
		this.spa = spa;
		
		this.startTime 	= startTime;
		this.endTime 	= endTime;
	}

	@Override
	public void initScenario(IEventScheduler scheduler) {
		spa.initEvents(scheduler, this.startTime, this.endTime);
		
		for (Treatment treatment : spa.getTreatments()) {
			treatment.initEvents(scheduler, spa, this.startTime, this.endTime);
		}
	}

	@Override
	public ZonedDateTime getStartTime() {
		return this.startTime;
	}

	@Override
	public ZonedDateTime getEndTime() {
		return this.endTime;
	}
}

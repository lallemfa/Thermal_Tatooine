package spa.scenario;

import java.time.ZonedDateTime;

import engine.event.IEventScheduler;
import engine.scenario.IScenario;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class Scenario implements IScenario {

	private final SpaResort spa;
	
	public Scenario(SpaResort spa) {
		this.spa = spa;
	}

	@Override
	public void initScenario(IEventScheduler scheduler, ZonedDateTime startTime, ZonedDateTime endTime) {
		spa.initEvents(scheduler, startTime, endTime);
		/*
		for (Treatment treatment : spa.getTreatments()) {
			treatment.initEvents(scheduler, spa, startTime, endTime);
		}
		*/
	}
}

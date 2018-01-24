package spa.scenario;

import java.time.ZonedDateTime;

import engine.scenario.IScenario;
import spa.resort.SpaResort;
import spa.treatment.Treatment;

public class Scenario implements IScenario {

	private final SpaResort spa;
	
	public Scenario(SpaResort spa) {
		this.spa = spa;
	}

	@Override
	public void initScenario(ZonedDateTime startTime, ZonedDateTime endTime) {
		spa.initEvents(startTime, endTime);
		for (Treatment treatment : spa.getTreatments()) {
			treatment.initEvents(spa, startTime, endTime);
		}
	}
}

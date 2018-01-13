package scenario;

import java.time.ZonedDateTime;

import spa.ISpaResort;

public class Scenario implements IScenario {

	private final ISpaResort spa;
	
	public Scenario(ISpaResort spa) {
		this.spa = spa;
	}

	@Override
	public void initScenario(ZonedDateTime startTime, ZonedDateTime endTime) {
		spa.initEvents(startTime, endTime);
	}
	
}

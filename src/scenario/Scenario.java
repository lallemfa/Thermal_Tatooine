package scenario;

import java.time.ZonedDateTime;

import spa.ISpaResort;
import spa.Treatment;

public class Scenario implements IScenario {

	private final ISpaResort spa;
	
	public Scenario(ISpaResort spa) {
		this.spa = spa;
	}

	@Override
	public void initScenario(ZonedDateTime startTime, ZonedDateTime endTime) {
		spa.initEvents(startTime, endTime);
		for (Treatment treatment : spa.getTreatments()) {
			treatment.initEvents(startTime, endTime);
		}
	}
}

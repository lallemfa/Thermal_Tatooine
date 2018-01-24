package spa.event;

import java.time.ZonedDateTime;

import engine.event.IEvent;
import spa.treatment.Treatment;

public class AvailableTreatmentEvent implements IEvent {

	private ZonedDateTime scheduledTime;
	private Treatment treatment;

	public AvailableTreatmentEvent(ZonedDateTime scheduledTime, Treatment treatment) {
		this.scheduledTime = scheduledTime;
		this.treatment = treatment;
	}
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process() {
		// TODO prendre patient de waitinglist et mettre dans currentList, setTreatment patient ajouter event fin treatment
		// TRICHEURS
		
	}

}

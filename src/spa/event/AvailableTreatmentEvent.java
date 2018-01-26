package spa.event;

import java.time.ZonedDateTime;

import engine.event.Event;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import spa.treatment.Treatment;

public class AvailableTreatmentEvent extends Event implements IEvent {

	private ZonedDateTime scheduledTime;
	private Treatment treatment;

	public AvailableTreatmentEvent(Object parent, ZonedDateTime scheduledTime, Treatment treatment) {
		super(parent);
		this.scheduledTime = scheduledTime;
		this.treatment = treatment;
	}
	
	@Override
	public ZonedDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	@Override
	public void process(IEventScheduler scheduler) {
		// TODO prendre patient de waitinglist et mettre dans currentList, setTreatment patient ajouter event fin treatment
		// TRICHEURS
		
	}

}

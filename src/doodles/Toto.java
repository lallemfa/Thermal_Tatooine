package doodles;

import java.time.Duration;
import java.time.ZonedDateTime;

import engine.event.FunctionalEvent;
import engine.event.IEventScheduler;

public class Toto {
	
	private ZonedDateTime wakeUpTime;
	private final IEventScheduler scheduler;

	public Toto(IEventScheduler scheduler) {
		this.scheduler = scheduler;
		
	}
	
	public void initialize(ZonedDateTime wakeUpTime) {
		this.wakeUpTime = wakeUpTime;
		scheduler.postEvent(new FunctionalEvent<Object>(this, wakeUpTime, this::wakeUp));
	}
	
	private void wakeUp() {
		System.out.println("Toto woke up");
		scheduler.postEvent(new FunctionalEvent<Object>(this, wakeUpTime.plus(Duration.ofHours(16)), this::goToBed));
	}
	
	private void goToBed() {
		System.out.println("Toto go to bed");
	}

}

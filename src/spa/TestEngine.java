package spa;


import java.time.ZonedDateTime;

import doodles.Toto;
import engine.Engine;
import engine.SortedListScheduler;
import engine.event.MessageEvent;



public class TestEngine {

	public static void main(String[] args) {

		ZonedDateTime time;
		ZonedDateTime startTime;
		ZonedDateTime endTime;
		
		
		SortedListScheduler scheduler = new SortedListScheduler();
		Engine engine = new Engine(scheduler);
		
		startTime 	= ZonedDateTime.parse("2018-01-01T07:00:00+01:00[Europe/Paris]");
		endTime 	= ZonedDateTime.parse("2018-01-02T08:00:00+01:00[Europe/Paris]");
		
		time = ZonedDateTime.parse("2018-01-01T08:00:00+01:00[Europe/Paris]");
		scheduler.postEvent(new MessageEvent(null, time, "Bonjour"));
		
		time = ZonedDateTime.parse("2018-01-01T10:00:00+01:00[Europe/Paris]");
		scheduler.postEvent(new MessageEvent(null, time, "Two hours"));
		
		time = ZonedDateTime.parse("2018-01-01T09:00:00+01:00[Europe/Paris]");
		scheduler.postEvent(new MessageEvent(null, time, "One hour"));
		
		Toto noelie = new Toto(scheduler);
		noelie.initialize(ZonedDateTime.parse("2018-01-01T12:30:00+01:00[Europe/Paris]"));
		
		engine.simulateUntil(startTime, endTime);
		
		System.out.println(startTime.getClass().getName());
		
	}

}

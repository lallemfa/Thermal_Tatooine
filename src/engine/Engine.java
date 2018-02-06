package engine;

import java.time.Duration;
import java.time.ZonedDateTime;

import engine.event.EndEvent;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import engine.scenario.IScenario;
import enstabretagne.base.math.MoreRandom;
import enstabretagne.base.time.LogicalDateTime;
import enstabretagne.simulation.components.IScenarioIdProvider;
import enstabretagne.simulation.components.ScenarioId;
import enstabretagne.simulation.core.ISimulationDateProvider;
import spa.event.EndTreatmentEvent;
import spa.scenario.Scenario;

public class Engine implements ISimulationDateProvider, IScenarioIdProvider {

    private IEventScheduler scheduler;
    private ZonedDateTime currentTime;
    private ScenarioId scenarioId;
    private MoreRandom rng;
    
    public Engine(IEventScheduler scheduler, MoreRandom rng) {
    	scenarioId = new ScenarioId("Scenario1");
        this.scheduler 		= scheduler;
        this.currentTime 	= null;
        this.rng = rng;
    }
    
    
    
    public Engine() {
    	this(new SortedListScheduler(), new MoreRandom());
    }
    
    public Engine(long seed) {
    	this(new SortedListScheduler(), new MoreRandom(seed));
    }
    

    
    public ZonedDateTime getCurrentTime() {
        return currentTime;
    }

    public IEventScheduler getScheduler() {
        return scheduler;
    }

    public void simulateUntil(IScenario scenario, ZonedDateTime startTime, ZonedDateTime endTime) {
        scenario.initScenario(scheduler, startTime, endTime);
        currentTime = startTime;
        IEvent endEvent = new EndEvent(this, endTime);
        scheduler.postEvent(endEvent);
        IEvent currentEvent = scheduler.popNextEvent();
        while (currentEvent != null) {
            if (currentEvent.getScheduledTime().isBefore(currentTime)) {
                throw new IllegalStateException("Trying to simulate an event from the past\n" +
                        "Current time: " + currentTime + "\n" +
                        "Event time: " + currentEvent.getScheduledTime());
            }
			currentTime = currentEvent.getScheduledTime();
			currentEvent.process(scheduler);
            if (currentEvent == endEvent) break;
            currentEvent = scheduler.popNextEvent();
        }
    }

    public void simulateFor(IScenario scenario, ZonedDateTime startTime, Duration duration) {
        simulateUntil(scenario, startTime, startTime.plus(duration));
    }

	@Override
	public LogicalDateTime SimulationDate() {
		return new LogicalDateTime(getCurrentTime().toLocalDateTime());
	}

	@Override
	public ScenarioId getScenarioId() {
		return scenarioId;
	}


	public MoreRandom getRng() {
		return rng;
	}
	

}

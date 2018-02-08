package engine;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import engine.event.EndEvent;
import engine.event.IEvent;
import engine.event.IEventScheduler;
import engine.scenario.IScenario;
import enstabretagne.base.math.MoreRandom;
import enstabretagne.base.time.LogicalDateTime;
import enstabretagne.simulation.components.IScenarioIdProvider;
import enstabretagne.simulation.components.ScenarioId;
import enstabretagne.simulation.core.ISimulationDateProvider;

public class Engine implements ISimulationDateProvider, IScenarioIdProvider {

    private IEventScheduler scheduler;
    private ZonedDateTime currentTime;
    private ScenarioId scenarioId;
    private MoreRandom rng;
    
    private List<IScenario> scenarios = new ArrayList<IScenario>();
    
    public Engine(IEventScheduler scheduler, MoreRandom rng) {
    	scenarioId = new ScenarioId("Scenario 1");
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

    public void addScenario(IScenario scenario) {
    	this.scenarios.add(scenario);
    }
    
    public void simulate() {
    	int compteur = 1;
    	for(IScenario scenario : scenarios) {
    		scenarioId = new ScenarioId("Scenario " + compteur);
    		this.simulate(scenario);
    		compteur += 1;
    	}
    }
    
    public void simulate(IScenario scenario) {
        scenario.initScenario(scheduler);
        currentTime = scenario.getStartTime();
        IEvent endEvent = new EndEvent(this, scenario.getEndTime());
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

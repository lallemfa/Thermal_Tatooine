package engine.event;

public interface IEventScheduler {
	
	IEvent popNextEvent();
	void postEvent(IEvent event);
	boolean hasNoEvent();
	boolean removeEvent(IEvent event);
	int size();
	
}

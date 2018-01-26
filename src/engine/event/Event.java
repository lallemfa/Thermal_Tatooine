package engine.event;

public abstract class Event {

	private final Object parent;
	
	public Event(Object parent) {
		this.parent = parent;
	}
	
	public Object getParent() {
		return parent;
	}
	
}

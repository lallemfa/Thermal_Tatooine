package spa.entity;

import java.util.List;

import engine.event.IEventScheduler;
import engine.event.MessageEvent;

public abstract class Entity {
	
	protected final IEventScheduler scheduler;
	
	private IEntityState state;
	private List<Entity> children;
	
	public Entity(IEventScheduler scheduler) {
		this.scheduler = scheduler;
		this.state = IEntityState.NONE;
	}
	
	public void endConstructor() {
		this.state = IEntityState.BORN;
	}
	
	public void initialize() {
		this.state = IEntityState.IDLE;
	}
	
	public void activate() {
		this.state = IEntityState.ACTIVE;
	}
	
	public void pause() {
		this.state = IEntityState.IDLE;
	}
	
	public void deactivate() {
		for(Entity child : children) {
			child.deactivate();
		}
		this.state = IEntityState.BORN;
	}
	
	public void terminate() {
		this.state = IEntityState.DEAD;
	}

	// Add children
	public void addChildren(Entity child) {
		children.add(child);
	}
	
	// Getter
	public IEntityState getState() {
		return state;
	}

	public List<Entity> getChildren() {
		return children;
	}
	
}

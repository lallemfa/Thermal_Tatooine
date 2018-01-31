package spa.person;

import engine.event.IEventScheduler;
import spa.entity.Entity;
import spa.treatment.Treatment;

public abstract class Person extends Entity {

    protected int id;

    protected PersonState state = PersonState.Out;
    protected Treatment treatment;
    protected boolean patient;

    public Person(IEventScheduler scheduler) {
    	super(scheduler);
    }
    
    public PersonState getPersonState() {
        return state;
    }

    public int getId() {
        return id;
    }

    public Treatment getTreatment() {
        return this.treatment;
    }

	@Override
	public void endConstructor() {
		super.endConstructor();
	}
    
    
}

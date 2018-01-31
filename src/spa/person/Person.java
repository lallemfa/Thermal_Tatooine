package spa.person;

import spa.treatment.Treatment;

public abstract class Person {

    protected int id;

    protected PersonState state = PersonState.Out;
    protected Treatment treatment;
    protected boolean patient;

    public PersonState getPersonState() {
        return this.state;
    }

    public void setPersonState(PersonState state) {
        this.state = state;
    }

    public int getId() {
        return this.id;
    }

    public Treatment getTreatment() {
        return this.treatment;
    }
}

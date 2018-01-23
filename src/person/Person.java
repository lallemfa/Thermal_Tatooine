package person;

import spa.Treatment;

public abstract class Person {

    protected int id;

    protected PersonState state = PersonState.Out;
    protected Treatment treatment;
    protected boolean patient;

    public PersonState getState() {
        return state;
    }

    public int getId() {
        return id;
    }

    public Treatment getTreatment() {
        return this.treatment;
    }
}

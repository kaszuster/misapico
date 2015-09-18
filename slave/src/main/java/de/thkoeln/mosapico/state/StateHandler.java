package de.thkoeln.mosapico.state;

import org.springframework.stereotype.Component;

/**
 * Created by szuster on 15.09.2015.
 */
@Component
public class StateHandler {

    private State currentState = State.IDLE;

    public State getCurrentState() {
        return currentState;
    }

    public boolean isIdle() {
        return currentState == State.IDLE;
    }

    public void idle() {
        currentState = State.IDLE;
    }

    public void analyse() {
        currentState = State.ANALYSE;
    }

    public void build() {
        currentState = State.BUILD;
    }

}

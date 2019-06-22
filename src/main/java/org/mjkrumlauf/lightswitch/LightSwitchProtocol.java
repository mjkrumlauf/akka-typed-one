package org.mjkrumlauf.lightswitch;

import akka.actor.typed.ActorRef;

import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchState.OFF;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchState.ON;

abstract class LightSwitchProtocol {

    enum SwitchState { ON, OFF }

    interface LightSwitchMessage {}

    abstract static class ChangeState implements LightSwitchMessage {
        final long requestId;
        final SwitchState value;
        final ActorRef<StateChanged> replyTo;

        ChangeState(long requestId, SwitchState value, ActorRef<StateChanged> replyTo) {
            this.requestId = requestId;
            this.value = value;
            this.replyTo = replyTo;
        }
    }

    static final class SwitchOn extends ChangeState implements LightSwitchMessage {
        public SwitchOn(long requestId, ActorRef<StateChanged> replyTo) {
            super(requestId, ON, replyTo);
        }
    }

    static final class SwitchOff extends ChangeState implements LightSwitchMessage {
        public SwitchOff(long requestId, ActorRef<StateChanged> replyTo) {
            super(requestId, OFF, replyTo);
        }
    }

    static final class GetState implements LightSwitchMessage {
        final long requestId;
        final ActorRef<GetStateResponse> replyTo;

        public GetState(long requestId, ActorRef<GetStateResponse> replyTo) {
            this.requestId = requestId;
            this.replyTo = replyTo;
        }
    }


    static final class StateChanged {
        final long requestId;

        StateChanged(long requestId) {
            this.requestId = requestId;
        }
    }

    static final class GetStateResponse {
        final long requestId;
        final SwitchState value;

        GetStateResponse(long requestId, SwitchState value) {
            this.requestId = requestId;
            this.value = value;
        }
    }

    private LightSwitchProtocol() {}
}

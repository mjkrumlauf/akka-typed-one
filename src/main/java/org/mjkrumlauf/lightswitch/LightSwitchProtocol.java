package org.mjkrumlauf.lightswitch;

import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchState.*;

import akka.actor.typed.ActorRef;

abstract class LightSwitchProtocol {

    enum SwitchState { ON, OFF }

    interface LightSwitchMessage {}

    abstract static class ChangeState implements LightSwitchMessage {
        final long requestId;
        final SwitchState value;
        final ActorRef<StateChanged> replyTo;

        public ChangeState(long requestId, SwitchState value, ActorRef<StateChanged> replyTo) {
            this.requestId = requestId;
            this.value = value;
            this.replyTo = replyTo;
        }
    }

    static final class TurnOn extends ChangeState implements LightSwitchMessage {
        public TurnOn(long requestId, ActorRef<StateChanged> replyTo) {
            super(requestId, ON, replyTo);
        }
    }

    static final class TurnOff extends ChangeState implements LightSwitchMessage {
        public TurnOff(long requestId, ActorRef<StateChanged> replyTo) {
            super(requestId, OFF, replyTo);
        }
    }

    static final class StateChanged {
        final long requestId;

        public StateChanged(long requestId) {
            this.requestId = requestId;
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

    static final class GetStateResponse {
        final long requestId;
        final SwitchState value;

        public GetStateResponse(long requestId, SwitchState value) {
            this.requestId = requestId;
            this.value = value;
        }
    }

    private LightSwitchProtocol() {}
}

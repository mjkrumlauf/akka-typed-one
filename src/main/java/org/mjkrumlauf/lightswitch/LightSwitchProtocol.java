package org.mjkrumlauf.lightswitch;

import akka.actor.typed.ActorRef;

import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchState.OFF;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchState.ON;

/**
 * Defines the state and communications protocol of interacting
 * with the {@link LightSwitch}; modeling the state, changing the state,
 * and querying the state of the {@link LightSwitch}.
 */
interface LightSwitchProtocol {

    // Represents the state of the LightSwitch
    enum SwitchState { ON, OFF }

    // All LightSwitch messages must implement LightSwitchMessage
    interface LightSwitchMessage {}

    // Base class for all messages that change the LightSwitch state
    abstract class ChangeState implements LightSwitchMessage {
        final long requestId;
        final SwitchState switchState;
        final ActorRef<StateChanged> replyTo;

        ChangeState(long requestId, SwitchState switchState, ActorRef<StateChanged> replyTo) {
            this.requestId = requestId;
            this.switchState = switchState;
            this.replyTo = replyTo;
        }
    }

    // Command message to set the state of a LightSwitch to "on"
    final class SwitchOn extends ChangeState implements LightSwitchMessage {
        public SwitchOn(long requestId, ActorRef<StateChanged> replyTo) {
            super(requestId, ON, replyTo);
        }
    }

    // Command message to set the state of a LightSwitch to "off"
    final class SwitchOff extends ChangeState implements LightSwitchMessage {
        public SwitchOff(long requestId, ActorRef<StateChanged> replyTo) {
            super(requestId, OFF, replyTo);
        }
    }

    // Query message to query the state of a LightSwitch
    final class GetStateRequest implements LightSwitchMessage {
        final long requestId;
        final ActorRef<GetStateResponse> replyTo;

        public GetStateRequest(long requestId, ActorRef<GetStateResponse> replyTo) {
            this.requestId = requestId;
            this.replyTo = replyTo;
        }
    }

    // Reply message to indicate that the state of a LightSwitch has changed
    final class StateChanged {
        final long requestId;

        StateChanged(long requestId) {
            this.requestId = requestId;
        }
    }

    // Reply message containing the current state of a LightSwitch
    final class GetStateResponse {
        final long requestId;
        final SwitchState switchState;

        GetStateResponse(long requestId, SwitchState switchState) {
            this.requestId = requestId;
            this.switchState = switchState;
        }
    }
}

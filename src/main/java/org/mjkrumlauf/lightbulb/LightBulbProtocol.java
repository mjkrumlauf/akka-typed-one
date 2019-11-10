package org.mjkrumlauf.lightbulb;

import akka.actor.typed.ActorRef;

import static org.mjkrumlauf.lightbulb.LightBulbProtocol.BulbState.OFF;
import static org.mjkrumlauf.lightbulb.LightBulbProtocol.BulbState.ON;

/**
 * Defines the state and communications protocol of interacting
 * with the {@link LightBulb}; modeling the state, changing the state,
 * and querying the state of the {@link LightBulb}.
 */
interface LightBulbProtocol {

    // Represents the state of the LightBulb
    enum BulbState { OFF, ON }

    // All LightBulb messages must implement LightBulbMessage
    interface LightBulbMessage {}

    // Base class for all messages that change the LightBulb state
    abstract class ChangeState implements LightBulbMessage {
        final long requestId;
        final BulbState bulbState;
        final ActorRef<StateChanged> replyTo;

        ChangeState(long requestId, BulbState bulbState, ActorRef<StateChanged> replyTo) {
            this.requestId = requestId;
            this.bulbState = bulbState;
            this.replyTo = replyTo;
        }
    }

    // Command message to set the state of a LightBulb to "on"
    final class TurnBulbOn extends ChangeState implements LightBulbMessage {
        public TurnBulbOn(long requestId, ActorRef<StateChanged> replyTo) {
            super(requestId, ON, replyTo);
        }
    }

    // Command message to set the state of a LightBulb to "off"
    final class TurnBulbOff extends ChangeState implements LightBulbMessage {
        public TurnBulbOff(long requestId, ActorRef<StateChanged> replyTo) {
            super(requestId, OFF, replyTo);
        }
    }

    // Query message to query the state of a LightBulb
    final class GetStateRequest implements LightBulbMessage {
        final long requestId;
        final ActorRef<GetStateResponse> replyTo;

        public GetStateRequest(long requestId, ActorRef<GetStateResponse> replyTo) {
            this.requestId = requestId;
            this.replyTo = replyTo;
        }
    }

    // Reply message to indicate that the state of a LightBulb has changed
    final class StateChanged {
        final long requestId;
        final BulbState bulbState;

        StateChanged(long requestId, BulbState bulbState) {
            this.requestId = requestId;
            this.bulbState = bulbState;
        }
    }

    // Reply message containing the current state of a LightBulb
    final class GetStateResponse {
        final long requestId;
        final BulbState bulbState;

        GetStateResponse(long requestId, BulbState bulbState) {
            this.requestId = requestId;
            this.bulbState = bulbState;
        }
    }
}

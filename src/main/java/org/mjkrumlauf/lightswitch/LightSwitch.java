package org.mjkrumlauf.lightswitch;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.UUID;

import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.ChangeState;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.GetStateRequest;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.GetStateResponse;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.LightSwitchMessage;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.StateChanged;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchState;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchState.OFF;

/**
 * This actor models the state and behavior of a simple two-state light switch.
 */
public class LightSwitch extends AbstractBehavior<LightSwitchMessage> {

    public static Behavior<LightSwitchMessage> createBehavior() {
        return Behaviors.setup(LightSwitch::new);
    }

    private final UUID switchId;

    private SwitchState lastSwitchState;

    private LightSwitch(ActorContext<LightSwitchMessage> context) {
        super(context);
        this.switchId = UUID.randomUUID();
        this.lastSwitchState = OFF;
        getContext().getLog().info("LightSwitch actor {} started", switchId);
    }

    @Override
    public Receive<LightSwitchMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(ChangeState.class, this::changeState)
            .onMessage(GetStateRequest.class, this::getState)
            .onSignal(PostStop.class, signal -> postStop())
            .build();
    }

    private Behavior<LightSwitchMessage> changeState(ChangeState msg) {
        lastSwitchState = msg.switchState;
        msg.replyTo.tell(new StateChanged(msg.requestId, msg.switchState));
        return this;
    }

    private Behavior<LightSwitchMessage> getState(GetStateRequest msg) {
        getContext().getLog().info("LightSwitch is {}, requestId {}", lastSwitchState, msg.requestId);
        msg.replyTo.tell(new GetStateResponse(msg.requestId, lastSwitchState));
        return this;
    }

    private Behavior<LightSwitchMessage> postStop() {
        getContext().getLog().info("LightSwitch actor {} stopped", switchId);
        return Behaviors.stopped();
    }

}

package org.mjkrumlauf.lightswitch;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.ChangeState;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.GetState;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.GetStateResponse;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.LightSwitchMessage;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.StateChanged;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchState;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchState.OFF;

public class LightSwitch extends AbstractBehavior<LightSwitchMessage> {

    public static Behavior<LightSwitchMessage> createBehavior(long switchId) {
        return Behaviors.setup(context -> new LightSwitch(context, switchId));
    }

    private final ActorContext<LightSwitchMessage> context;
    private long switchId;
    private SwitchState lastSwitchState = OFF;

    public LightSwitch(ActorContext<LightSwitchMessage> context, long switchId) {
        this.context = context;
        this.switchId = switchId;

        this.context.getLog().info("LightSwitch actor {} started", switchId);
    }

    @Override
    public Receive<LightSwitchMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(ChangeState.class, this::changeState)
            .onMessage(GetState.class, this::getState)
            .onSignal(PostStop.class, signal -> postStop())
            .build();
    }

    private Behavior<LightSwitchMessage> changeState(ChangeState r) {
        lastSwitchState = r.value;
        r.replyTo.tell(new StateChanged(r.requestId));
        return this;
    }

    private Behavior<LightSwitchMessage> getState(GetState msg) {
        context.getLog().info("LightSwitch is {}, requestId {}", lastSwitchState, msg.requestId);
        msg.replyTo.tell(new GetStateResponse(msg.requestId, lastSwitchState));
        return this;
    }

    private Behavior<LightSwitchMessage> postStop() {
        context.getLog().info("LightSwitch actor {} stopped", switchId);
        return Behaviors.stopped();
    }

}

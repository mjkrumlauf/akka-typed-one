package org.mjkrumlauf.lightbulb;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.UUID;

import static org.mjkrumlauf.lightbulb.LightBulbProtocol.BulbState;
import static org.mjkrumlauf.lightbulb.LightBulbProtocol.BulbState.OFF;
import static org.mjkrumlauf.lightbulb.LightBulbProtocol.ChangeState;
import static org.mjkrumlauf.lightbulb.LightBulbProtocol.GetStateRequest;
import static org.mjkrumlauf.lightbulb.LightBulbProtocol.GetStateResponse;
import static org.mjkrumlauf.lightbulb.LightBulbProtocol.LightBulbMessage;
import static org.mjkrumlauf.lightbulb.LightBulbProtocol.StateChanged;

/**
 * This actor models the state and behavior of a simple two-state light bulb.
 */
public class LightBulb extends AbstractBehavior<LightBulbMessage> {

    private static final int MAX_USES = 1;

    public static Behavior<LightBulbMessage> createBehavior() {
        return Behaviors.setup(LightBulb::new);
    }

    private final UUID bulbId;

    private BulbState bulbState;
    private int remainingUses;

    private LightBulb(ActorContext<LightBulbMessage> context) {
        super(context);
        this.bulbId = UUID.randomUUID();
        this.bulbState = OFF;
        this.remainingUses = MAX_USES;
        getContext().getLog().info("LightBulb actor {} started", bulbId);
    }

    @Override
    public Receive<LightBulbMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(ChangeState.class, this::changeState)
            .onMessage(GetStateRequest.class, this::getState)
            .onSignal(PostStop.class, signal -> postStop())
            .build();
    }

    private Behavior<LightBulbMessage> changeState(ChangeState msg) {
        bulbState = msg.bulbState;
        if (msg instanceof LightBulbProtocol.TurnBulbOn) {
            if (remainingUses > 0) {
                remainingUses--;
            } else {
                throw new UsageLimitExceeded(bulbId);
            }
        }
        msg.replyTo.tell(new StateChanged(msg.requestId, msg.bulbState));
        return this;
    }

    private Behavior<LightBulbMessage> getState(GetStateRequest msg) {
        getContext().getLog().info(
            "LightBulb is {}, requestId {}, remainingUses {}", bulbState, msg.requestId, remainingUses);
        msg.replyTo.tell(new GetStateResponse(msg.requestId, bulbState));
        return this;
    }

    private Behavior<LightBulbMessage> postStop() {
        getContext().getLog().info("LightBulb actor {} stopped", bulbId);
        return Behaviors.stopped();
    }

}

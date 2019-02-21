package org.mjkrumlauf.lightswitch;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class LightSwitchSupervisor extends AbstractBehavior<Void> {

    static Behavior<Void> createBehavior() {
        return Behaviors.setup(LightSwitchSupervisor::new);
    }

    private final ActorContext<Void> context;

    private LightSwitchSupervisor(ActorContext<Void> context) {
        this.context = context;
        this.context.getLog().info("OnOff Application started");
    }

    @Override
    public Receive<Void> createReceive() {
        return receiveBuilder()
            .onSignal(PostStop.class, signal -> postStop())
            .build();
    }

    private LightSwitchSupervisor postStop() {
        context.getLog().info("OnOff Application stopped");
        return this;
    }
}

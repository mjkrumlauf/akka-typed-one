package org.mjkrumlauf.iot;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class IotSupervisor extends AbstractBehavior<Void> {

    static Behavior<Void> createBehavior() {
        return Behaviors.setup(IotSupervisor::new);
    }

    private final ActorContext<Void> context;

    private IotSupervisor(ActorContext<Void> context) {
        this.context = context;
        context.getLog().info("IoT Application started");
    }

    // No need to handle any messages
    @Override
    public Receive<Void> createReceive() {
        return newReceiveBuilder().onSignal(PostStop.class, signal -> postStop()).build();
    }

    private IotSupervisor postStop() {
        context.getLog().info("IoT Application stopped");
        return this;
    }
}
//package org.mjkrumlauf.lightbulb;
//
//import akka.actor.typed.Behavior;
//import akka.actor.typed.PostStop;
//import akka.actor.typed.javadsl.AbstractBehavior;
//import akka.actor.typed.javadsl.ActorContext;
//import akka.actor.typed.javadsl.Behaviors;
//import akka.actor.typed.javadsl.Receive;
//
//public class LightBulbSupervisor extends AbstractBehavior<Void> {
//
//    static Behavior<Void> createBehavior() {
//        return Behaviors.setup(LightBulbSupervisor::new);
//    }
//
//    private LightBulbSupervisor(ActorContext<Void> context) {
//        super(context);
//        this.getContext().getLog().info("LightBulb Application started");
//    }
//
//    @Override
//    public Receive<Void> createReceive() {
//        return newReceiveBuilder()
//            .onSignal(PostStop.class, signal -> postStop())
//            .build();
//    }
//
//    private LightBulbSupervisor postStop() {
//        getContext().getLog().info("LightBulb Application stopped");
//        return this;
//    }
//}

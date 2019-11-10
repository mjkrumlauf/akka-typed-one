package org.mjkrumlauf.lightbulb;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.Behaviors;

public class LightBulbMain {

    public static void main(String[] args) {
        // TODO incomplete
        final Behavior<LightBulbProtocol.LightBulbMessage> behavior =
            LightBulb.createBehavior();

        Behaviors.supervise(behavior)
                 .onFailure(UsageLimitExceeded.class, SupervisorStrategy.stop());

        ActorSystem<LightBulbProtocol.LightBulbMessage> actorSystem =
            ActorSystem.create(behavior, "light-bulb-system");

        actorSystem.tell(new LightBulbProtocol.TurnBulbOn(1L, null));
    }
}

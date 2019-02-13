package org.mjkrumlauf.lightswitch;

import akka.actor.typed.ActorSystem;

public class OnOffMain {

    public static void main(String[] args) {
        ActorSystem.create(OnOffSupervisor.createBehavior(), "on-off-system");
    }
}

package org.mjkrumlauf.lightswitch;

import akka.actor.typed.ActorSystem;

public class LightSwitchMain {

    public static void main(String[] args) {
        ActorSystem.create(LightSwitchSupervisor.createBehavior(), "light-switch-system");
    }
}

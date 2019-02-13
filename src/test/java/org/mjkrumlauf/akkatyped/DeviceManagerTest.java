package org.mjkrumlauf.akkatyped;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.ClassRule;
import org.junit.Test;

import static org.mjkrumlauf.akkatyped.DeviceManagerProtocol.*;
import static org.junit.Assert.assertNotEquals;

public class DeviceManagerTest {

    @ClassRule public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void testReplyToRegistrationRequests() {
        TestProbe<DeviceRegistered> probe = testKit.createTestProbe(DeviceRegistered.class);
        ActorRef<DeviceManagerMessage> managerActor = testKit.spawn(DeviceManager.createBehavior());

        managerActor.tell(new RequestTrackDevice("group1", "device", probe.getRef()));
        DeviceRegistered registered1 = probe.receiveMessage();

        // another group
        managerActor.tell(new RequestTrackDevice("group2", "device", probe.getRef()));
        DeviceRegistered registered2 = probe.receiveMessage();
        assertNotEquals(registered1.device, registered2.device);
    }
}
package org.mjkrumlauf.lightswitch;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchState.*;

import org.junit.ClassRule;
import org.junit.Test;
import org.mjkrumlauf.lightswitch.LightSwitchProtocol.*;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;

public class LightSwitchTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void mustReplyWithDefaultState() {
        TestProbe<GetStateResponse> probe = testKit.createTestProbe(GetStateResponse.class);
        long switchId = 0L;

        ActorRef<LightSwitchMessage> lightSwitchActor = testKit.spawn(LightSwitch.createBehavior(switchId));

        long requestId = 1L;
        lightSwitchActor.tell(new GetState(requestId, probe.getRef()));
        GetStateResponse response = probe.receiveMessage();
        assertThat(response.requestId, equalTo(requestId));
        assertThat(response.value, equalTo(OFF));
    }

    @Test
    public void mustChangeStateAndReport() {
        TestProbe<StateChanged> stateChangedProbe = testKit.createTestProbe(StateChanged.class);
        TestProbe<GetStateResponse> responseProbe = testKit.createTestProbe(GetStateResponse.class);
        long switchId = 0L;
        ActorRef<LightSwitchMessage> lightSwitchActor = testKit.spawn(LightSwitch.createBehavior(switchId));

        long requestId1 = 1L;
        lightSwitchActor.tell(new SwitchOn(requestId1, stateChangedProbe.getRef()));
        assertThat(stateChangedProbe.receiveMessage().requestId, equalTo(requestId1));

        long requestId2 = 2L;
        lightSwitchActor.tell(new GetState(requestId2, responseProbe.getRef()));
        GetStateResponse response1 = responseProbe.receiveMessage();
        assertThat(response1.requestId, equalTo(requestId2));
        assertThat(response1.value, equalTo(ON));

        long requestId3 = 3L;
        lightSwitchActor.tell(new SwitchOff(requestId3, stateChangedProbe.getRef()));
        assertThat(stateChangedProbe.receiveMessage().requestId, equalTo(requestId3));

        long requestId4 = 4L;
        lightSwitchActor.tell(new GetState(requestId4, responseProbe.getRef()));
        GetStateResponse response2 = responseProbe.receiveMessage();
        assertThat(response2.requestId, equalTo(requestId4));
        assertThat(response2.value, equalTo(OFF));
    }
}

package org.mjkrumlauf.lightswitch;

import akka.actor.InvalidMessageException;
import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mjkrumlauf.lightswitch.LightSwitchProtocol.GetStateRequest;
import org.mjkrumlauf.lightswitch.LightSwitchProtocol.GetStateResponse;
import org.mjkrumlauf.lightswitch.LightSwitchProtocol.LightSwitchMessage;
import org.mjkrumlauf.lightswitch.LightSwitchProtocol.StateChanged;
import org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchOff;
import org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchOn;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchState.OFF;
import static org.mjkrumlauf.lightswitch.LightSwitchProtocol.SwitchState.ON;

public class LightSwitchTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();
    private ActorRef<LightSwitchMessage> lightSwitchActor;

    @Before
    public void setUp() throws Exception {
        lightSwitchActor = testKit.spawn(LightSwitch.createBehavior());
    }

    @Test
    public void mustReplyWithDefaultState() {
        TestProbe<GetStateResponse> responseProbe = testKit.createTestProbe(GetStateResponse.class);

        // Query LightSwitch state - must be OFF by default
        long requestId = 1L;
        lightSwitchActor.tell(new GetStateRequest(requestId, responseProbe.getRef()));
        GetStateResponse response = responseProbe.receiveMessage();
        assertThat(response.requestId, equalTo(requestId));
        assertThat(response.switchState, equalTo(OFF));
    }

    @Test
    public void mustChangeStateAndReport() {
        TestProbe<StateChanged> stateChangedProbe = testKit.createTestProbe(StateChanged.class);
        TestProbe<GetStateResponse> responseProbe = testKit.createTestProbe(GetStateResponse.class);

        // Turn LightSwitch ON
        long requestId1 = 1L;
        lightSwitchActor.tell(new SwitchOn(requestId1, stateChangedProbe.getRef()));
        assertThat(stateChangedProbe.receiveMessage().requestId, equalTo(requestId1));

        // Query LightSwitch state - must be ON
        long requestId2 = 2L;
        lightSwitchActor.tell(new GetStateRequest(requestId2, responseProbe.getRef()));
        GetStateResponse response1 = responseProbe.receiveMessage();
        assertThat(response1.requestId, equalTo(requestId2));
        assertThat(response1.switchState, equalTo(ON));

        // Turn LightSwitch OFF
        long requestId3 = 3L;
        lightSwitchActor.tell(new SwitchOff(requestId3, stateChangedProbe.getRef()));
        assertThat(stateChangedProbe.receiveMessage().requestId, equalTo(requestId3));

        // Query LightSwitch state - must be OFF
        long requestId4 = 4L;
        lightSwitchActor.tell(new GetStateRequest(requestId4, responseProbe.getRef()));
        GetStateResponse response2 = responseProbe.receiveMessage();
        assertThat(response2.requestId, equalTo(requestId4));
        assertThat(response2.switchState, equalTo(OFF));
    }

    @Test(expected = InvalidMessageException.class)
    public void mustNotSendNullStateChangeMessageToLightSwitch() {
        // Null state change message not allowed
        lightSwitchActor.tell(null);
    }
}

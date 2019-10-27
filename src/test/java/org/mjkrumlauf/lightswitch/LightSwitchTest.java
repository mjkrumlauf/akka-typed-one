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
    private ActorRef<LightSwitchMessage> lightSwitch;

    @Before
    public void setUp() throws Exception {
        lightSwitch = testKit.spawn(LightSwitch.createBehavior());
    }

    @Test
    public void mustReplyWithDefaultState() {
        TestProbe<GetStateResponse> responseProbe = testKit.createTestProbe(GetStateResponse.class);

        // Query LightSwitch state - must be OFF by default
        long requestId = 1L;
        lightSwitch.tell(new GetStateRequest(requestId, responseProbe.getRef()));
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
        lightSwitch.tell(new SwitchOn(requestId1, stateChangedProbe.getRef()));
        final StateChanged stateChanged1 = stateChangedProbe.receiveMessage();
        assertThat(stateChanged1.requestId, equalTo(requestId1));
        assertThat(stateChanged1.switchState, equalTo(ON));

        // Query LightSwitch state - must be ON
        long requestId2 = 2L;
        lightSwitch.tell(new GetStateRequest(requestId2, responseProbe.getRef()));
        GetStateResponse response2 = responseProbe.receiveMessage();
        assertThat(response2.requestId, equalTo(requestId2));
        assertThat(response2.switchState, equalTo(ON));

        // Turn LightSwitch OFF
        long requestId3 = 3L;
        lightSwitch.tell(new SwitchOff(requestId3, stateChangedProbe.getRef()));
        final StateChanged stateChanged3 = stateChangedProbe.receiveMessage();
        assertThat(stateChanged3.requestId, equalTo(requestId3));
        assertThat(stateChanged3.switchState, equalTo(OFF));

        // Query LightSwitch state - must be OFF
        long requestId4 = 4L;
        lightSwitch.tell(new GetStateRequest(requestId4, responseProbe.getRef()));
        GetStateResponse response4 = responseProbe.receiveMessage();
        assertThat(response4.requestId, equalTo(requestId4));
        assertThat(response4.switchState, equalTo(OFF));
    }

    @Test(expected = InvalidMessageException.class)
    public void mustNotSendNullStateChangeMessageToLightSwitch() {
        // Null state change message not allowed
        lightSwitch.tell(null);
    }
}

package org.mjkrumlauf.lightbulb;

import akka.actor.InvalidMessageException;
import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mjkrumlauf.lightbulb.LightBulbProtocol.GetStateRequest;
import org.mjkrumlauf.lightbulb.LightBulbProtocol.GetStateResponse;
import org.mjkrumlauf.lightbulb.LightBulbProtocol.LightBulbMessage;
import org.mjkrumlauf.lightbulb.LightBulbProtocol.StateChanged;
import org.mjkrumlauf.lightbulb.LightBulbProtocol.TurnBulbOff;
import org.mjkrumlauf.lightbulb.LightBulbProtocol.TurnBulbOn;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mjkrumlauf.lightbulb.LightBulbProtocol.BulbState.OFF;
import static org.mjkrumlauf.lightbulb.LightBulbProtocol.BulbState.ON;

public class LightBulbTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();
    private ActorRef<LightBulbMessage> lightBulb;

    @Before
    public void setUp() throws Exception {
        lightBulb = testKit.spawn(LightBulb.createBehavior());
    }

    @Test
    public void mustReplyWithDefaultState() {
        TestProbe<GetStateResponse> responseProbe = testKit.createTestProbe(GetStateResponse.class);

        // Query LightBulb state - must be OFF by default
        var requestId = 1L;
        lightBulb.tell(new GetStateRequest(requestId, responseProbe.getRef()));
        GetStateResponse response = responseProbe.receiveMessage();
        assertThat(response.requestId, equalTo(requestId));
        assertThat(response.bulbState, equalTo(OFF));
    }

    @Test
    public void mustChangeStateAndReport() {
        TestProbe<StateChanged> stateChangedProbe = testKit.createTestProbe(StateChanged.class);
        TestProbe<GetStateResponse> responseProbe = testKit.createTestProbe(GetStateResponse.class);

        // Turn LightBulb ON
        var requestId1 = 1L;
        lightBulb.tell(new TurnBulbOn(requestId1, stateChangedProbe.getRef()));
        StateChanged stateChanged1 = stateChangedProbe.receiveMessage();
        assertThat(stateChanged1.requestId, equalTo(requestId1));
        assertThat(stateChanged1.bulbState, equalTo(ON));

        // Query LightBulb state - must be ON
        var requestId2 = 2L;
        lightBulb.tell(new GetStateRequest(requestId2, responseProbe.getRef()));
        GetStateResponse response2 = responseProbe.receiveMessage();
        assertThat(response2.requestId, equalTo(requestId2));
        assertThat(response2.bulbState, equalTo(ON));

        // Turn LightBulb OFF
        var requestId3 = 3L;
        lightBulb.tell(new TurnBulbOff(requestId3, stateChangedProbe.getRef()));
        StateChanged stateChanged3 = stateChangedProbe.receiveMessage();
        assertThat(stateChanged3.requestId, equalTo(requestId3));
        assertThat(stateChanged3.bulbState, equalTo(OFF));

        // Query LightBulb state - must be OFF
        var requestId4 = 4L;
        lightBulb.tell(new GetStateRequest(requestId4, responseProbe.getRef()));
        GetStateResponse response4 = responseProbe.receiveMessage();
        assertThat(response4.requestId, equalTo(requestId4));
        assertThat(response4.bulbState, equalTo(OFF));
    }

    @Test(expected = InvalidMessageException.class)
    public void mustNotSendNullStateChangeMessageToLightBulb() {
        // Null state change message not allowed
        lightBulb.tell(null);
    }
}

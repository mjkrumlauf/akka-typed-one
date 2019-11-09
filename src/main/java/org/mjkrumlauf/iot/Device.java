package org.mjkrumlauf.iot;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

import static org.mjkrumlauf.iot.DeviceProtocol.DeviceMessage;
import static org.mjkrumlauf.iot.DeviceProtocol.Passivate;
import static org.mjkrumlauf.iot.DeviceProtocol.ReadTemperature;
import static org.mjkrumlauf.iot.DeviceProtocol.RecordTemperature;
import static org.mjkrumlauf.iot.DeviceProtocol.RespondTemperature;
import static org.mjkrumlauf.iot.DeviceProtocol.TemperatureRecorded;

public class Device extends AbstractBehavior<DeviceMessage> {

    public static Behavior<DeviceMessage> createBehavior(String groupId, String deviceId) {
        return Behaviors.setup(context -> new Device(context, groupId, deviceId));
    }

    private final String groupId;
    private final String deviceId;

    private Optional<Double> lastTemperatureReading = Optional.empty();

    public Device(ActorContext<DeviceMessage> context, String groupId, String deviceId) {
        super(context);
        this.groupId = groupId;
        this.deviceId = deviceId;

        getContext().getLog().info("Device actor {}-{} started", groupId, deviceId);
    }

    @Override
    public Receive<DeviceMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(RecordTemperature.class, this::recordTemperature)
                .onMessage(ReadTemperature.class, this::readTemperature)
                .onMessage(Passivate.class, m -> Behaviors.stopped())
                .onSignal(PostStop.class, signal -> postStop())
                .build();
    }

    private Behavior<DeviceMessage> recordTemperature(RecordTemperature r) {
        getContext().getLog().info("Recorded temperature reading {} with {}", r.value, r.requestId);
        lastTemperatureReading = Optional.of(r.value);
        r.replyTo.tell(new TemperatureRecorded(r.requestId));
        return this;
    }

    private Behavior<DeviceMessage> readTemperature(ReadTemperature r) {
        r.replyTo.tell(new RespondTemperature(r.requestId, deviceId, lastTemperatureReading));
        return this;
    }

    private Behavior<DeviceMessage> postStop() {
        getContext().getLog().info("Device actor {}-{} stopped", groupId, deviceId);
        return Behaviors.stopped();
    }
}
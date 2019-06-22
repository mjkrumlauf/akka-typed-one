package org.mjkrumlauf.iot;

import akka.actor.typed.ActorRef;

import java.util.Optional;

abstract class DeviceProtocol {

    interface DeviceMessage {}

    static final class RecordTemperature implements DeviceMessage {
        final long requestId;
        final double value;
        final ActorRef<TemperatureRecorded> replyTo;

        RecordTemperature(long requestId, double value, ActorRef<TemperatureRecorded> replyTo) {
            this.requestId = requestId;
            this.value = value;
            this.replyTo = replyTo;
        }
    }


    static final class TemperatureRecorded {
        final long requestId;

        TemperatureRecorded(long requestId) {
            this.requestId = requestId;
        }
    }


    static final class ReadTemperature implements DeviceMessage {
        final long requestId;
        final ActorRef<RespondTemperature> replyTo;

        ReadTemperature(long requestId, ActorRef<RespondTemperature> replyTo) {
            this.requestId = requestId;
            this.replyTo = replyTo;
        }
    }


    static final class RespondTemperature {
        final long requestId;
        final String deviceId;
        final Optional<Double> value;

        RespondTemperature(long requestId, String deviceId, Optional<Double> value) {
            this.requestId = requestId;
            this.deviceId = deviceId;
            this.value = value;
        }
    }


    enum Passivate implements DeviceMessage { INSTANCE }

    // no instances of DeviceProtocol class
    private DeviceProtocol() {}

}
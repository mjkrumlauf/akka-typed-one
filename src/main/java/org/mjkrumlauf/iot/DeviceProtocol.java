package org.mjkrumlauf.iot;

import akka.actor.typed.ActorRef;

import java.util.Optional;

interface DeviceProtocol {

    // All Device messages implement this interface
    interface DeviceMessage {}

    // Command message to record the temperature of a Device
    final class RecordTemperature implements DeviceMessage {
        final long requestId;
        final double value;
        final ActorRef<TemperatureRecorded> replyTo;

        RecordTemperature(long requestId, double value, ActorRef<TemperatureRecorded> replyTo) {
            this.requestId = requestId;
            this.value = value;
            this.replyTo = replyTo;
        }
    }

    // Reply message to indicate that a Device temperature has been recorded
    final class TemperatureRecorded {
        final long requestId;

        TemperatureRecorded(long requestId) {
            this.requestId = requestId;
        }
    }

    // Command message to query the temperature of a Device
    final class ReadTemperature implements DeviceMessage {
        final long requestId;
        final ActorRef<RespondTemperature> replyTo;

        ReadTemperature(long requestId, ActorRef<RespondTemperature> replyTo) {
            this.requestId = requestId;
            this.replyTo = replyTo;
        }
    }

    // Reply message containing the temperature of the Device indicated by deviceId
    final class RespondTemperature {
        final long requestId;
        final String deviceId;
        final Optional<Double> value;

        RespondTemperature(long requestId, String deviceId, Optional<Double> value) {
            this.requestId = requestId;
            this.deviceId = deviceId;
            this.value = value;
        }
    }

    // Command message to shut down (passivate) a Device
    enum Passivate implements DeviceMessage { INSTANCE }
}
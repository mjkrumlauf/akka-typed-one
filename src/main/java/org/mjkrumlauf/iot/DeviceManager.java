package org.mjkrumlauf.iot;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mjkrumlauf.iot.DeviceManagerProtocol.DeviceGroupMessage;
import static org.mjkrumlauf.iot.DeviceManagerProtocol.DeviceManagerMessage;
import static org.mjkrumlauf.iot.DeviceManagerProtocol.ReplyDeviceList;
import static org.mjkrumlauf.iot.DeviceManagerProtocol.RequestDeviceList;
import static org.mjkrumlauf.iot.DeviceManagerProtocol.RequestTrackDevice;

// #device-manager-full
public class DeviceManager extends AbstractBehavior<DeviceManagerMessage> {

    public static Behavior<DeviceManagerMessage> createBehavior() {
        return Behaviors.setup(DeviceManager::new);
    }

    private static class DeviceGroupTerminated implements DeviceManagerMessage {
        public final String groupId;

        DeviceGroupTerminated(String groupId) {
            this.groupId = groupId;
        }
    }

    private final Map<String, ActorRef<DeviceGroupMessage>> groupIdToActor = new HashMap<>();

    public DeviceManager(ActorContext<DeviceManagerMessage> context) {
        super(context);
        getContext().getLog().info("DeviceManager started");
    }

    private DeviceManager onTrackDevice(RequestTrackDevice trackMsg) {
        String groupId = trackMsg.groupId;
        ActorRef<DeviceGroupMessage> ref = groupIdToActor.get(groupId);
        if (ref != null) {
            ref.tell(trackMsg);
        } else {
            getContext().getLog().info("Creating device group actor for {}", groupId);
            ActorRef<DeviceGroupMessage> groupActor =
                    getContext().spawn(DeviceGroup.createBehavior(groupId), "group-" + groupId);
            getContext().watchWith(groupActor, new DeviceGroupTerminated(groupId));
            groupActor.tell(trackMsg);
            groupIdToActor.put(groupId, groupActor);
        }
        return this;
    }

    private DeviceManager onRequestDeviceList(RequestDeviceList request) {
        ActorRef<DeviceGroupMessage> ref = groupIdToActor.get(request.groupId);
        if (ref != null) {
            ref.tell(request);
        } else {
            request.replyTo.tell(new ReplyDeviceList(request.requestId, Collections.emptySet()));
        }
        return this;
    }

    private DeviceManager onTerminated(DeviceGroupTerminated t) {
        getContext().getLog().info("Device group actor for {} has been terminated", t.groupId);
        groupIdToActor.remove(t.groupId);
        return this;
    }

    public Receive<DeviceManagerMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(RequestTrackDevice.class, this::onTrackDevice)
                .onMessage(RequestDeviceList.class, this::onRequestDeviceList)
                .onMessage(DeviceGroupTerminated.class, this::onTerminated)
                .onSignal(PostStop.class, signal -> postStop())
                .build();
    }

    private DeviceManager postStop() {
        getContext().getLog().info("DeviceManager stopped");
        return this;
    }
}
package com.lilith.lilithsvotes.events;

import net.minecraftforge.eventbus.api.Event;

public class VoteReceivedEvent extends Event {
    private final String serviceName;
    private final String username;
    private final String address;
    private final String timeStamp;

    public VoteReceivedEvent(String serviceName, String username, String address, String timeStamp) {
        this.serviceName = serviceName;
        this.username = username;
        this.address = address;
        this.timeStamp = timeStamp;
    }

    public String getServiceName() { return serviceName; }
    public String getUsername() { return username; }
    public String getAddress() { return address; }
    public String getTimeStamp() { return timeStamp; }
}

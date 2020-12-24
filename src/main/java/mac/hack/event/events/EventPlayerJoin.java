package mac.hack.event.events;


import mac.hack.event.Event;

public class EventPlayerJoin extends Event
{
    private final String name;

    public EventPlayerJoin(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
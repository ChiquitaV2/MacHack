package mac.hack.event.events;


import mac.hack.event.Event;

public class EventPlayerLeave extends Event
{
    private final String name;

    public EventPlayerLeave(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
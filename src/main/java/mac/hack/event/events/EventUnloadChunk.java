package mac.hack.event.events;

import mac.hack.event.Event;

public class EventUnloadChunk extends Event {
    private int x;
    private int z;

    public EventUnloadChunk(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
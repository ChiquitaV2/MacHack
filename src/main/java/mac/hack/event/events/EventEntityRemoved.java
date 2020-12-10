package mac.hack.event.events;

import mac.hack.event.Event;
import net.minecraft.entity.Entity;

public class EventEntityRemoved extends Event
{
    private Entity m_Entity;

    public EventEntityRemoved(Entity p_Entity)
    {
        m_Entity = p_Entity;
    }

    public Entity GetEntity()
    {
        return m_Entity;
    }
}
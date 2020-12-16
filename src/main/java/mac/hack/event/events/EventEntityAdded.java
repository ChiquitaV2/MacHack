package mac.hack.event.events;


import mac.hack.event.Event;
import net.minecraft.entity.Entity;

public class EventEntityAdded extends Event
{
    private Entity m_Entity;

    public EventEntityAdded(Entity p_Entity)
    {
        m_Entity = p_Entity;
    }

    public Entity GetEntity()
    {
        return m_Entity;
    }
}
package mac.hack.module.mods;

import mac.hack.module.Category;
import mac.hack.module.Module;
import net.minecraft.util.math.Vec3d;

public class FakePlayer extends Module {

    private mac.hack.utils.FakePlayer fake;
    private Vec3d oldPos;
    private Vec3d newPos;

    public FakePlayer() {
        super("FakePlayer", KEY_UNBOUND, Category.MISC, "Creates fake player");
    }

    public void
    onEnable()
    {
        if (mc.player != null) {
            oldPos = mc.player.getPos();

            // fake.setAbsorptionAmount(69);
            fake = new mac.hack.utils.FakePlayer();
            fake.copyPositionAndRotation(mc.player);
            fake.copyFrom(mc.player);
            fake.setBoundingBox(fake.getBoundingBox().expand(0.1));
            fake.spawn();
            fake.abilities.invulnerable = true;
        }
        super.onEnable();
    }

    public void
    onDisable()
    {
        if (fake != null) {
            newPos = mc.player.getPos();
            fake.despawn();
            mc.player.setPos(newPos.x, newPos.y, newPos.z);
        }
        super.onDisable();
    }

}

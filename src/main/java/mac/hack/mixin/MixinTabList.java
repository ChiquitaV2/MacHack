package mac.hack.mixin;

import mac.hack.module.ModuleManager;
import mac.hack.module.mods.TablistTweaks;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(PlayerListHud.class)
public class MixinTabList {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;subList(II)Ljava/util/List;"))
    public List<PlayerListEntry> subList(List<PlayerListEntry> list, int fromIndex, int toIndex) {
        return list.subList(0, (int) Math.min(list.size(), ModuleManager.getModule(TablistTweaks.class).getSetting(2).asSlider().getValue()));
    }

}

package mac.hack.command.commands;

import mac.hack.command.Command;
import mac.hack.utils.MacLogger;
import mac.hack.utils.MacQueue;
import mac.hack.utils.ItemContentUtils;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;

import java.util.List;

public class CmdPeek extends Command {

	@Override
	public String getAlias() {
		return "peek";
	}

	@Override
	public String getDescription() {
		return "Shows whats inside a container";
	}

	@Override
	public String getSyntax() {
		return "peek";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception {
		ItemStack item = mc.player.inventory.getMainHandStack();

		if (!(item.getItem() instanceof BlockItem)) {
			MacLogger.errorMessage("Must be holding a containter to peek.");
			return;
		}

		if (!(((BlockItem) item.getItem()).getBlock() instanceof ShulkerBoxBlock)
				&& !(((BlockItem) item.getItem()).getBlock() instanceof ChestBlock)
				&& !(((BlockItem) item.getItem()).getBlock() instanceof DispenserBlock)
				&& !(((BlockItem) item.getItem()).getBlock() instanceof HopperBlock)) {
			MacLogger.errorMessage("Must be holding a containter to peek.");
			return;
		}

		List<ItemStack> items = ItemContentUtils.getItemsInContainer(item);

		SimpleInventory inv = new SimpleInventory(items.toArray(new ItemStack[27]));

		MacQueue.add(() -> {
			mc.openScreen(new PeekShulkerScreen(
					new ShulkerBoxScreenHandler(420, mc.player.inventory, inv),
					mc.player.inventory,
					item.getName()));
		});
	}

	class PeekShulkerScreen extends ShulkerBoxScreen {

		public PeekShulkerScreen(ShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title) {
			super(handler, inventory, title);
		}

		public boolean mouseClicked(double double_1, double double_2, int int_1) {
			return false;
		}
	}

}
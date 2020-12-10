/*
 * This file is part of the MacHack distribution (https://github.com/BleachDrinker420/bleachhack-1.14/).
 * Copyright (c) 2019 Bleach.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mac.hack.mixin;

import mac.hack.MacHack;
import mac.hack.command.Command;
import mac.hack.command.CommandManager;
import mac.hack.event.events.EventReadPacket;
import mac.hack.event.events.EventSendPacket;
import mac.hack.module.ModuleManager;
import mac.hack.module.mods.AntiChunkBan;
import mac.hack.utils.MacLogger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketEncoderException;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {
	@Shadow
	private Channel channel;

	@Shadow
	private void sendImmediately(Packet<?> packet_1, GenericFutureListener<? extends Future<? super Void>> genericFutureListener_1) {
	}

	@Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
	public void channelRead0(ChannelHandlerContext channelHandlerContext_1, Packet<?> packet_1, CallbackInfo callback) {
		if (this.channel.isOpen() && packet_1 != null) {
			try {
				EventReadPacket event = new EventReadPacket(packet_1);
				MacHack.eventBus.post(event);
				if (event.isCancelled()) callback.cancel();
			} catch (Exception exception) {
			}
		}
	}

	@Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"), cancellable = true)
	public void send(Packet<?> packet_1, GenericFutureListener<? extends Future<? super Void>> genericFutureListener_1, CallbackInfo callback) {
		if (packet_1 instanceof ChatMessageC2SPacket) {
			ChatMessageC2SPacket pack = (ChatMessageC2SPacket) packet_1;
			if (pack.getChatMessage().startsWith(Command.PREFIX)) {
				CommandManager.callCommand(pack.getChatMessage().substring(Command.PREFIX.length()));
				callback.cancel();
			}
		}

		EventSendPacket event = new EventSendPacket(packet_1);
		MacHack.eventBus.post(event);

		if (event.isCancelled()) callback.cancel();
	}

	// Packet kick blocc
	@Inject(method = "exceptionCaught(Lio/netty/channel/ChannelHandlerContext;Ljava/lang/Throwable;)V", at = @At("HEAD"), cancellable = true)
	public void exceptionCaught(ChannelHandlerContext channelHandlerContext_1, Throwable throwable_1, CallbackInfo callback) {
		if (!ModuleManager.getModule(AntiChunkBan.class).isToggled()) return;

		if (!(throwable_1 instanceof PacketEncoderException)) {
			MacLogger.warningMessage("Canceled Defect Packet: " + throwable_1);
			callback.cancel();
		}
	}
}
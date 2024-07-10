package dev.xkmc.l2damagetracker.contents.curios;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record TotemUseToClient(int id, UUID uid, ItemStack item)
		implements SerialPacketBase<TotemUseToClient> {

	public static TotemUseToClient of(Entity entity, ItemStack stack) {
		return new TotemUseToClient(entity.getId(), entity.getUUID(), stack.copy());
	}

	@Override
	public void handle(Player player) {
		ClientHandlers.handleTotemUse(item, id, uid);
	}

}

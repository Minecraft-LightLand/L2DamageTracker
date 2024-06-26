package dev.xkmc.l2damagetracker.contents.curios;

import dev.xkmc.l2library.util.Proxy;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class ClientHandlers {

	public static void handleTotemUse(ItemStack item, int id, UUID uid) {
		Level level = Proxy.getClientWorld();
		if (level == null) return;
		Entity entity = level.getEntity(id);
		if (entity == null) return;
		if (!entity.getUUID().equals(uid)) return;
		if (item.isEmpty() || !(item.getItem() instanceof L2Totem totem)) return;
		totem.onClientTrigger(entity, item);
	}

}

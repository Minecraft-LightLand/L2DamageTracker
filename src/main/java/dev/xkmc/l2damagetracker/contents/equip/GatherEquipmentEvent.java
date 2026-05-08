package dev.xkmc.l2damagetracker.contents.equip;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

import java.util.List;

public class GatherEquipmentEvent extends Event {

	private final LivingEntity entity;
	private final List<ItemStack> list;

	public GatherEquipmentEvent(LivingEntity entity, List<ItemStack> list) {
		this.entity = entity;
		this.list = list;
	}

	public LivingEntity getEntity() {
		return entity;
	}

}

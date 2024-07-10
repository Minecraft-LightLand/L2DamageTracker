package dev.xkmc.l2damagetracker.contents.materials.generic;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExtraArmorConfig {

	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity) {
		return amount;
	}

	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
	}

	public void modify(ItemAttributeModifiers.Builder builder, EquipmentSlot slot, ItemStack stack) {

	}

	public void onArmorTick(ItemStack stack, Level world, Player player) {
	}

	public void addTooltip(ItemStack stack, List<Component> list) {
	}

}

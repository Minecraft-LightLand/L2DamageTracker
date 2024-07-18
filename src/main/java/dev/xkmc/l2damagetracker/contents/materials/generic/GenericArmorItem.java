package dev.xkmc.l2damagetracker.contents.materials.generic;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class GenericArmorItem extends ArmorItem {

	private final ExtraArmorConfig config;

	public GenericArmorItem(Holder<ArmorMaterial> material, Type slot, Properties prop, ExtraArmorConfig config) {
		super(material, slot, prop);
		this.config = config;
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
		return config.damageItem(stack, amount, entity);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		config.inventoryTick(stack, level, entity, slot, selected);
		if (entity instanceof Player player && player.getItemBySlot(getEquipmentSlot()).getItem() == this)
			config.onArmorTick(stack, level, player);
	}

	public ExtraArmorConfig getConfig() {
		return config;
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		var parent = super.getDefaultAttributeModifiers(stack);
		var b = ItemAttributeModifiers.builder();
		for (var e : parent.modifiers()) b.add(e.attribute(), e.modifier(), e.slot());
		config.modifyDynamicAttributes(b, getEquipmentSlot(), stack);
		return b.build();
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
		config.addTooltip(stack, list);
	}

}

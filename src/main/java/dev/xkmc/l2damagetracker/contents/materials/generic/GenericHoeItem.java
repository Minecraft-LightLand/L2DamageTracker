package dev.xkmc.l2damagetracker.contents.materials.generic;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class GenericHoeItem extends HoeItem implements GenericTieredItem {

	private final ExtraToolConfig config;

	public GenericHoeItem(Tier tier, Properties prop, ExtraToolConfig config) {
		super(tier, prop);
		this.config = config;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		config.inventoryTick(stack, level, entity, slot, selected);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
		return config.damageItem(stack, amount, entity);
	}

	@Override
	public ExtraToolConfig getExtraConfig() {
		return config;
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		var parent = super.getDefaultAttributeModifiers(stack);
		var b = ItemAttributeModifiers.builder();
		for (var e : parent.modifiers()) b.add(e.attribute(), e.modifier(), e.slot());
		config.modify(b, stack);
		return b.build();
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		float old = super.getDestroySpeed(stack, state);
		return config.getDestroySpeed(stack, state, old);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
		config.addTooltip(stack, list);
	}


}

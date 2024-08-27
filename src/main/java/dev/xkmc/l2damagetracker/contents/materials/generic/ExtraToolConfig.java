package dev.xkmc.l2damagetracker.contents.materials.generic;

import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.materials.api.IMatVanillaType;
import dev.xkmc.l2damagetracker.contents.materials.vanilla.GenItemVanillaType;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class ExtraToolConfig {

	public Function<IMatVanillaType, Item> stick = e -> Items.STICK;
	public boolean reversed = false;
	public Function<Integer, TagKey<Block>> tier = GenItemVanillaType::getBlockTag;
	private TagKey<Item>[] tags = new TagKey[0];

	@SafeVarargs
	public final ExtraToolConfig tags(TagKey<Item>... tags){
		this.tags = tags;
		return this;
	}

	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity) {
		return amount;
	}

	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
	}

	public ExtraToolConfig setStick(Function<IMatVanillaType, Item> sup, boolean reverse) {
		this.stick = sup;
		this.reversed = reverse;
		return this;
	}

	public ExtraToolConfig setTier(Function<Integer, TagKey<Block>> tag) {
		tier = tag;
		return this;
	}

	public void configureAttributes(ItemAttributeModifiers.Builder builder) {
	}

	@Deprecated(forRemoval = true)
	public void modifyDynamicAttributes(ItemAttributeModifiers.Builder builder, ItemStack stack) {

	}


	public float getDestroySpeed(ItemStack stack, BlockState state, float old) {
		return old;
	}

	public TagKey<Block> getTier(int level) {
		return tier.apply(level);
	}

	public void addTooltip(ItemStack stack, List<Component> list) {
	}

	public void onDamage(DamageData.Offence cache, ItemStack stack) {

	}

	public TagKey<Item>[] tags() {
		return tags;
	}

}

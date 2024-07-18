package dev.xkmc.l2damagetracker.contents.materials.api;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2damagetracker.contents.materials.vanilla.Tools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface IMatVanillaType extends IMatToolType, IMatArmorType {

	int ordinal();

	ItemEntry<Item>[][] getGenerated();

	ResourceLocation id();

	Item getNugget();

	Item getIngot();

	Block getBlock();

	default Item getArmor(EquipmentSlot slot) {
		return getGenerated()[ordinal()][slot.getIndex()].get();
	}

	default Item getTool(Tools tool) {
		return getGenerated()[ordinal()][4 + tool.ordinal()].get();
	}

	default Item getToolIngot() {
		var tool_extra = getExtraToolConfig();
		return tool_extra.reversed ? tool_extra.stick.apply(this) : getIngot();
	}

	default Item getToolStick() {
		var tool_extra = getExtraToolConfig();
		return tool_extra.reversed ? getIngot() : tool_extra.stick.apply(this);
	}

	String getID();

}

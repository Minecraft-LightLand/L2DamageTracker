package dev.xkmc.l2damagetracker.contents.materials.api;

import dev.xkmc.l2damagetracker.contents.materials.generic.ExtraArmorConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public interface IMatArmorType {

	Holder<ArmorMaterial> getArmorMaterial();

	ArmorConfig getArmorConfig();

	ExtraArmorConfig getExtraArmorConfig();

	int armorDurability();

	default void defaultAttributes(ItemAttributeModifiers.Builder builder, ArmorItem.Type slot) {
		var armorMat = getArmorMaterial().value();
		int armor = armorMat.getDefense(slot);
		float tough = armorMat.toughness();
		float kbres = armorMat.knockbackResistance();
		EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(slot.getSlot());
		ResourceLocation id = ResourceLocation.withDefaultNamespace("armor." + slot.getName());
		if (armor > 0) {
			builder.add(Attributes.ARMOR, new AttributeModifier(id, armor, AttributeModifier.Operation.ADD_VALUE), group);
		}
		if (tough > 0) {
			builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, tough, AttributeModifier.Operation.ADD_VALUE), group);
		}
		if (kbres > 0) {
			builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(id, kbres, AttributeModifier.Operation.ADD_VALUE), group);
		}

		getExtraArmorConfig().configureAttributes(builder, slot.getSlot());
	}
}

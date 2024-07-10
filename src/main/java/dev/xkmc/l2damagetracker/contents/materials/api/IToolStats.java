package dev.xkmc.l2damagetracker.contents.materials.api;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import static net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID;
import static net.minecraft.world.item.Item.BASE_ATTACK_SPEED_ID;

public interface IToolStats {

	static void configure(ItemAttributeModifiers.Builder builder, int dmg, float speed) {

		builder.add(
				Attributes.ATTACK_DAMAGE,
				new AttributeModifier(BASE_ATTACK_DAMAGE_ID, dmg, AttributeModifier.Operation.ADD_VALUE),
				EquipmentSlotGroup.MAINHAND
		);
		builder.add(
				Attributes.ATTACK_SPEED,
				new AttributeModifier(BASE_ATTACK_SPEED_ID, speed - 4, AttributeModifier.Operation.ADD_VALUE),
				EquipmentSlotGroup.MAINHAND
		);
	}

	int durability();

	int speed();

	int enchant();

	void configure(ITool tool, ItemAttributeModifiers.Builder builder);
}

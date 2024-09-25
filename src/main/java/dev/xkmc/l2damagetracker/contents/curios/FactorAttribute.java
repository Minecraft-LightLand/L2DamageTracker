package dev.xkmc.l2damagetracker.contents.curios;

import dev.xkmc.l2damagetracker.init.data.L2DTLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;

import javax.annotation.Nullable;

public class FactorAttribute extends RangedAttribute {

	public FactorAttribute(String pDescriptionId, double pDefaultValue, double pMin, double pMax) {
		super(pDescriptionId, pDefaultValue, pMin, pMax);
	}

	@Override
	public MutableComponent toValueComponent(@Nullable AttributeModifier.Operation op, double value, TooltipFlag flag) {
		if (IAttributeExtension.isNullOrAddition(op))
			return Component.translatable("neoforge.value.percent", FORMAT.format(value * 100));
		return Component.translatable("neoforge.value.flat", FORMAT.format(1 + value));
	}

	@Override
	public MutableComponent toComponent(AttributeModifier modif, TooltipFlag flag) {
		if (modif.operation() == AttributeModifier.Operation.ADD_VALUE)
			return super.toComponent(modif, flag);
		double value = modif.amount();
		ChatFormatting color = getStyle(value >= 0);
		Component attrDesc = Component.translatable(getDescriptionId());
		Component valueComp = this.toValueComponent(modif.operation(), value, flag);
		MutableComponent comp = L2DTLangData.MULT.get(valueComp, attrDesc).withStyle(color);
		return comp.append(this.getDebugInfo(modif, flag));
	}

}

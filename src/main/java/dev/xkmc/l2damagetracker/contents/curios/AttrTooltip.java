package dev.xkmc.l2damagetracker.contents.curios;

import com.google.common.collect.Multimap;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

public class AttrTooltip {

	public static MutableComponent getDesc(Holder<Attribute> attr, double val, AttributeModifier.Operation op) {
		return getDesc(attr, val, op, null, (val < 0 ^ attr.is(L2DamageTracker.NEGATIVE)) ? ChatFormatting.RED : ChatFormatting.BLUE);
	}

	public static MutableComponent getText(Holder<Attribute> attr, double val, AttributeModifier.Operation op) {
		return getDesc(attr, val, op, ChatFormatting.AQUA, ChatFormatting.GRAY);
	}

	public static MutableComponent getDesc(Holder<Attribute> attr, double val, AttributeModifier.Operation op,
										   @Nullable ChatFormatting num, ChatFormatting all) {
		var text = Component.translatable(attr.value().getDescriptionId());
		MutableComponent base;
		if (attr.is(L2DamageTracker.PERCENTAGE)){
			if (op == AttributeModifier.Operation.ADD_VALUE) {
				base = Component.literal(val < 0 ? "-" : "+");
				base.append(ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(val * 100)));
				base.append("%");

			} else {
				base = Component.literal("x");
				base.append(ATTRIBUTE_MODIFIER_FORMAT.format(val + 1));
			}
		} else{
			base = Component.literal(val < 0 ? "-" : "+");
			if (op == AttributeModifier.Operation.ADD_VALUE) {
				base.append(ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(val)));
			} else {
				base.append(ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(val * 100)));
				base.append("%");
			}
		}
		if (num != null)
			base = Component.empty().append(base.withStyle(num));
		base.append(" ");
		base.append(text);
		return base.withStyle(all);
	}

	public static List<Component> modifyTooltip(List<Component> tooltips, Multimap<Holder<Attribute>, AttributeModifier> attributes, boolean remove) {
		Map<TooltipDetail, Integer> map = analyzeTooltip(tooltips);
		for (var ent : attributes.entries()) {
			var attr = ent.getKey();
			double val = ent.getValue().amount();
			var op = ent.getValue().operation();
			var key = new TooltipDetail(attr.value().getDescriptionId(), op.id());
			Integer index = map.get(key);
			if (index == null) continue;
			map.remove(key);
			if (remove) {
				tooltips.set(index, null);
			} else {
				MutableComponent rep = null;
				if (attr.is(L2DamageTracker.PERCENTAGE) || attr.is(L2DamageTracker.NEGATIVE)) {
					rep = getDesc(attr, val, op);
				}
				if (rep != null) {
					tooltips.set(index, rep);
				}
			}
		}
		tooltips.removeIf(Objects::isNull);
		return tooltips;
	}

	private static Map<TooltipDetail, Integer> analyzeTooltip(List<Component> tooltips) {
		Map<TooltipDetail, Integer> map = new LinkedHashMap<>();
		for (int i = 0; i < tooltips.size(); i++) {
			var txt = tooltips.get(i);
			if (txt.getContents() instanceof TranslatableContents tr) {
				var args = tr.getArgs();
				if (args.length == 2 && args[1] instanceof MutableComponent comp) {
					if (comp.getContents() instanceof TranslatableContents sub) {
						int op = tr.getKey().charAt(tr.getKey().length() - 1) - 48;
						TooltipDetail key = new TooltipDetail(sub.getKey(), op);
						if (!map.containsKey(key) && op >= 0 && op <= 2) {
							map.put(key, i);
						}
					}
				}
			}
		}
		return map;
	}

	public record TooltipDetail(String id, int op) {
	}

}

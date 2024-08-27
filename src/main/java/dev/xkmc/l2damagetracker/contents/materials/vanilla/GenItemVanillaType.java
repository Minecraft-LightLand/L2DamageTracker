package dev.xkmc.l2damagetracker.contents.materials.vanilla;

import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.util.MathHelper;
import dev.xkmc.l2damagetracker.contents.materials.api.*;
import dev.xkmc.l2damagetracker.contents.materials.generic.GenericArmorItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.Locale;
import java.util.function.BiFunction;

@SuppressWarnings({"unchecked", "rawtypes", "unsafe"})
public record GenItemVanillaType(String modid, L2Registrate registrate) {

	private static final int[] DURABILITY = new int[]{13, 15, 16, 11};

	public static final ToolConfig TOOL_GEN = new ToolConfig(GenItemVanillaType::genGenericTool);
	public static final ArmorConfig ARMOR_GEN = new ArmorConfig(GenItemVanillaType::genGenericArmor);

	public static Item genGenericTool(IMatToolType mat, ITool tool, Item.Properties prop) {
		var builder = ItemAttributeModifiers.builder();
		mat.getToolStats().configure(tool, builder);
		mat.getExtraToolConfig().configureAttributes(builder);
		prop.attributes(builder.build());
		return tool.create(mat.getTier(), prop, mat.getExtraToolConfig());
	}

	private static ArmorItem genGenericArmor(IMatArmorType mat, ArmorItem.Type slot, Item.Properties prop) {
		var builder = ItemAttributeModifiers.builder();
		mat.defaultAttributes(builder, slot);
		prop.attributes(builder.build());
		prop.durability(slot.getDurability(mat.armorDurability()));
		return new GenericArmorItem(mat.getArmorMaterial(), slot, prop, mat.getExtraArmorConfig());
	}

	public static TagKey<Block> getBlockTag(int level) {
		return switch (level) {
			case 0 -> BlockTags.INCORRECT_FOR_WOODEN_TOOL;
			case 1 -> BlockTags.INCORRECT_FOR_STONE_TOOL;
			case 2 -> BlockTags.INCORRECT_FOR_IRON_TOOL;
			case 3 -> BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
			default -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;

		};
	}

	public ItemEntry<Item>[][] genItem(IMatVanillaType[] mats) {
		int n = mats.length;
		ItemEntry[][] ans = new ItemEntry[n][9];
		for (int i = 0; i < n; i++) {
			IMatVanillaType mat = mats[i];
			String id = mat.getID();
			BiFunction<String, ArmorItem.Type, ItemBuilder> armor_gen = (str, slot) ->
					registrate.item(id + "_" + str, p -> mat.getArmorConfig().sup().get(mat, slot, p))
							.model((ctx, pvd) -> generatedModel(ctx, pvd, id, str))
							.defaultLang();
			ans[i][3] = armor_gen.apply("helmet", ArmorItem.Type.HELMET).tag(MathHelper.merge(mat.getExtraArmorConfig().tags(), ItemTags.HEAD_ARMOR, ItemTags.TRIMMABLE_ARMOR)).register();
			ans[i][2] = armor_gen.apply("chestplate", ArmorItem.Type.CHESTPLATE).tag(MathHelper.merge(mat.getExtraArmorConfig().tags(), ItemTags.CHEST_ARMOR, ItemTags.TRIMMABLE_ARMOR)).register();
			ans[i][1] = armor_gen.apply("leggings", ArmorItem.Type.LEGGINGS).tag(MathHelper.merge(mat.getExtraArmorConfig().tags(), ItemTags.LEG_ARMOR, ItemTags.TRIMMABLE_ARMOR)).register();
			ans[i][0] = armor_gen.apply("boots", ArmorItem.Type.BOOTS).tag(MathHelper.merge(mat.getExtraArmorConfig().tags(), ItemTags.FOOT_ARMOR, ItemTags.TRIMMABLE_ARMOR)).register();
			BiFunction<String, Tools, ItemEntry> tool_gen = (str, tool) ->
					registrate.item(id + "_" + str, p -> mat.getToolConfig().sup().get(mat, tool, p))
							.model((ctx, pvd) -> handHeld(ctx, pvd, id, str)).tag(MathHelper.merge(mat.getExtraToolConfig().tags(), tool.tag))
							.defaultLang().register();
			for (int j = 0; j < Tools.values().length; j++) {
				Tools tool = Tools.values()[j];
				ans[i][4 + j] = tool_gen.apply(tool.name().toLowerCase(Locale.ROOT), tool);
			}
		}
		return ans;
	}

	public ItemEntry<Item>[] genMats(IMatVanillaType[] mats, String suffix, TagKey<Item> tag) {
		int n = mats.length;
		ItemEntry[] ans = new ItemEntry[n];
		for (int i = 0; i < n; i++) {
			String id = mats[i].getID();
			ans[i] = registrate.item(id + "_" + suffix, Item::new)
					.model((ctx, pvd) -> generatedModel(ctx, pvd, id, suffix))
					.tag(tag).defaultLang().register();
		}
		return ans;
	}

	public BlockEntry<Block>[] genBlockMats(IMatVanillaType[] mats) {
		int n = mats.length;
		BlockEntry[] ans = new BlockEntry[n];
		for (int i = 0; i < n; i++) {
			ans[i] = registrate.block(mats[i].getID() + "_block", p -> new Block(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)))
					.defaultLoot().defaultBlockstate()
					.tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL, Tags.Blocks.STORAGE_BLOCKS)
					.item().tag(Tags.Items.STORAGE_BLOCKS).build().defaultLang().register();
		}
		return ans;
	}

	public <T extends Item> void generatedModel(DataGenContext<Item, T> ctx, RegistrateItemModelProvider pvd, String id, String suf) {
		pvd.generated(ctx, ResourceLocation.fromNamespaceAndPath(modid, "item/generated/" + id + "/" + suf));
	}

	public <T extends Item> void handHeld(DataGenContext<Item, T> ctx, RegistrateItemModelProvider pvd, String id, String suf) {
		pvd.handheld(ctx, ResourceLocation.fromNamespaceAndPath(modid, "item/generated/" + id + "/" + suf));
	}

}

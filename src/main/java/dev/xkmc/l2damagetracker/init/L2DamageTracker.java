package dev.xkmc.l2damagetracker.init;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2core.init.reg.datapack.DataMapReg;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.registrate.SimpleEntry;
import dev.xkmc.l2core.init.reg.simple.Reg;
import dev.xkmc.l2core.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2damagetracker.contents.attack.AttackEventHandler;
import dev.xkmc.l2damagetracker.contents.logging.SendLogPacket;
import dev.xkmc.l2damagetracker.contents.curios.FactorAttribute;
import dev.xkmc.l2damagetracker.contents.curios.TotemUseToClient;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeRoot;
import dev.xkmc.l2damagetracker.events.ArsEventCompat;
import dev.xkmc.l2damagetracker.events.L2DTGeneralAttackListener;
import dev.xkmc.l2damagetracker.init.data.*;
import dev.xkmc.l2serial.network.PacketHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

import static dev.xkmc.l2core.init.L2TagGen.ATTR_TAGS;

@Mod(L2DamageTracker.MODID)
@SuppressWarnings("unchecked")
@EventBusSubscriber(modid = L2DamageTracker.MODID, bus = EventBusSubscriber.Bus.MOD)
public class L2DamageTracker {

	public static final String MODID = "l2damagetracker";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Reg REG = new Reg(MODID);
	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	public static final PacketHandlerWithConfig PACKET_HANDLER = new PacketHandlerWithConfig(
			MODID, 3,
			e -> e.create(TotemUseToClient.class, PacketHandler.NetDir.PLAY_TO_CLIENT),
			e -> e.create(SendLogPacket.class, PacketHandler.NetDir.PLAY_TO_CLIENT)
	);

	@Deprecated(forRemoval = true)
	public static final TagKey<Attribute> PERCENTAGE = key("percentage");
	@Deprecated(forRemoval = true)
	public static final TagKey<Attribute> NEGATIVE = key("negative");

	public static final SimpleEntry<Attribute> CRIT_RATE = reg(REGISTRATE, "crit_rate", e -> new FactorAttribute(e, 0, 0, 1), "Weapon Crit Rate");
	public static final SimpleEntry<Attribute> CRIT_DMG = reg(REGISTRATE, "crit_damage", e -> new FactorAttribute(e, 0.5, 0, 1000), "Weapon Crit Damage");
	public static final SimpleEntry<Attribute> BOW_STRENGTH = regPerc(REGISTRATE, "bow_strength", "Projectile Strength");
	public static final SimpleEntry<Attribute> EXPLOSION_FACTOR = regPerc(REGISTRATE, "explosion_damage", "Explosion Damage");
	public static final SimpleEntry<Attribute> FIRE_FACTOR = regPerc(REGISTRATE, "fire_damage", "Fire Damage");
	public static final SimpleEntry<Attribute> FREEZING_FACTOR = regPerc(REGISTRATE, "freezing_damage", "Freezing Damage");
	public static final SimpleEntry<Attribute> LIGHTNING_FACTOR = regPerc(REGISTRATE, "lightning_damage", "Lightning Damage");
	public static final SimpleEntry<Attribute> MAGIC_FACTOR = regPerc(REGISTRATE, "magic_damage", "Magic Damage");
	public static final SimpleEntry<Attribute> REGEN = regPerc(REGISTRATE, "regen", "Regeneration Rate");
	public static final SimpleEntry<Attribute> ABSORB = reg(REGISTRATE, "damage_absorption", e -> new RangedAttribute(e, 0, 0, 10000), "Damage Absorption");
	public static final SimpleEntry<Attribute> REDUCTION = reg(REGISTRATE, "damage_reduction", e -> new FactorAttribute(e, 1, -10000, 10000).setSentiment(Attribute.Sentiment.NEGATIVE), "Damage after Reduction");

	public static final DataMapReg<ArmorMaterial, ArmorImmunity> ARMOR = REG.dataMap("armor_immunity", Registries.ARMOR_MATERIAL, ArmorImmunity.class);

	public L2DamageTracker() {
		L2DamageTrackerConfig.init();
		L2DamageTypes.register();
		AttackEventHandler.register(1000, new L2DTGeneralAttackListener());
		new L2DamageTypes(REGISTRATE).generate();
		REGISTRATE.addDataGenerator(ProviderType.LANG, L2DTLangData::genLang);
		REGISTRATE.addDataGenerator(ProviderType.DATA_MAP, DTAttributeConfigGen::onDataMapGen);
		if (ModList.get().isLoaded(ArsNouveau.MODID)) NeoForge.EVENT_BUS.register(ArsEventCompat.class);
		NeoForgeMod.enableMergedAttributeTooltips();
	}

	@SubscribeEvent
	public static void modifyAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, CRIT_RATE.holder());
		event.add(EntityType.PLAYER, CRIT_DMG.holder());
		for (var e : event.getTypes()) {
			event.add(e, BOW_STRENGTH.holder());
			event.add(e, EXPLOSION_FACTOR.holder());
			event.add(e, FIRE_FACTOR.holder());
			event.add(e, FREEZING_FACTOR.holder());
			event.add(e, LIGHTNING_FACTOR.holder());
			event.add(e, MAGIC_FACTOR.holder());
			event.add(e, REDUCTION.holder());
			event.add(e, ABSORB.holder());
		}
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event) {
		DamageTypeRoot.generateAll();
	}

	@Deprecated(forRemoval = true)
	public static SimpleEntry<Attribute> regWrapped(L2Registrate reg, String id, double def, double min, double max, String name, TagKey<Attribute>... keys) {
		reg.addRawLang("attribute." + reg.getModid() + "." + id, name);
		return new SimpleEntry<>(reg.generic(reg, id, Registries.ATTRIBUTE,
				() -> new RangedAttribute("attribute." + reg.getModid() + "." + id, def, min, max)
						.setSyncable(true)).tag(ATTR_TAGS, keys).register());
	}

	public static SimpleEntry<Attribute> reg(L2Registrate reg, String id, Function<String, ? extends Attribute> func, String name) {
		reg.addRawLang("attribute." + reg.getModid() + "." + id, name);
		return new SimpleEntry<>(reg.generic(reg, id, Registries.ATTRIBUTE,
				() -> func.apply("attribute." + reg.getModid() + "." + id).setSyncable(true)).register());
	}

	public static SimpleEntry<Attribute> regPerc(L2Registrate reg, String id, String name) {
		return reg(reg, id, e -> new FactorAttribute(e, 1, 0, 1000), name);
	}

	public static ResourceLocation loc(String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}

	public static TagKey<Attribute> key(String id) {
		return TagKey.create(Registries.ATTRIBUTE, loc(id));
	}

}

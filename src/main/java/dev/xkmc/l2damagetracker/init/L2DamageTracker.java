package dev.xkmc.l2damagetracker.init;

import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2core.init.reg.datapack.DataMapReg;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.registrate.SimpleEntry;
import dev.xkmc.l2core.init.reg.simple.Reg;
import dev.xkmc.l2core.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2damagetracker.contents.attack.AttackEventHandler;
import dev.xkmc.l2damagetracker.contents.curios.TotemUseToClient;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeRoot;
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
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
			MODID, 2,
			e -> e.create(TotemUseToClient.class, PacketHandler.NetDir.PLAY_TO_CLIENT));

	public static final TagKey<Attribute> PERCENTAGE = key("percentage");
	public static final TagKey<Attribute> NEGATIVE = key("negative");

	public static final SimpleEntry<Attribute> CRIT_RATE = regWrapped(REGISTRATE, "crit_rate", 0, 0, 1, "Weapon Crit Rate", PERCENTAGE);
	public static final SimpleEntry<Attribute> CRIT_DMG = regWrapped(REGISTRATE, "crit_damage", 0.5, 0, 1000, "Weapon Crit Damage", PERCENTAGE);
	public static final SimpleEntry<Attribute> BOW_STRENGTH = regWrapped(REGISTRATE, "bow_strength", 1, 0, 1000, "Projectile Strength", PERCENTAGE);
	public static final SimpleEntry<Attribute> EXPLOSION_FACTOR = regWrapped(REGISTRATE, "explosion_damage", 1, 0, 1000, "Explosion Damage", PERCENTAGE);
	public static final SimpleEntry<Attribute> FIRE_FACTOR = regWrapped(REGISTRATE, "fire_damage", 1, 0, 1000, "Fire Damage", PERCENTAGE);
	public static final SimpleEntry<Attribute> FREEZING_FACTOR = regWrapped(REGISTRATE, "freezing_damage", 1, 0, 1000, "Freezing Damage", PERCENTAGE);
	public static final SimpleEntry<Attribute> LIGHTNING_FACTOR = regWrapped(REGISTRATE, "lightning_damage", 1, 0, 1000, "Lightning Damage", PERCENTAGE);
	public static final SimpleEntry<Attribute> MAGIC_FACTOR = regWrapped(REGISTRATE, "magic_damage", 1, 0, 1000, "Magic Damage", PERCENTAGE);
	public static final SimpleEntry<Attribute> REGEN = regWrapped(REGISTRATE, "regen", 1, 0, 1000, "Regeneration Rate", PERCENTAGE);
	public static final SimpleEntry<Attribute> ABSORB = regWrapped(REGISTRATE, "damage_absorption", 0, 0, 10000, "Damage Absorption");
	public static final SimpleEntry<Attribute> REDUCTION = regWrapped(REGISTRATE, "damage_reduction", 1, -10000, 10000, "Damage after Reduction", PERCENTAGE, NEGATIVE);

	public static final DataMapReg<ArmorMaterial, ArmorImmunity> ARMOR = REG.dataMap("armor_immunity", Registries.ARMOR_MATERIAL, ArmorImmunity.class);

	public L2DamageTracker() {
		L2DamageTrackerConfig.init();
		L2DamageTypes.register();
		AttackEventHandler.register(1000, new L2DTGeneralAttackListener());
		new L2DamageTypes(REGISTRATE).generate();
		REGISTRATE.addDataGenerator(ProviderType.LANG, L2DTLangData::genLang);
		REGISTRATE.addDataGenerator(ProviderType.DATA_MAP, DTAttributeConfigGen::onDataMapGen);
		//TODO if (ModList.get().isLoaded(ArsNouveau.MODID)) MinecraftForge.EVENT_BUS.register(ArsEventCompat.class);
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

	@SuppressWarnings({"unchecked"})
	public static SimpleEntry<Attribute> regWrapped(L2Registrate reg, String id, double def, double min, double max, String name, TagKey<Attribute>... keys) {
		reg.addRawLang("attribute." + reg.getModid() + "." + id, name);
		return new SimpleEntry<>(reg.generic(reg, id, Registries.ATTRIBUTE,
				() -> new RangedAttribute("attribute." + reg.getModid() + "." + id, def, min, max)
						.setSyncable(true)).tag(ATTR_TAGS, keys).register());
	}

	public static ResourceLocation loc(String id) {
		return ResourceLocation.fromNamespaceAndPath(MODID, id);
	}

	public static TagKey<Attribute> key(String id) {
		return TagKey.create(Registries.ATTRIBUTE, loc(id));
	}

}

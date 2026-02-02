package dev.xkmc.l2damagetracker.init.data;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeRoot;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeWrapper;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.datagen.DamageTypeTagGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class L2DamageTypes extends DamageTypeAndTagsGen {

	public static final TagKey<DamageType> MATERIAL_MUX = TagKey.create(Registries.DAMAGE_TYPE,
			L2DamageTracker.loc("material_mux"));

	public static final TagKey<DamageType> NO_SCALE = TagKey.create(Registries.DAMAGE_TYPE,
			L2DamageTracker.loc("ignore_scaling"));

	public static final TagKey<DamageType> DIRECT = TagKey.create(Registries.DAMAGE_TYPE,
			L2DamageTracker.loc("direct"));

	public static final DamageTypeRoot PLAYER_ATTACK = new DamageTypeRoot(L2DamageTracker.MODID, DamageTypes.PLAYER_ATTACK,
			List.of(DIRECT), (type) -> new DamageType("player", 0.1F));

	public static final DamageTypeRoot MOB_ATTACK = new DamageTypeRoot(L2DamageTracker.MODID, DamageTypes.MOB_ATTACK,
			List.of(DIRECT), (type) -> new DamageType("mob", 0.1F));

	public static final DamageTypeTagGroup BYPASS_MAGIC = DamageTypeTagGroup.of(
			DamageTypeTags.BYPASSES_ENCHANTMENTS, DamageTypeTags.BYPASSES_RESISTANCE,
			DamageTypeTags.BYPASSES_EFFECTS
	);

	public static final DamageTypeTagGroup BYPASS_INVUL = DamageTypeTagGroup.of(
			DamageTypeTags.BYPASSES_ARMOR,
			DamageTypeTags.BYPASSES_ENCHANTMENTS, DamageTypeTags.BYPASSES_RESISTANCE,
			DamageTypeTags.BYPASSES_INVULNERABILITY, DamageTypeTags.BYPASSES_EFFECTS
	);

	protected static final List<DamageTypeWrapper> LIST = new ArrayList<>();

	public static void register() {
		PLAYER_ATTACK.add(DefaultDamageState.BYPASS_ARMOR);
		PLAYER_ATTACK.add(DefaultDamageState.BYPASS_MAGIC);
		PLAYER_ATTACK.add(DefaultDamageState.BYPASS_COOLDOWN);

		MOB_ATTACK.add(DefaultDamageState.BYPASS_ARMOR);
		MOB_ATTACK.add(DefaultDamageState.BYPASS_MAGIC);
		MOB_ATTACK.add(DefaultDamageState.BYPASS_COOLDOWN);

		DamageTypeRoot.configureGeneration(Set.of(L2DamageTracker.MODID), L2DamageTracker.MODID, LIST);
	}

	public L2DamageTypes(L2Registrate reg) {
		super(reg);
	}

	@Override
	protected void addDamageTypes(BootstrapContext<DamageType> ctx) {
		DamageTypeRoot.generateAll();
		for (DamageTypeWrapper wrapper : L2DamageTypes.LIST) {
			ctx.register(wrapper.type(), wrapper.getObject());
		}
	}

	@Override
	protected void addDamageTypeTags(RegistrateTagsProvider.Impl<DamageType> pvd) {
		DamageTypeRoot.generateAll();
		for (DamageTypeWrapper wrapper : LIST) {
			wrapper.gen(pvd::addTag);
		}
		pvd.addTag(MATERIAL_MUX).add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK);
		pvd.addTag(DIRECT).add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK);
		pvd.addTag(NO_SCALE).add(DamageTypes.THORNS, DamageTypes.STARVE, DamageTypes.DROWN, DamageTypes.DRY_OUT, DamageTypes.IN_WALL);
		pvd.addTag(Tags.DamageTypes.IS_MAGIC).add(DamageTypes.SONIC_BOOM);
		if (ModList.get().isLoaded(IronsSpellbooks.MODID)) {
			pvd.addTag(Tags.DamageTypes.IS_MAGIC).addOptionalTags(
							DamageTypeTagGenerator.FIRE_MAGIC,
							DamageTypeTagGenerator.ICE_MAGIC,
							DamageTypeTagGenerator.LIGHTNING_MAGIC,
							DamageTypeTagGenerator.HOLY_MAGIC,
							DamageTypeTagGenerator.ENDER_MAGIC,
							DamageTypeTagGenerator.BLOOD_MAGIC,
							DamageTypeTagGenerator.EVOCATION_MAGIC,
							DamageTypeTagGenerator.ELDRITCH_MAGIC,
							DamageTypeTagGenerator.NATURE_MAGIC
					).addOptional(ISSDamageTypes.CAULDRON.location())
					.addOptional(ISSDamageTypes.DRAGON_BREATH_POOL.location())
					.addOptional(ISSDamageTypes.FIRE_FIELD.location())
					.addOptional(ISSDamageTypes.POISON_CLOUD.location());
		}
		if (ModList.get().isLoaded(ArsNouveau.MODID)) {
			pvd.addTag(Tags.DamageTypes.IS_MAGIC).addOptional(DamageTypesRegistry.CRUSH.location());
			pvd.addTag(Tags.DamageTypes.IS_MAGIC).addOptional(DamageTypesRegistry.WINDSHEAR.location());
			pvd.addTag(Tags.DamageTypes.IS_MAGIC).addOptional(DamageTypesRegistry.FLARE.location());
			pvd.addTag(Tags.DamageTypes.IS_MAGIC).addOptional(DamageTypesRegistry.COLD_SNAP.location());
		}
		pvd.addTag(Tags.DamageTypes.IS_MAGIC)
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "aerial_collapse"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "aqua_magic"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "asteroid_impact_crater"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "extended_water_bolt"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "lingering_strain"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "nullflare_blast"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "nullflare_fire"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "nullflare_ice"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "primordial_crest"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "reversal"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "tectonic_crest"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "void_uppercut"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("traveloptics", "voidstrike_reaper_bonus_damage"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("gtbcs_geomancy_plus", "geo_magic"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("ess_requiem", "blade_magic"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("ess_requiem", "divine_magic"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("aero_additions", "wind_magic"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("fantasy_ending", "ds_power"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("fantasy_ending", "fe_power"))
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("familiarslib", "sound_magic"))
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "dune_magic"))
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "is_soul"))
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "spirit_magic"))
				.addOptionalTag(ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "symmetry_magic"));
	}
}

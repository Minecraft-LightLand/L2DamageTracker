package dev.xkmc.l2damagetracker.init.data;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.Tags;

public class L2DamageTypes extends DamageTypeAndTagsGen {

	public static final TagKey<DamageType> NO_SCALE = TagKey.create(Registries.DAMAGE_TYPE,
			L2DamageTracker.loc("ignore_scaling"));

	public static final TagKey<DamageType> DIRECT = TagKey.create(Registries.DAMAGE_TYPE,
			L2DamageTracker.loc("direct"));

	public static final DamageTypeTagGroup BYPASS_MAGIC = DamageTypeTagGroup.of(
			DamageTypeTags.BYPASSES_ENCHANTMENTS, DamageTypeTags.BYPASSES_RESISTANCE,
			DamageTypeTags.BYPASSES_EFFECTS
	);

	public static final DamageTypeTagGroup BYPASS_INVUL = DamageTypeTagGroup.of(
			DamageTypeTags.BYPASSES_ARMOR,
			DamageTypeTags.BYPASSES_ENCHANTMENTS, DamageTypeTags.BYPASSES_RESISTANCE,
			DamageTypeTags.BYPASSES_INVULNERABILITY, DamageTypeTags.BYPASSES_EFFECTS
	);

	public static void register() {
	}

	public L2DamageTypes(L2Registrate reg) {
		super(reg);
	}

	@Override
	protected void addDamageTypes(BootstrapContext<DamageType> ctx) {
	}

	@Override
	protected void addDamageTypeTags(RegistrateTagsProvider.Impl<DamageType> pvd) {
		pvd.tag(DIRECT).add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK);
		pvd.tag(NO_SCALE).add(DamageTypes.THORNS, DamageTypes.STARVE, DamageTypes.DROWN, DamageTypes.DRY_OUT, DamageTypes.IN_WALL);
		pvd.tag(Tags.DamageTypes.IS_MAGIC).add(DamageTypes.SONIC_BOOM);

		/*

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
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "aerial_collapse"))
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "aqua_magic"))
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "asteroid_impact_crater"))
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "extended_water_bolt"))
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "lingering_strain"))
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "nullflare_blast"))
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "nullflare_fire"))
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "nullflare_ice"))
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "primordial_crest"))
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "reversal"))
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "tectonic_crest"))
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "void_uppercut"))
				.addOptional(Identifier.fromNamespaceAndPath("traveloptics", "voidstrike_reaper_bonus_damage"))
				.addOptional(Identifier.fromNamespaceAndPath("gtbcs_geomancy_plus", "geo_magic"))
				.addOptional(Identifier.fromNamespaceAndPath("ess_requiem", "blade_magic"))
				.addOptional(Identifier.fromNamespaceAndPath("ess_requiem", "divine_magic"))
				.addOptional(Identifier.fromNamespaceAndPath("aero_additions", "wind_magic"))
				.addOptional(Identifier.fromNamespaceAndPath("fantasy_ending", "ds_power"))
				.addOptional(Identifier.fromNamespaceAndPath("fantasy_ending", "fe_power"))
				.addOptionalTag(Identifier.fromNamespaceAndPath("alshanex_familiars", "sound_magic"))
				.addOptionalTag(Identifier.fromNamespaceAndPath("iss_magicfromtheeast", "dune_magic"))
				.addOptionalTag(Identifier.fromNamespaceAndPath("iss_magicfromtheeast", "is_soul"))
				.addOptionalTag(Identifier.fromNamespaceAndPath("iss_magicfromtheeast", "spirit_magic"))
				.addOptionalTag(Identifier.fromNamespaceAndPath("iss_magicfromtheeast", "symmetry_magic"));

		 */
	}
}

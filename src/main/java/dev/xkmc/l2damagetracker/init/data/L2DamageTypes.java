package dev.xkmc.l2damagetracker.init.data;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeRoot;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeWrapper;
import dev.xkmc.l2damagetracker.contents.damage.DamageWrapperTagProvider;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.datagen.DamageTypeTagGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class L2DamageTypes extends DamageTypeAndTagsGen {

	public static final TagKey<DamageType> MATERIAL_MUX = TagKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(L2DamageTracker.MODID, "material_mux"));

	public static final TagKey<DamageType> MAGIC = TagKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation("forge", "is_magic"));

	public static final TagKey<DamageType> NO_SCALE = TagKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation("forge", "ignore_scaling"));

	public static final TagKey<DamageType> DIRECT = TagKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation("forge", "direct"));

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

		MOB_ATTACK.add(DefaultDamageState.BYPASS_ARMOR);
		MOB_ATTACK.add(DefaultDamageState.BYPASS_MAGIC);

		DamageTypeRoot.configureGeneration(Set.of(L2DamageTracker.MODID), L2DamageTracker.MODID, LIST);
	}

	public L2DamageTypes(PackOutput output,
						 CompletableFuture<HolderLookup.Provider> pvd,
						 ExistingFileHelper files) {
		super(output, pvd, files, L2DamageTracker.MODID);
	}

	@Override
	protected void addDamageTypes(BootstapContext<DamageType> ctx) {
		DamageTypeRoot.generateAll();
		for (DamageTypeWrapper wrapper : L2DamageTypes.LIST) {
			ctx.register(wrapper.type(), wrapper.getObject());
		}
	}

	@Override
	protected void addDamageTypeTags(DamageWrapperTagProvider pvd, HolderLookup.Provider lookup) {
		DamageTypeRoot.generateAll();
		for (DamageTypeWrapper wrapper : LIST) {
			wrapper.gen(pvd, lookup);
		}
		pvd.tag(MATERIAL_MUX).add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK);
		pvd.tag(DIRECT).add(DamageTypes.PLAYER_ATTACK, DamageTypes.MOB_ATTACK);
		pvd.tag(MAGIC).add(DamageTypes.MAGIC, DamageTypes.INDIRECT_MAGIC, DamageTypes.THORNS, DamageTypes.SONIC_BOOM,
				DamageTypes.WITHER, DamageTypes.DRAGON_BREATH, DamageTypes.WITHER_SKULL);
		pvd.tag(NO_SCALE).add(DamageTypes.THORNS, DamageTypes.STARVE, DamageTypes.DROWN, DamageTypes.DRY_OUT, DamageTypes.IN_WALL);
		if (ModList.get().isLoaded(IronsSpellbooks.MODID)) {
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.FIRE_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.ICE_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.LIGHTNING_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.HOLY_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.ENDER_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.BLOOD_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.EVOCATION_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.ELDRITCH_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.NATURE_MAGIC.location());
			pvd.tag(MAGIC).addOptional(ISSDamageTypes.CAULDRON.location());
			pvd.tag(MAGIC).addOptional(ISSDamageTypes.HEARTSTOP.location());
			pvd.tag(MAGIC).addOptional(ISSDamageTypes.DRAGON_BREATH_POOL.location());
			pvd.tag(MAGIC).addOptional(ISSDamageTypes.FIRE_FIELD.location());
			pvd.tag(MAGIC).addOptional(ISSDamageTypes.POISON_CLOUD.location());
		}
		if (ModList.get().isLoaded(ArsNouveau.MODID)) {
			pvd.tag(MAGIC).addOptional(DamageTypesRegistry.CRUSH.location());
			pvd.tag(MAGIC).addOptional(DamageTypesRegistry.WINDSHEAR.location());
			pvd.tag(MAGIC).addOptional(DamageTypesRegistry.FLARE.location());
			pvd.tag(MAGIC).addOptional(DamageTypesRegistry.COLD_SNAP.location());
		}
		pvd.tag(MAGIC)
			.addOptional(new ResourceLocation("traveloptics", "aerial_collapse"))
			.addOptional(new ResourceLocation("traveloptics", "aqua_magic"))
			.addOptional(new ResourceLocation("traveloptics", "asteroid_impact_crater"))
			.addOptional(new ResourceLocation("traveloptics", "extended_water_bolt"))
			.addOptional(new ResourceLocation("traveloptics", "lingering_strain"))
			.addOptional(new ResourceLocation("traveloptics", "nullflare_blast"))
			.addOptional(new ResourceLocation("traveloptics", "nullflare_fire"))
			.addOptional(new ResourceLocation("traveloptics", "nullflare_ice"))
			.addOptional(new ResourceLocation("traveloptics", "primordial_crest"))
			.addOptional(new ResourceLocation("traveloptics", "reversal"))
			.addOptional(new ResourceLocation("traveloptics", "tectonic_crest"))
			.addOptional(new ResourceLocation("traveloptics", "void_uppercut"))
			.addOptional(new ResourceLocation("traveloptics", "voidstrike_reaper_bonus_damage"))
			.addOptional(new ResourceLocation("gtbcs_geomancy_plus", "geo_magic"))
			.addOptionalTag(new ResourceLocation("familiarslib", "sound_magic"))
			.addOptional(new ResourceLocation("fantasy_ending", "ds_power"))
			.addOptional(new ResourceLocation("fantasy_ending", "fe_power"));
	}

}

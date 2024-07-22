package dev.xkmc.l2damagetracker.init.data;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeRoot;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeWrapper;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

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

		MOB_ATTACK.add(DefaultDamageState.BYPASS_ARMOR);
		MOB_ATTACK.add(DefaultDamageState.BYPASS_MAGIC);

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

		/* TODO
		if (ModList.get().isLoaded(IronsSpellbooks.MODID)) {
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.FIRE_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.ICE_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.LIGHTNING_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.HOLY_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.ENDER_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.BLOOD_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.EVOCATION_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.VOID_MAGIC.location());
			pvd.tag(MAGIC).addOptionalTag(DamageTypeTagGenerator.POISON_MAGIC.location());
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
		}*/
	}

}

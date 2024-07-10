package dev.xkmc.l2damagetracker.init.data;

import dev.xkmc.l2damagetracker.contents.damage.DamageWrapperTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class DamageTypeAndTagsGen {

	public record DamageTypeTagGroup(TagKey<DamageType>[] tags) {

		@SafeVarargs
		public static DamageTypeTagGroup of(TagKey<DamageType>... tags) {
			return new DamageTypeTagGroup(tags);
		}

	}

	public class DamageTypeHolder {

		private final ResourceKey<DamageType> key;
		private final DamageType value;
		private final Set<TagKey<DamageType>> tags = new HashSet<>();

		public DamageTypeHolder(ResourceKey<DamageType> key, DamageType value) {
			this.key = key;
			this.value = value;
			holders.add(this);
		}

		@SafeVarargs
		public final DamageTypeHolder add(TagKey<DamageType>... tags) {
			this.tags.addAll(Arrays.asList(tags));
			return this;
		}

		public final DamageTypeHolder add(DamageTypeTagGroup group) {
			add(group.tags());
			return this;
		}

	}

	private final PackOutput output;
	private final CompletableFuture<HolderLookup.Provider> pvd;
	private final ExistingFileHelper helper;
	private final String modid;
	private final List<DamageTypeHolder> holders = new ArrayList<>();

	public DamageTypeAndTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> pvd, ExistingFileHelper helper, String modid) {
		this.output = output;
		this.pvd = pvd;
		this.helper = helper;
		this.modid = modid;
	}

	public void generate(boolean gen, DataGenerator generator) {
		var entries = new DamageTypeGen();
		generator.addProvider(gen, entries);
		generator.addProvider(gen, new DamageTypeTagsGen(entries.getRegistryProvider()));
	}

	protected void addDamageTypes(BootstrapContext<DamageType> ctx) {
		for (var e : holders) {
			ctx.register(e.key, e.value);
		}
	}

	protected void addDamageTypeTags(DamageWrapperTagProvider pvd, HolderLookup.Provider lookup) {
		for (var e : holders) {
			for (var t : e.tags) {
				pvd.tag(t).add(e.key);
			}
		}
	}

	private class DamageTypeGen extends DatapackBuiltinEntriesProvider {

		public DamageTypeGen() {
			super(output, pvd, new RegistrySetBuilder().add(Registries.DAMAGE_TYPE,
					DamageTypeAndTagsGen.this::addDamageTypes), Set.of(modid));
		}

	}

	private class DamageTypeTagsGen extends TagsProvider<DamageType> {

		public DamageTypeTagsGen(CompletableFuture<HolderLookup.Provider> pvd) {
			super(output, Registries.DAMAGE_TYPE, pvd, modid, helper);
		}

		@Override
		protected void addTags(HolderLookup.Provider lookup) {
			addDamageTypeTags(this::tag, lookup);
		}

	}


}

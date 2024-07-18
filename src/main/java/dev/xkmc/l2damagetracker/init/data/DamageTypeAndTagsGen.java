package dev.xkmc.l2damagetracker.init.data;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.l2core.init.L2TagGen;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.*;

public abstract class DamageTypeAndTagsGen {

	public static final ProviderType<RegistrateTagsProvider.Impl<DamageType>> DAMAGE_TYPE_TAG = L2TagGen.getProvider(Registries.DAMAGE_TYPE);

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

	private final L2Registrate reg;
	private final List<DamageTypeHolder> holders = new ArrayList<>();

	public DamageTypeAndTagsGen(L2Registrate reg) {
		this.reg = reg;
	}

	public void generate() {
		reg.getDataGenInitializer().add(Registries.DAMAGE_TYPE, this::addDamageTypes);
		reg.addDataGenerator(DAMAGE_TYPE_TAG, this::addDamageTypeTags);
		reg.getDataGenInitializer().addDependency(DAMAGE_TYPE_TAG, ProviderType.DYNAMIC);
	}

	protected void addDamageTypes(BootstrapContext<DamageType> ctx) {
		for (var e : holders) {
			ctx.register(e.key, e.value);
		}
	}

	protected void addDamageTypeTags(RegistrateTagsProvider.Impl<DamageType> pvd) {
		for (var e : holders) {
			for (var t : e.tags) {
				pvd.addTag(t).add(e.key);
			}
		}
	}

}

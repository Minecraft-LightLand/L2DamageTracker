package dev.xkmc.l2damagetracker.mixin;

import dev.xkmc.l2damagetracker.contents.attack.DamageSourceExtra;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;

@Mixin(DamageSource.class)
public class DamageSourceMixin implements DamageSourceExtra {

	private final HashSet<TagKey<DamageType>> additionalTags = new HashSet<>();
	private final HashSet<TagKey<DamageType>> removedTags = new HashSet<>();

	@Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
	public void l2damagetracker$is(TagKey<DamageType> tag, CallbackInfoReturnable<Boolean> cir) {
		if (removedTags.contains(tag)) {
			cir.setReturnValue(false);
			return;
		}
		if (additionalTags.contains(tag)) {
			cir.setReturnValue(true);
			return;
		}
	}

	@Override
	public void l2$enable(TagKey<DamageType> tag) {
		additionalTags.add(tag);
	}

	@Override
	public void l2$disable(TagKey<DamageType> tag) {
		removedTags.add(tag);
	}

}

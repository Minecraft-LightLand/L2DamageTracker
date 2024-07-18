package dev.xkmc.l2damagetracker.contents.attack;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jetbrains.annotations.Nullable;

public interface DamageData {

	DamageContainer getContainer();

	DamageSource getSource();

	LivingEntity getTarget();

	@Nullable
	LivingEntity getAttacker();

	float getStrength();

	float getDamageOriginal();

	boolean bypassMagic();

	@Nullable
	PlayerAttackCache getPlayerData();

	interface PostSetup extends DamageData {

		ItemStack getWeapon();

	}

	interface Attack extends PostSetup {

		void setNonCancellable();

	}

	interface Offence extends PostSetup {

		void addHurtModifier(DamageModifier mod);

	}

	interface OffenceMax extends PostSetup {

		float getDamageIncoming();

	}

	interface Defence extends OffenceMax {

		void addDealtModifier(DamageModifier mod);

	}

	interface DefenceMax extends OffenceMax {

		float getDamageFinal();

	}

	interface All extends Attack, Offence, Defence, DefenceMax {

	}

}

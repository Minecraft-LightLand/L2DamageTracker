package dev.xkmc.l2damagetracker.contents.attack;

import net.neoforged.neoforge.common.damagesource.DamageContainer;

public interface DamageContainerExtra {

	static DamageDataExtra get(DamageContainer cont) {
		return ((DamageContainerExtra) cont).l2damagetracker$getExtra();
	}

	DamageDataExtra l2damagetracker$getExtra();

}

package dev.xkmc.l2damagetracker.contents.attack;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

import javax.annotation.Nullable;

public class PlayerAttackCache {

	private Stage stage = Stage.PREINIT;
	private AttackEntityEvent player;
	private CriticalHitEvent crit;

	private LivingEntity attacker;

	private ItemStack weapon = ItemStack.EMPTY;

	private float strength = -1;

	void pushPlayer(AttackEntityEvent event) {
		stage = Stage.PLAYER_ATTACK;
		player = event;
		strength = event.getEntity().getAttackStrengthScale(1);
		AttackEventHandler.getListeners().forEach(e -> e.onPlayerAttack(this));
	}

	void pushCrit(CriticalHitEvent event) {
		stage = Stage.CRITICAL_HIT;
		crit = event;
		boolean handled = false;
		for (AttackListener e : AttackEventHandler.getListeners()) {
			handled |= e.onCriticalHit(this, event);
		}
		if (handled) {
			event.setCriticalHit(true);
		}
	}

	@Nullable
	public AttackEntityEvent getPlayerAttackEntityEvent() {
		return player;
	}

	@Nullable
	public CriticalHitEvent getCriticalHitEvent() {
		return crit;
	}

	public float getStrength() {
		return strength;
	}

	public void setupAttackerProfile(LivingEntity entity, ItemStack stack) {
		attacker = entity;
		weapon = stack;
	}

	public LivingEntity getAttacker() {
		return attacker;
	}

	public ItemStack getWeapon() {
		return weapon;
	}


}

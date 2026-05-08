package dev.xkmc.l2damagetracker.compat;

import dev.xkmc.l2damagetracker.contents.attack.*;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.apache.commons.lang3.function.Consumers;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CustomAttackListener implements AttackListener {

	private Consumer<PlayerAttackCache> onPlayerAttack = Consumers.nop();
	private BiPredicate<PlayerAttackCache, CriticalHitEvent> onCriticalHit = (a, b) -> false;
	private Predicate<DamageData.Attack> onAttack = e -> false;
	private Consumer<DamageData.Offence> onHurt = Consumers.nop();
	private Consumer<DamageData.OffenceMax> onHurtMaximized = Consumers.nop();
	private Consumer<DamageData.Defence> onDamage = Consumers.nop();
	private Consumer<DamageData.DefenceMax> onDamageFinalized = Consumers.nop();
	private Consumer<OnDamageSourceModifyEvent> onCreateSource = Consumers.nop();

	@Override
	public void onPlayerAttack(PlayerAttackCache cache) {
		onPlayerAttack.accept(cache);
	}

	@Override
	public boolean onCriticalHit(PlayerAttackCache cache, CriticalHitEvent event) {
		return onCriticalHit.test(cache, event);
	}

	@Override
	public boolean onAttack(DamageData.Attack cache) {
		return onAttack.test(cache);
	}

	@Override
	public void onHurt(DamageData.Offence data) {
		onHurt.accept(data);
	}

	@Override
	public void onHurtMaximized(DamageData.OffenceMax data) {
		onHurtMaximized.accept(data);
	}

	@Override
	public void onDamage(DamageData.Defence data) {
		onDamage.accept(data);
	}

	@Override
	public void onDamageFinalized(DamageData.DefenceMax data) {
		onDamageFinalized.accept(data);
	}

	@Override
	public void onCreateSource(OnDamageSourceModifyEvent event) {
		onCreateSource.accept(event);
	}


	public CustomAttackListener subscribePlayerAttack(Consumer<PlayerAttackCache> onPlayerAttack) {
		this.onPlayerAttack = onPlayerAttack;
		return this;
	}

	public CustomAttackListener subscribeCriticalHit(BiPredicate<PlayerAttackCache, CriticalHitEvent> onCriticalHit) {
		this.onCriticalHit = onCriticalHit;
		return this;
	}

	public CustomAttackListener subscribeAttack(Predicate<DamageData.Attack> onAttack) {
		this.onAttack = onAttack;
		return this;
	}

	public CustomAttackListener subscribeHurt(Consumer<DamageData.Offence> onHurt) {
		this.onHurt = onHurt;
		return this;
	}

	public CustomAttackListener subscribeHurtMaximized(Consumer<DamageData.OffenceMax> onHurtMaximized) {
		this.onHurtMaximized = onHurtMaximized;
		return this;
	}

	public CustomAttackListener subscribeDamage(Consumer<DamageData.Defence> onDamage) {
		this.onDamage = onDamage;
		return this;
	}

	public CustomAttackListener subscribeDamageFinalized(Consumer<DamageData.DefenceMax> onDamageFinalized) {
		this.onDamageFinalized = onDamageFinalized;
		return this;
	}

	public CustomAttackListener subscribeCreateSource(Consumer<OnDamageSourceModifyEvent> onCreateSource) {
		this.onCreateSource = onCreateSource;
		return this;
	}

	private String name;

	public void register(String name, int priority) {
		this.name = name;
		AttackEventHandler.getListeners().removeIf(e -> e instanceof CustomAttackListener c && c.name.equals(name));
		AttackEventHandler.register(priority, this);
	}

}

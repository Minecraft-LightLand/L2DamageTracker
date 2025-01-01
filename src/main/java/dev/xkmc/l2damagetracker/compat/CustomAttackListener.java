package dev.xkmc.l2damagetracker.compat;

import dev.xkmc.l2damagetracker.contents.attack.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class CustomAttackListener implements AttackListener {

	private Consumer<PlayerAttackCache> onPlayerAttack = e -> {
	};
	private BiPredicate<PlayerAttackCache, CriticalHitEvent> onCriticalHit = (c, e) -> false;
	private Consumer<AttackCache> onAttack = e -> {
	};
	private Consumer<AttackCache> postAttack = e -> {
	};
	private Consumer<AttackCache> onHurt = e -> {
	};
	private Consumer<AttackCache> onHurtMaximized = e -> {
	};
	private Consumer<AttackCache> postHurt = e -> {
	};
	private Consumer<AttackCache> onDamage = e -> {
	};
	private Consumer<AttackCache> onDamageFinalized = e -> {
	};
	private Consumer<CreateSourceEvent> onCreateSource = e -> {
	};

	@Override
	public void onPlayerAttack(PlayerAttackCache cache) {
		onPlayerAttack.accept(cache);
	}

	@Override
	public boolean onCriticalHit(PlayerAttackCache cache, CriticalHitEvent event) {
		return onCriticalHit.test(cache, event);
	}

	@Override
	public void setupProfile(AttackCache attackCache, BiConsumer<LivingEntity, ItemStack> setupProfile) {

	}

	@Override
	public void onAttack(AttackCache cache, ItemStack weapon) {
		onAttack.accept(cache);
	}

	@Override
	public void postAttack(AttackCache cache, LivingAttackEvent event, ItemStack weapon) {
		postAttack.accept(cache);
	}

	@Override
	public void onHurt(AttackCache cache, ItemStack weapon) {
		onHurt.accept(cache);
	}

	@Override
	public void onHurtMaximized(AttackCache cache, ItemStack weapon) {
		onHurtMaximized.accept(cache);
	}

	@Override
	public void postHurt(AttackCache cache, LivingHurtEvent event, ItemStack weapon) {
		postHurt.accept(cache);
	}

	@Override
	public void onDamage(AttackCache cache, ItemStack weapon) {
		onDamage.accept(cache);
	}

	@Override
	public void onDamageFinalized(AttackCache cache, ItemStack weapon) {
		onDamageFinalized.accept(cache);
	}

	@Override
	public void onCreateSource(CreateSourceEvent event) {
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

	public CustomAttackListener subscribeAttack(Consumer<AttackCache> onAttack) {
		this.onAttack = onAttack;
		return this;
	}

	public CustomAttackListener subscribePostAttack(Consumer<AttackCache> postAttack) {
		this.postAttack = postAttack;
		return this;
	}

	public CustomAttackListener subscribeHurt(Consumer<AttackCache> onHurt) {
		this.onHurt = onHurt;
		return this;
	}

	public CustomAttackListener subscribeHurtMaximized(Consumer<AttackCache> onHurtMaximized) {
		this.onHurtMaximized = onHurtMaximized;
		return this;
	}

	public CustomAttackListener subscribePostHurt(Consumer<AttackCache> postHurt) {
		this.postHurt = postHurt;
		return this;
	}

	public CustomAttackListener subscribeDamage(Consumer<AttackCache> onDamage) {
		this.onDamage = onDamage;
		return this;
	}

	public CustomAttackListener subscribeDamageFinalized(Consumer<AttackCache> onDamageFinalized) {
		this.onDamageFinalized = onDamageFinalized;
		return this;
	}

	public CustomAttackListener subscribeCreateSource(Consumer<CreateSourceEvent> onCreateSource) {
		this.onCreateSource = onCreateSource;
		return this;
	}

	public void register(int priority) {
		AttackEventHandler.register(priority, this);
	}

}

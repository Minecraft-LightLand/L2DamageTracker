package dev.xkmc.l2damagetracker.contents.attack;

import dev.xkmc.l2damagetracker.init.data.L2DamageTrackerConfig;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class DamageDataExtra implements DamageData.All {

	private DamageSource source;
	private LivingEntity target;
	private LivingEntity attacker;
	private ItemStack weapon = ItemStack.EMPTY;
	private PlayerAttackCache player;
	private DamageContainer cont;

	private final DamageAccumulator offenseModifiers = new DamageAccumulator();
	private final DamageAccumulator defenseModifiers = new DamageAccumulator();
	private LogEntry log;

	private boolean bypassMagic;
	private float originalDamage;
	private boolean noCancellation;

	public boolean bypassMagic() {
		return bypassMagic;
	}

	public DamageSource getSource() {
		return source;
	}

	public DamageContainer getContainer() {
		return cont;
	}

	public LivingEntity getTarget() {
		return target;
	}

	@Nullable
	public LivingEntity getAttacker() {
		return attacker;
	}

	public ItemStack getWeapon() {
		return weapon;
	}

	public float getStrength() {
		return player == null ? 1 : player.getStrength();
	}

	public float getDamageOriginal() {
		return originalDamage;
	}

	public float getDamageIncoming() {
		return offenseModifiers.getMaximized();
	}

	public float getDamageFinal() {
		return defenseModifiers.getMaximized();
	}

	public void addHurtModifier(DamageModifier mod) {
		log.recordModifier(mod);
		offenseModifiers.addHurtModifier(mod);
	}

	public void addDealtModifier(DamageModifier mod) {
		log.recordModifier(mod);
		defenseModifiers.addHurtModifier(mod);
	}

	@Override
	public void setNonCancellable() {
		noCancellation = true;
	}

	private boolean shouldLog() {
		if (getAttacker() instanceof Player && L2DamageTrackerConfig.SERVER.savePlayerAttack.get()) return true;
		if (getTarget() instanceof Player && L2DamageTrackerConfig.SERVER.savePlayerHurt.get()) return true;
		return L2DamageTrackerConfig.SERVER.printDamageTrace.get();
	}

	public void init(DamageSource source, float originalDamage) {
		this.source = source;
		this.originalDamage = originalDamage;
		this.attacker = source.getEntity() instanceof LivingEntity le ? le : null;
		this.bypassMagic = source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) ||
				source.is(DamageTypeTags.BYPASSES_EFFECTS);
	}

	void setupAttackerProfile(@Nullable LivingEntity entity, @Nullable ItemStack stack) {
		if (attacker == null && entity != null) attacker = entity;
		if (weapon.isEmpty() && stack != null) weapon = stack;
	}

	void setupPlayer(PlayerAttackCache prev) {
		player = prev;
		attacker = prev.getAttacker();
		if (!prev.getWeapon().isEmpty()) weapon = prev.getWeapon();
	}

	public void onIncoming(LivingIncomingDamageEvent event, Consumer<LivingIncomingDamageEvent> cons) {
		target = event.getEntity();
		cont = event.getContainer();
		var list = AttackEventHandler.getListeners();
		for (var e : list) e.setupProfile(this, this::setupAttackerProfile);
		log.log(LogEntry.Stage.INCOMING, originalDamage);
		boolean cancelled = false;
		for (var e : list) cancelled |= e.onAttack(this);
		if (!noCancellation && cancelled) {
			event.setCanceled(true);
			return;
		}
		float damage = offenseModifiers.run(event.getAmount(), log.initModifiers(),
				e -> e.onHurt(this),
				e -> e.onHurtMaximized(this),
				event(event, cons));
		event.setAmount(damage);
		log.log(LogEntry.Stage.INCOMING_POST, damage);
	}

	public void onDamage(LivingDamageEvent.Pre event, Consumer<LivingDamageEvent.Pre> cons) {
		log.log(LogEntry.Stage.DAMAGE, event.getOriginalDamage());
		float damage = defenseModifiers.run(event.getOriginalDamage(), log.initModifiers(),
				e -> e.onDamage(this),
				e -> e.onDamageFinalized(this),
				event(event, cons));
		log.log(LogEntry.Stage.DAMAGE_POST, damage);
		event.setNewDamage(damage);
	}

	private static DamageModifier event(LivingIncomingDamageEvent event, Consumer<LivingIncomingDamageEvent> cons) {
		return new Nonlinear(DamageModifier.Order.EVENT, 0, f -> {
			event.setAmount(f);
			cons.accept(event);
			return event.getAmount();
		});
	}

	private static DamageModifier event(LivingDamageEvent.Pre event, Consumer<LivingDamageEvent.Pre> cons) {
		return new Nonlinear(DamageModifier.Order.EVENT, 0, f -> {
			event.setNewDamage(f);
			cons.accept(event);
			return event.getNewDamage();
		});
	}

}

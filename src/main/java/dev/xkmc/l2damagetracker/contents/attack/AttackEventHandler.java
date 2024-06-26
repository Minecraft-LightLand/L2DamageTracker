package dev.xkmc.l2damagetracker.contents.attack;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2damagetracker.init.data.L2DamageTrackerConfig;
import dev.xkmc.l2library.init.L2Library;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = L2DamageTracker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttackEventHandler {

	private static final Map<Integer, AttackListener> LISTENERS = new TreeMap<>();

	/**
	 * 0000 - L2Complements		Special Damage Creation
	 * 1000 - L2DamageTracker	General Attack Listener: crit calculation, create source
	 * 1100 - L2Weaponry		Throwable Source Creation
	 * 2000 - L2Archery			Arrow source modification
	 * 3000 - L2Artifacts		Artifact damage boost
	 * 4000 - L2Weaponry		Primarily post damage
	 * 5000 - L2Complements		Listen only, for material drops
	 */
	public synchronized static void register(int priority, AttackListener entry) {
		while (LISTENERS.containsKey(priority))
			priority++;
		LISTENERS.put(priority, entry);
	}

	public static Collection<AttackListener> getListeners() {
		return LISTENERS.values();
	}

	private static final HashMap<UUID, PlayerAttackCache> PLAYER = new HashMap<>();
	private static final HashMap<UUID, AttackCache> CACHE = new HashMap<>();

	@SubscribeEvent
	public static void onPlayerAttack(AttackEntityEvent event) {
		if (event.getEntity().level().isClientSide())
			return;
		PlayerAttackCache cache = new PlayerAttackCache();
		PLAYER.put(event.getEntity().getUUID(), cache);
		ItemStack stack = event.getEntity().getMainHandItem();
		cache.setupAttackerProfile(event.getEntity(), stack);
		cache.pushPlayer(event);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onCriticalHitFirst(CriticalHitEvent event) {
		if (event.getEntity().level().isClientSide())
			return;
		PlayerAttackCache cache = PLAYER.get(event.getEntity().getUUID());
		if (cache == null) cache = new PlayerAttackCache();
		PLAYER.put(event.getTarget().getUUID(), cache);
		cache.pushCrit(event);
	}

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof AbstractArrow arrow) {
			if (arrow.getOwner() instanceof Player player) {
				double cr = L2DamageTracker.CRIT_RATE.get().getWrappedValue(player);
				double cd = L2DamageTracker.CRIT_DMG.get().getWrappedValue(player);
				double strength = L2DamageTracker.BOW_STRENGTH.get().getWrappedValue(player);
				if (arrow.isCritArrow() && player.getRandom().nextDouble() < cr) {
					strength *= (1 + cd);
				}
				arrow.setBaseDamage((float) (arrow.getBaseDamage() * strength));
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onAttackPre(LivingAttackEvent event) {
		if (event.getEntity().level().isClientSide())
			return;
		if (CACHE.size() > 1000) {
			L2DamageTracker.LOGGER.error("attack cache too large: " + CACHE.size());
			CACHE.clear();
			return;
		}
		UUID id = event.getEntity().getUUID();
		AttackCache cache = CACHE.get(id);
		if (cache != null) {
			if (cache.getStage() == Stage.ACTUALLY_HURT_PRE || cache.getStage() == Stage.HURT_PRE) {
				cache.recursive++;
				if (L2DamageTrackerConfig.COMMON.enableCyclicDamageEventInterrupt.get() &&
						cache.recursive >= L2DamageTrackerConfig.COMMON.cyclicDamageThreshold.get()) {
					L2DamageTracker.LOGGER.error("Cyclic Damage Event Detected");
					L2DamageTracker.LOGGER.throwing(Level.ERROR, new IllegalStateException("Cyclic Damage Event Detected"));
					event.setCanceled(true);
				}
				return;
			}
		}
		boolean replace = cache == null;
		PlayerAttackCache prev = null;
		if (!replace)
			replace = cache.getStage().ordinal() >= Stage.HURT_PRE.ordinal();
		Entity attacker = event.getSource().getEntity();
		if (!replace && attacker != null && PLAYER.containsKey(attacker.getUUID()))
			prev = PLAYER.get(attacker.getUUID());
		if (replace) {
			cache = new AttackCache();
			CACHE.put(id, cache);
		}
		if (prev != null)
			cache.setupPlayer(prev);
		cache.pushAttackPre(event);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public static void onAttackPost(LivingAttackEvent event) {
		if (event.getEntity().level().isClientSide())
			return;
		AttackCache cache = CACHE.get(event.getEntity().getUUID());
		if (cache != null) {
			if (cache.getStage() == Stage.ACTUALLY_HURT_PRE || cache.getStage() == Stage.HURT_PRE) {
				if (cache.recursive > 0) {
					cache.recursive--;
					return;
				}
			}
			cache.pushAttackPost(event);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onActuallyHurtPre(LivingHurtEvent event) {
		if (event.getEntity().level().isClientSide())
			return;
		AttackCache cache = CACHE.get(event.getEntity().getUUID());
		if (cache != null && cache.getStage() == Stage.HURT_POST)
			cache.pushHurtPre(event);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public static void onActuallyHurtPost(LivingHurtEvent event) {
		if (event.getEntity().level().isClientSide())
			return;
		AttackCache cache = CACHE.get(event.getEntity().getUUID());
		if (cache != null && cache.getStage() == Stage.ACTUALLY_HURT_PRE)
			cache.pushHurtPost(event);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onDamagePost(LivingDamageEvent event) {
		if (event.getEntity().level().isClientSide())
			return;
		AttackCache cache = CACHE.get(event.getEntity().getUUID());
		if (cache != null && cache.getStage() == Stage.ACTUALLY_HURT_POST)
			cache.pushDamagePre(event);
	}

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event) {

	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		CACHE.clear();
		PLAYER.clear();
	}

	@Nullable
	public static DamageSource onDamageSourceCreate(CreateSourceEvent event) {
		if (event.getAttacker().level().isClientSide())
			return null;
		PlayerAttackCache cache = null;
		if (PLAYER.containsKey(event.getAttacker().getUUID())) {
			cache = PLAYER.get(event.getAttacker().getUUID());
		}
		if (cache != null)
			event.setPlayerAttackCache(cache);
		getListeners().forEach(e -> e.onCreateSource(event));
		if (event.getPlayerAttackCache() != cache) {
			PLAYER.put(event.getAttacker().getUUID(), event.getPlayerAttackCache());
		}
		if (event.getResult() == null) return null;
		return new DamageSource(event.getRegistry().getHolderOrThrow(event.getResult().type()), event.getDirect(), event.getAttacker());
	}

}

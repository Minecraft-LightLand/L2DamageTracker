package dev.xkmc.l2damagetracker.contents.attack;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.*;

@EventBusSubscriber(modid = L2DamageTracker.MODID)
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

	static final HashMap<UUID, PlayerAttackCache> PLAYER = new HashMap<>();

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
				double cr = player.getAttributeValue(L2DamageTracker.CRIT_RATE.holder());
				double cd = player.getAttributeValue(L2DamageTracker.CRIT_DMG.holder());
				double strength = player.getAttributeValue(L2DamageTracker.BOW_STRENGTH.holder());
				if (arrow.isCritArrow() && player.getRandom().nextDouble() < cr) {
					strength *= (1 + cd);
				}
				arrow.setBaseDamage((float) (arrow.getBaseDamage() * strength));
			}
		}
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		PLAYER.clear();
	}

	public static void onDamageSourceCreate(LivingEntity attacker) {
		if (attacker.level().isClientSide()) return;
		PlayerAttackCache cache = null;
		if (PLAYER.containsKey(attacker.getUUID())) {
			cache = PLAYER.get(attacker.getUUID());
		}
		if (cache != null)
			event.setPlayerAttackCache(cache);
		getListeners().forEach(e -> e.onCreateSource(event));
		if (event.getPlayerAttackCache() != cache) {
			PLAYER.put(attacker.getUUID(), event.getPlayerAttackCache());
		}
	}

}

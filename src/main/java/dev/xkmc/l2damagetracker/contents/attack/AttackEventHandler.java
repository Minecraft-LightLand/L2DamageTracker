package dev.xkmc.l2damagetracker.contents.attack;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import javax.annotation.Nullable;
import java.util.*;

@EventBusSubscriber(modid = L2DamageTracker.MODID, bus = EventBusSubscriber.Bus.GAME)
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

	public static DamageSource createSource(ServerLevel level, @Nullable LivingEntity user, ResourceKey<DamageType> key, @Nullable Entity direct, @Nullable Vec3 pos) {
		var access = level.registryAccess();
		if (user != null) {
			var event = new CreateSourceEvent(access.registryOrThrow(Registries.DAMAGE_TYPE),
					key, user, direct, pos);
			var ans = onDamageSourceCreate(event);
			if (ans != null) return ans;
		}
		return new DamageSource(access.holderOrThrow(key), direct, user == null ? direct : user, pos);
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
		NeoForge.EVENT_BUS.post(event);
		if (event.getPlayerAttackCache() != cache) {
			PLAYER.put(event.getAttacker().getUUID(), event.getPlayerAttackCache());
		}
		if (event.getResult() == null) return null;
		return new DamageSource(event.getRegistry().getHolderOrThrow(event.getResult().type()),
				event.getDirect(), event.getAttacker(), event.getPos());
	}

}

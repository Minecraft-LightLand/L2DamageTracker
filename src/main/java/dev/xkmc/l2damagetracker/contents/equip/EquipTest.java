package dev.xkmc.l2damagetracker.contents.equip;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;
import java.util.function.Supplier;

public class EquipTest {

	public static EquipTest of(LivingEntity le) {
		return ((EquipTestHolder) le).l2$getEquipTest();
	}

	private int lastTick = -1;

	private final List<ItemStack> lastItems = new ArrayList<>();
	private final Multimap<Item, ItemStack> map = Multimaps.newListMultimap(Maps.newHashMap(), ArrayList::new);
	private final Map<Class<?>, Object> cache = new LinkedHashMap<>();

	public List<ItemStack> getAllItems(LivingEntity le) {
		if (le.tickCount == lastTick)
			return lastItems;
		lastItems.clear();
		map.clear();
		cache.clear();
		lastTick = le.tickCount;
		for (var e : EquipmentSlot.values()) {
			var stack = le.getItemBySlot(e);
			if (!stack.isEmpty())
				lastItems.add(stack);
		}
		NeoForge.EVENT_BUS.post(new GatherEquipmentEvent(le, lastItems));
		return lastItems;
	}

	private void verifyMap(LivingEntity le) {
		getAllItems(le);
		if (map.isEmpty() && !lastItems.isEmpty()) {
			for (var e : lastItems) {
				map.put(e.getItem(), e);
			}
		}
	}

	public Set<Item> getItemSet(LivingEntity le) {
		verifyMap(le);
		return map.keySet();
	}

	public Collection<ItemStack> getItemStacks(LivingEntity le, Item item) {
		verifyMap(le);
		return map.get(item);
	}

	public <T> T compute(Class<T> cls, Supplier<T> factory) {
		var prev = cache.get(cls);
		if (prev != null) return Wrappers.cast(prev);
		var ans = factory.get();
		cache.put(cls, ans);
		return ans;
	}

}

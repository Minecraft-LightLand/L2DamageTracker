package dev.xkmc.l2damagetracker.contents.attack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class DamageAccumulator {

	private boolean frozen = true;
	private boolean maximized = false;

	private float finalDamage;

	private final List<DamageModifier> modifiers = new ArrayList<>();

	float run(float original, Consumer<AttackListener> collect, Consumer<AttackListener> maximize) {
		frozen = false;
		AttackEventHandler.getListeners().forEach(collect);
		frozen = true;
		finalDamage = accumulate(original);
		maximized = true;
		AttackEventHandler.getListeners().forEach(maximize);
		return finalDamage;
	}

	private float accumulate(float val) {
		Map<DamageModifier.Order, TreeMap<Integer, DamageModifier>> map = new TreeMap<>();
		for (var e : modifiers) {
			if (!map.containsKey(e.order())) {
				map.put(e.order(), new TreeMap<>());
			}
			var sub = map.get(e.order());
			int i = e.priority();
			while (sub.containsKey(i)) i++;
			sub.put(i, e);
		}
		for (var ent : map.entrySet()) {
			float num = ent.getKey().type.start.start(val);
			for (var e : ent.getValue().values()) {
				num = e.modify(num);
			}
			val = ent.getKey().type.end.end(val, num);
		}
		return val;
	}

	public float getMaximized() {
		if (!maximized)
			throw new IllegalStateException("damage not calculated yet");
		return finalDamage;
	}

	public void addHurtModifier(DamageModifier mod) {
		if (frozen)
			throw new IllegalStateException("modify damage only on event.");
		this.modifiers.add(mod);
	}

}

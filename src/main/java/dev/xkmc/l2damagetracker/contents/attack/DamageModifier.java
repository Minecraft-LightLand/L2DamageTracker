package dev.xkmc.l2damagetracker.contents.attack;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.resources.ResourceLocation;

public interface DamageModifier {

	static DamageModifier nonlinearPre(int priority, Float2FloatFunction func, ResourceLocation id) {
		return new Nonlinear(id, Order.PRE_NONLINEAR, priority, func);
	}

	static DamageModifier multAttr(float val, ResourceLocation id) {
		return new Multiplicative(id, Order.PRE_MULTIPLICATIVE, val);
	}

	static DamageModifier add(float val, ResourceLocation id) {
		return new Additive(id, Order.PRE_ADDITIVE, val);
	}

	static DamageModifier multBase(float val, ResourceLocation id) {
		return new Additive(id, Order.POST_MULT_BASE, val);
	}

	static DamageModifier multTotal(float val, ResourceLocation id) {
		return new Multiplicative(id, Order.POST_MULTIPLICATIVE, val);
	}

	static DamageModifier nonlinearMiddle(int priority, Float2FloatFunction func, ResourceLocation id) {
		return new Nonlinear(id, Order.POST_NONLINEAR, priority, func);
	}

	static DamageModifier addExtra(float val, ResourceLocation id) {
		return new Additive(id, Order.POST_ADDITIVE, val);
	}

	static DamageModifier nonlinearFinal(int priority, Float2FloatFunction func, ResourceLocation id) {
		return new Nonlinear(id, Order.END_NONLINEAR, priority, func);
	}

	String info(float num);

	ResourceLocation id();

	enum Time {
		CRIT,
		ATTACK,
		HURT,
		DAMAGE,
	}

	enum Type {
		ADDITIVE(v -> 0, (v, n) -> v + n),
		MULTIPLICATIVE(v -> 1, (v, n) -> v * n),
		NONLINEAR(v -> v, (v, n) -> n);

		public final Start start;
		public final End end;

		Type(Start start, End end) {
			this.start = start;
			this.end = end;
		}

		public interface Start {

			float start(float val);

		}

		public interface End {

			float end(float val, float num);

		}

	}

	enum Order {
		PRE_NONLINEAR(Type.NONLINEAR),
		PRE_MULTIPLICATIVE(Type.MULTIPLICATIVE),
		PRE_ADDITIVE(Type.ADDITIVE),
		POST_MULT_BASE(Type.MULTIPLICATIVE),
		EVENT(Type.NONLINEAR),
		POST_MULTIPLICATIVE(Type.MULTIPLICATIVE),
		POST_NONLINEAR(Type.NONLINEAR),
		POST_ADDITIVE(Type.ADDITIVE),
		END_NONLINEAR(Type.NONLINEAR);

		public final Type type;

		Order(Type type) {
			this.type = type;
		}
	}

	float modify(float val);

	int priority();

	Order order();

}

record Additive(ResourceLocation id, Order order, float n) implements DamageModifier {

	@Override
	public float modify(float val) {
		return val + n;
	}

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public String info(float num) {
		return n > 0 ? "+" + n : "" + n;
	}

}


record Multiplicative(ResourceLocation id, Order order, float n) implements DamageModifier {

	@Override
	public float modify(float val) {
		return val * n;
	}

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public String info(float num) {
		return "x" + n;
	}

}

record Nonlinear(ResourceLocation id, Order order, int priority, Float2FloatFunction func) implements DamageModifier {

	@Override
	public float modify(float val) {
		return func.get(val);
	}

	@Override
	public String info(float num) {
		return "-> " + num;
	}
}
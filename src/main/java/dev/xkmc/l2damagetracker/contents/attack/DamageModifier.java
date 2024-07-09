package dev.xkmc.l2damagetracker.contents.attack;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;

public interface DamageModifier {

	static DamageModifier nonlinearPre(int priority, Float2FloatFunction func) {
		return new Nonlinear(Order.PRE_NONLINEAR, priority, func);
	}

	static DamageModifier multAttr(float val) {
		return new Multiplicative(Order.PRE_MULTIPLICATIVE, val);
	}

	static DamageModifier add(float val) {
		return new Additive(Order.PRE_ADDITIVE, val);
	}

	static DamageModifier multBase(float val) {
		return new Additive(Order.POST_MULT_BASE, val);
	}

	static DamageModifier multTotal(float val) {
		return new Multiplicative(Order.POST_MULTIPLICATIVE, val);
	}

	static DamageModifier nonlinearMiddle(int priority, Float2FloatFunction func) {
		return new Nonlinear(Order.POST_NONLINEAR, priority, func);
	}

	static DamageModifier addExtra(float val) {
		return new Additive(Order.POST_ADDITIVE, val);
	}

	static DamageModifier nonlinearFinal(int priority, Float2FloatFunction func) {
		return new Nonlinear(Order.END_NONLINEAR, priority, func);
	}

	String info(float num);

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

record Additive(Order order, float n) implements DamageModifier {

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


record Multiplicative(Order order, float n) implements DamageModifier {

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

record Nonlinear(Order order, int priority, Float2FloatFunction func) implements DamageModifier {

	@Override
	public float modify(float val) {
		return func.get(val);
	}

	@Override
	public String info(float num) {
		return "-> " + num;
	}
}
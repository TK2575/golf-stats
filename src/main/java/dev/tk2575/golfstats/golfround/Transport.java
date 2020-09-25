package dev.tk2575.golfstats.golfround;

public class Transport {
	private final int representation;

	public static Transport ride() { return new Transport(0); }

	public static Transport walk() { return new Transport(1); }

	public static Transport walkWithPushCart() { return walk().with(pushCart()); }

	public static Transport walkWithCaddie() { return walk().with(caddie()); }

	private static Transport caddie() { return new Transport(2); }

	private static Transport pushCart() { return new Transport(4); }

	private static Transport unknown() { return new Transport(8); }

	private Transport(int representation) {
		this.representation = representation;
	}

	public static Transport valueOf(String s) {
		if (s.equalsIgnoreCase("cart")) {
			return ride();
		}
		if (s.equalsIgnoreCase("walk (push)")) {
			return walkWithPushCart();
		}
		if (s.equalsIgnoreCase("walk (caddie)")) {
			return walkWithCaddie();
		}
		if (s.equalsIgnoreCase("walk")) {
			return walk();
		}
		return unknown();
	}

	private Transport with(Transport status) {
		return new Transport(this.representation | status.representation);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (this.isSupersetOf(ride())) {
			sb.append("Cart").append(" ");
		}

		if (this.isSupersetOf(walk())) {
			sb.append("Walking").append(" ");
		}

		if (this.isSupersetOf(caddie())) {
			sb.append("(Caddie)").append(" ");
		}

		if (this.isSupersetOf(pushCart())) {
			sb.append("(Push)").append(" ");
		}

		return sb.toString().trim();
	}

	public boolean isSupersetOf(Transport other) {
		return (this.representation & other.representation) == other.representation;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Transport &&
				this.equals((Transport)other);
	}

	@Override
	public int hashCode() {
		return this.representation;
	}

	private boolean equals(Transport other) {
		return this.representation == other.representation;
	}
}

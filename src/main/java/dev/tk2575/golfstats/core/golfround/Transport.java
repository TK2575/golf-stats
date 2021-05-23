package dev.tk2575.golfstats.core.golfround;

public class Transport {
	private final int representation;

	public static Transport ride() { return new Transport(0); }

	public static Transport walk() { return new Transport(1); }

	public static Transport walkWithPushCart() { return walk().with(pushCart()); }

	public static Transport walkWithCaddie() { return walk().with(caddie()); }

	private static Transport caddie() { return new Transport(2); }

	private static Transport pushCart() { return new Transport(4); }

	private static Transport unknown() { return new Transport(8); }

	private static Transport various() { return new Transport(16); }

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

	public static Transport compositeOf(Transport transport1, Transport transport2) {
		if (transport1.equals(transport2)) {
			return transport1;
		}
		if (bothSupersetOf(walk(), transport1, transport2)) {
			return walk();
		}
		if (bothSupersetOf(ride(), transport1, transport2)) {
			return ride();
		}
		return various();
	}

	private static boolean bothSupersetOf(Transport parent, Transport transport1, Transport transport2) {
		return transport1.isSupersetOf(parent) && transport2.isSupersetOf(parent);
	}

	private Transport with(Transport status) {
		return new Transport(this.representation | status.representation);
	}

	@Override
	public String toString() {
		if (this.equals(unknown())) {
			return "Unknown";
		}
		if (this.equals(various())) {
			return "Various";
		}

		StringBuilder sb = new StringBuilder();

		if (this.isSupersetOf(ride())) {
			sb.append("Cart").append(", ");
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

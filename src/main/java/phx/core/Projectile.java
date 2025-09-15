package phx.core;

public final class Projectile {
    private Projectile() { }

    public static double timeOfFlight(double v0, double thetaDeg, double g) {
        double th = Math.toRadians(thetaDeg);
        return (2.0 * v0 * Math.sin(th)) / g;
    }

    public static double range(double v0, double thetaDeg, double g) {
        double th = Math.toRadians(thetaDeg);
        return (v0 * v0 * Math.sin(2.0 * th)) / g;
    }

    public static double hMax(double v0, double thetaDeg, double g) {
        double th = Math.toRadians(thetaDeg);
        return (Math.pow(v0 * Math.sin(th), 2.0)) / (2.0 * g);
    }
}

package phx.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ProjectileTest {
    private static final double G = 9.81;
    private static final double EPS = 1e-9;

    @Test
    void theta45() {
        double v0 = 20.0, th = 45.0;
        assertEquals((2 * v0 * Math.sin(Math.toRadians(th))) / G,
                Projectile.timeOfFlight(v0, th, G), EPS);
        assertEquals((v0 * v0 * Math.sin(2 * Math.toRadians(th))) / G,
                Projectile.range(v0, th, G), EPS);
        double h = Math.pow(v0 * Math.sin(Math.toRadians(th)), 2) / (2 * G);
        assertEquals(h, Projectile.hMax(v0, th, G), EPS);
    }

    @Test
    void theta30() {
        double v0 = 18.0, th = 30.0;
        assertEquals((2 * v0 * Math.sin(Math.toRadians(th))) / G,
                Projectile.timeOfFlight(v0, th, G), EPS);
        assertEquals((v0 * v0 * Math.sin(2 * Math.toRadians(th))) / G,
                Projectile.range(v0, th, G), EPS);
        double h = Math.pow(v0 * Math.sin(Math.toRadians(th)), 2) / (2 * G);
        assertEquals(h, Projectile.hMax(v0, th, G), EPS);
    }

    @Test
    void theta60() {
        double v0 = 14.0, th = 60.0;
        assertEquals((2 * v0 * Math.sin(Math.toRadians(th))) / G,
                Projectile.timeOfFlight(v0, th, G), EPS);
        assertEquals((v0 * v0 * Math.sin(2 * Math.toRadians(th))) / G,
                Projectile.range(v0, th, G), EPS);
        double h = Math.pow(v0 * Math.sin(Math.toRadians(th)), 2) / (2 * G);
        assertEquals(h, Projectile.hMax(v0, th, G), EPS);
    }
}

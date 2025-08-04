package mekanism.common.lib;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.VersionParsingException;

/**
 * Version v2.0.0. Simple version handling for Mekanism.
 *
 * @param major Major number for version
 * @param minor Minor number for version
 * @param build Build number for version
 *
 * @author AidanBrady
 */
@Deprecated
public record Version(net.fabricmc.loader.api.Version version) implements Comparable<Version> {

    /**
     * Helper to make it so this is the only class with weird errors in IntelliJ (that don't actually exist), instead of having our main class also have "errors"
     */
    public Version(ModContainer container) {
        this(container.getMetadata().getVersion());
    }

    public Version(int major, int minor, int build) throws VersionParsingException {
        this(net.fabricmc.loader.api.Version.parse("%d.%d+%d".formatted(major, minor, build)));
    }

    /**
     * Gets a version object from a string.
     *
     * @param s - string object
     *
     * @return version if applicable, otherwise null
     */
    public static Version get(String s) {
        String[] split = s.replace('.', ':').split(":");
        if (split.length != 3) {
            return null;
        }

        int[] digits = new int[3];
        for (int i = 0; i < digits.length; i++) {
            try {
                digits[i] = Integer.parseInt(split[i]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        try {
            return new Version(digits[0], digits[1], digits[2]);
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int compareTo(Version version) {
        return this.version.compareTo(version.version);
    }

    @Override
    public String toString() {
        return this.version.toString();
    }
}
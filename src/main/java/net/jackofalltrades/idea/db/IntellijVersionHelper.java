package net.jackofalltrades.idea.db;

import net.jackofalltrades.idea.IntellijBuildVersion;
import net.jackofalltrades.idea.IntellijBuildVersionFormatException;

/**
 * Methods used by the H2 database to interact with IntellijBuildVersion object types.
 *
 * @author bhandy
 */
public class IntellijVersionHelper {

    public static boolean isIntellijVersion(String buildVersion) {
        if (buildVersion == null) {
            return true;
        }

        return parseBuildNumber(buildVersion) != null;
    }

    public static boolean isIntellijVersionInRange(String buildNumber, String lowEndInclusive, String highEndInclusive) {
        IntellijBuildVersion buildVersion = parseBuildNumber(buildNumber);
        IntellijBuildVersion sinceVersion = parseBuildNumber(lowEndInclusive);
        IntellijBuildVersion untilVersion = parseBuildNumber(highEndInclusive);

        if (buildVersion == null || sinceVersion == null || untilVersion == null) {
            return false;
        }

        return (buildVersion.equals(sinceVersion) || sinceVersion.isBefore(buildVersion))
                && (buildVersion.equals(untilVersion) || untilVersion.isAfter(buildVersion));

    }

    private static IntellijBuildVersion parseBuildNumber(String buildNumber) {
        try {
            return IntellijBuildVersion.fromString(buildNumber);
        } catch (IntellijBuildVersionFormatException e) {
            return null;
        }
    }

    private IntellijVersionHelper() {

    }

}

package net.jackofalltrades.idea;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a defined version number of IntelliJ for determining supported version of the IDE for the plugins.
 *
 * @author bhandy
 */
public class IntellijBuildVersion implements Serializable {

    private static final Pattern BUILD_NUMBER_PATTERN = Pattern.compile("(?:I[UC]-)?(\\d+)(?:\\.(?:(\\*)|(?:(\\d+)((?:\\.\\d+)+)?)))?");
    private static final Splitter BUILD_NUMBER_SPLITTER = Splitter.on('.');
    private static final Joiner BUILD_NUMBER_JOINER = Joiner.on('.');

    private final int branchNumber;
    private final int buildNumber;
    private final int[] additionalComponents;

    public static IntellijBuildVersion fromString(String buildVersion) {
        Matcher buildNumberMatcher = BUILD_NUMBER_PATTERN.matcher(Optional.ofNullable(buildVersion).orElse(""));
        if (!buildNumberMatcher.matches()) {
            throw new IntellijBuildVersionFormatException(String.format("'%s' is not a valid IntelliJ build number.", buildVersion));
        }

        int branchNumber = Integer.parseInt(buildNumberMatcher.group(1));
        int buildNumber = parseBuildNumber(Optional.ofNullable(buildNumberMatcher.group(3)).orElse(buildNumberMatcher.group(2)));

        int[] additionalComponents = new int[0];
        if (buildNumberMatcher.group(4) != null) {
            String additionaBuildVersionComponents = buildNumberMatcher.group(4);
            if (additionaBuildVersionComponents.length() > 1) {
                additionalComponents = parseBuildComponents(additionaBuildVersionComponents.substring(1));
            }
        }

        return new IntellijBuildVersion(branchNumber, buildNumber, additionalComponents);
    }

    private IntellijBuildVersion(int branchNumber, int buildNumber, int... additionalComponents) {
        this.branchNumber = branchNumber;
        this.buildNumber = buildNumber;
        this.additionalComponents = additionalComponents;
    }

    public int getBranchNumber() {
        return branchNumber;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public int[] getAdditionalComponents() {
        return additionalComponents;
    }

    public boolean isBefore(IntellijBuildVersion targetBuildVersion) {
        if (!equals(targetBuildVersion)) {
            if (this.branchNumber == targetBuildVersion.branchNumber) {
                if (this.buildNumber == targetBuildVersion.buildNumber) {
                    return compareAdditionalComponents(targetBuildVersion.additionalComponents) == -1;
                }

                return this.buildNumber < targetBuildVersion.buildNumber;
            }

            return this.branchNumber < targetBuildVersion.branchNumber;
        }

        return false;
    }

    public boolean isAfter(IntellijBuildVersion targetBuildVersion) {
        if (!equals(targetBuildVersion)) {
            if (this.branchNumber == targetBuildVersion.branchNumber) {
                if (this.buildNumber == targetBuildVersion.buildNumber) {
                    return compareAdditionalComponents(targetBuildVersion.additionalComponents) == 1;
                }

                return this.buildNumber > targetBuildVersion.buildNumber;
            }

            return this.branchNumber > targetBuildVersion.branchNumber;
        }

        return false;
    }

    @Override
    public String toString() {
        List<? super Comparable> buildNumberParts = Lists.newArrayList(branchNumber);
        if (buildNumber > 0) {
            if (buildNumber == Integer.MAX_VALUE) {
                buildNumberParts.add('*');
            } else {
                buildNumberParts.add(buildNumber);
            }
        }
        if (additionalComponents.length > 0) {
            for (int additionalComponent : additionalComponents) {
                buildNumberParts.add(additionalComponent);
            }
        }

        return BUILD_NUMBER_JOINER.join(buildNumberParts);
    }

    @Override
    public boolean equals(Object target) {
        if (this == target) {
            return true;
        }

        if (target instanceof IntellijBuildVersion) {
            IntellijBuildVersion buildVersionTarget = (IntellijBuildVersion) target;
            return this.branchNumber == buildVersionTarget.branchNumber
                    && this.buildNumber == buildVersionTarget.buildNumber
                    && Arrays.equals(this.additionalComponents, buildVersionTarget.additionalComponents);
        }

        return false;
    }

    private int compareAdditionalComponents(int[] targetAdditionalComponents) {
        Comparator<Integer> integerComparator = Comparator.naturalOrder();
        for (int componentIndex = 0;
                 componentIndex < this.additionalComponents.length && componentIndex < targetAdditionalComponents.length;
                 componentIndex++) {
            int comparisonResult = integerComparator.compare(this.additionalComponents[componentIndex], targetAdditionalComponents[componentIndex]);
            if (comparisonResult == 0) {
                continue;
            }

            return comparisonResult;
        }

        // if we've gotten to this point, the length better be different between the arrays. the equality case is
        // handled outside of this method.
        return this.additionalComponents.length < targetAdditionalComponents.length ? -1 : 1;
    }

    private static int[] parseBuildComponents(String additionalBuildVersionComponents) {
        List<String> buildVersionComponents = BUILD_NUMBER_SPLITTER.splitToList(additionalBuildVersionComponents);
        int[] versionNumberComponents = new int[buildVersionComponents.size()];

        int currentBuildVersionIndex = 0;
        for (String buildVersionComponent : buildVersionComponents) {
            versionNumberComponents[currentBuildVersionIndex++] = Integer.parseInt(buildVersionComponent);
        }

        return versionNumberComponents;
    }

    private static int parseBuildNumber(String buildNumber) {
        if (buildNumber == null) {
            return 0;
        }

        return buildNumber.equals("*") ? Integer.MAX_VALUE : Integer.parseInt(buildNumber);
    }

}

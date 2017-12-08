package net.jackofalltrades.idea;

import com.google.common.collect.ImmutableSet;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes an IntelliJ IDEA plugin
 */
public class PluginDescriptor {

    private final String id;
    private final String name;
    private final String vendorEmail;
    private final String vendorUrl;
    private final String vendorName;
    private final String version;
    private final IntellijBuildVersion earliestSupportedBuildNumber;
    private final IntellijBuildVersion latestSupportedBuildNumber;
    private final String description;
    private final Set<String> requiredDependencies;
    private final Set<String> optionalDependencies;
    private final String changeNotes;
    private final Path sourceArchive;
    private final long archiveSize;
    private final long lastModifiedTime;

    public static Builder builder() {
        return new Builder();
    }

    public PluginDescriptor(String id, String name, String vendorEmail, String vendorUrl, String vendorName,
                            String version, IntellijBuildVersion earliestSupportedBuildNumber,
                            IntellijBuildVersion latestSupportedBuildNumber, String description,
                            Set<String> requiredDependencies, Set<String> optionalDependencies, String changeNotes,
                            Path sourceArchive, long archiveSize, long lastModifiedTime) {
        this.id = id;
        this.name = name;
        this.vendorEmail = vendorEmail;
        this.vendorUrl = vendorUrl;
        this.vendorName = vendorName;
        this.version = version;
        this.earliestSupportedBuildNumber = earliestSupportedBuildNumber;
        this.latestSupportedBuildNumber = latestSupportedBuildNumber;
        this.description = description;
        this.requiredDependencies = ImmutableSet.copyOf(requiredDependencies);
        this.optionalDependencies = ImmutableSet.copyOf(optionalDependencies);
        this.changeNotes = changeNotes;
        this.sourceArchive = sourceArchive;
        this.archiveSize = archiveSize;
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public String getVendorUrl() {
        return vendorUrl;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getVersion() {
        return version;
    }

    public IntellijBuildVersion getEarliestSupportedBuildNumber() {
        return earliestSupportedBuildNumber;
    }

    public IntellijBuildVersion getLatestSupportedBuildNumber() {
        return latestSupportedBuildNumber;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getRequiredDependencies() {
        return requiredDependencies;
    }

    public Set<String> getOptionalDependencies() {
        return optionalDependencies;
    }

    public String getChangeNotes() {
        return changeNotes;
    }

    public Path getSourceArchive() {
        return sourceArchive;
    }

    public long getArchiveSize() {
        return archiveSize;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public boolean supportsVersion(IntellijBuildVersion version) {
        return earliestSupportedBuildNumber.equals(version) || latestSupportedBuildNumber.equals(version)
                || (earliestSupportedBuildNumber.isBefore(version) && latestSupportedBuildNumber.isAfter(version));
    }

    public PluginDescriptor linkToSourceArchive(Path sourceArchive, long size, long lastModifiedTime) {
        PluginDescriptor.Builder builder = builder()
                .withId(id)
                .withName(name)
                .withVendorEmail(vendorEmail)
                .withVendorUrl(vendorUrl)
                .withVendorName(vendorName)
                .withVersion(version)
                .withEarliestSupportedBuildNumber(earliestSupportedBuildNumber)
                .withLatestSupportedBuilderNumber(latestSupportedBuildNumber)
                .withDescription(description)
                .withChangeNotes(changeNotes);

        requiredDependencies.stream().forEach(builder::withRequiredDependency);
        optionalDependencies.stream().forEach(builder::withOptionalDependency);

        return builder.withArchiveSize(size)
                    .withLastModifiedTime(lastModifiedTime)
                    .withSourceArchive(sourceArchive).build();
    }

    public static class Builder {

        private String id;
        private String name;
        private String vendorEmail;
        private String vendorUrl;
        private String vendorName;
        private String version;
        private IntellijBuildVersion earliestSupportedBuildNumber;
        private IntellijBuildVersion latestSupportedBuildNumber;
        private String description;
        private Set<String> requiredDependencies = new HashSet<>();
        private Set<String> optionalDependencies = new HashSet<>();
        private String changeNotes;
        private Path sourceArchive;
        private long archiveSize;
        private long lastModifiedTime;

        private Builder() {

        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withVendorEmail(String vendorEmail) {
            this.vendorEmail = vendorEmail;
            return this;
        }

        public Builder withVendorUrl(String vendorUrl) {
            this.vendorUrl = vendorUrl;
            return this;
        }

        public Builder withVendorName(String vendorName) {
            this.vendorName = vendorName;
            return this;
        }

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder withEarliestSupportedBuildNumber(IntellijBuildVersion earliestSupportedBuildNumber) {
            this.earliestSupportedBuildNumber = earliestSupportedBuildNumber;
            return this;
        }

        public Builder withLatestSupportedBuilderNumber(IntellijBuildVersion latestSupportedBuilderNumber) {
            this.latestSupportedBuildNumber = latestSupportedBuilderNumber;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withChangeNotes(String changeNotes) {
            this.changeNotes = changeNotes;
            return this;
        }

        public Builder withRequiredDependency(String dependency) {
            this.requiredDependencies.add(dependency);
            return this;
        }

        public Builder withOptionalDependency(String dependency) {
            this.optionalDependencies.add(dependency);
            return this;
        }

        public Builder withSourceArchive(Path sourceArchive) {
            this.sourceArchive = sourceArchive;
            return this;
        }

        public Builder withArchiveSize(long archiveSize) {
            this.archiveSize = archiveSize;
            return this;
        }

        public Builder withLastModifiedTime(long lastModifiedTime) {
            this.lastModifiedTime = lastModifiedTime;
            return this;
        }

        public PluginDescriptor build() {
            return new PluginDescriptor(id, name, vendorEmail, vendorUrl, vendorName, version, earliestSupportedBuildNumber,
                    latestSupportedBuildNumber, description, requiredDependencies, optionalDependencies, changeNotes, sourceArchive,
                    archiveSize, lastModifiedTime);
        }

    }

}

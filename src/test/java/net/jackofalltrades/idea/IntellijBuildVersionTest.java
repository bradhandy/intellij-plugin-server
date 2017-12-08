package net.jackofalltrades.idea;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class IntellijBuildVersionTest {

    @Test
    public void verifySerializableFunctionality() throws Exception {
        IntellijBuildVersion intellijBuildVersion = IntellijBuildVersion.fromString("171.23423.34");
        assertTrue("The IntellijBuildVersion class is not Serializable.", intellijBuildVersion instanceof Serializable);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(intellijBuildVersion);
        objectOutputStream.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        IntellijBuildVersion deserializedIntellijBuildVersion = (IntellijBuildVersion) objectInputStream.readObject();

        assertEquals("The deserialized IntellijBuildVersion does not match.", intellijBuildVersion, deserializedIntellijBuildVersion);
    }

    @Test
    public void verifyFullPluginVersion() {
        IntellijBuildVersion intellijBuildVersion = IntellijBuildVersion.fromString("171.1.3432.11");

        assertNotNull("There should be an IntelliJBuildVersion.", intellijBuildVersion);
        assertEquals("The branch number does not match.", 171, intellijBuildVersion.getBranchNumber());
        assertEquals("The build number does not match.", 1, intellijBuildVersion.getBuildNumber());
        assertArrayEquals("The additional version components do not match.", new int[]{3432, 11},
                intellijBuildVersion.getAdditionalComponents());
    }

    @Test
    public void verifyPluginVersionComparisonWhenBranchNumberIsOnlyComponent() {
        IntellijBuildVersion buildVersion = IntellijBuildVersion.fromString("2");
        assertTrue("The build version should be before the provided version.",
                buildVersion.isBefore(IntellijBuildVersion.fromString("2.1")));
        assertTrue("The build version should be after the provided version.",
                buildVersion.isAfter(IntellijBuildVersion.fromString("1.9999.9999")));
    }

    @Test
    public void verifyPluginVersionComparisonWhenBranchNumberIsDifferent() {
        IntellijBuildVersion buildVersion = IntellijBuildVersion.fromString("1.2.3");
        IntellijBuildVersion anotherBuildVersion = IntellijBuildVersion.fromString("2.2.3");

        assertTrue("The build version should be before the provided version.", buildVersion.isBefore(anotherBuildVersion));
        assertTrue("The build version should be after the provided version.", anotherBuildVersion.isAfter(buildVersion));
    }

    @Test
    public void verifyPluginVersionComparisonWhenBuildNumberIsDifferent() {
        IntellijBuildVersion buildVersion = IntellijBuildVersion.fromString("1.2.3");
        IntellijBuildVersion anotherBuildVersion = IntellijBuildVersion.fromString("1.3.3");

        assertTrue("The build version should be before the provided version.", buildVersion.isBefore(anotherBuildVersion));
        assertTrue("The build version should be after the provided version.", anotherBuildVersion.isAfter(buildVersion));
    }

    @Test
    public void verifyPluginVersionComparisonWhenAdditionalComponentsAreDifferent() {
        IntellijBuildVersion buildVersion = IntellijBuildVersion.fromString("1.2.3.4");
        IntellijBuildVersion anotherBuildVersion = IntellijBuildVersion.fromString("1.2.3.5");

        assertTrue("The build version should be before the provided version.", buildVersion.isBefore(anotherBuildVersion));
        assertTrue("The build version should be after the provided version.", anotherBuildVersion.isAfter(buildVersion));
    }

    @Test
    public void verifyPluginVersionComparisonWhenAdditionalComponentsArePresentWithDifferentSize() {
        IntellijBuildVersion buildVersion = IntellijBuildVersion.fromString("1.2.3");
        IntellijBuildVersion anotherBuildVersion = IntellijBuildVersion.fromString("1.2.3.5");

        assertTrue("The build version should be before the provided version.", buildVersion.isBefore(anotherBuildVersion));
        assertTrue("The build version should be after the provided version.", anotherBuildVersion.isAfter(buildVersion));
    }

    @Test
    public void verifyPluginVersionComparisonWhenAdditionalComponentsMissingForOneVersionNumber() {
        IntellijBuildVersion buildVersion = IntellijBuildVersion.fromString("1.2");
        IntellijBuildVersion anotherBuildVersion = IntellijBuildVersion.fromString("1.2.3");

        assertTrue("The build version should be before the provided version.", buildVersion.isBefore(anotherBuildVersion));
        assertTrue("The build version should be after the provided version.", anotherBuildVersion.isAfter(buildVersion));
    }

    @Test
    public void verifyOpenVersionNumberComparison() {
        IntellijBuildVersion openIntellijBuildVersion = IntellijBuildVersion.fromString("1.*");
        assertTrue("The open build should be after the provided version.",
                openIntellijBuildVersion.isAfter(IntellijBuildVersion.fromString("1.2.3")));
        assertTrue("The open build should be after the provided version.",
                openIntellijBuildVersion.isAfter(IntellijBuildVersion.fromString("1.99.9999")));
        assertFalse("The open build should not be after the provided version.",
                openIntellijBuildVersion.isAfter(IntellijBuildVersion.fromString("2")));
    }

    @Test
    public void verifyEquals() {
        assertEquals("The build versions should be equal.", IntellijBuildVersion.fromString("1"),
                IntellijBuildVersion.fromString("1"));
        assertEquals("The build versions should be equal.", IntellijBuildVersion.fromString("1.2"),
                IntellijBuildVersion.fromString("1.2"));
        assertEquals("The build versions should be equal.", IntellijBuildVersion.fromString("1.2.3"),
                IntellijBuildVersion.fromString("1.2.3"));
        assertEquals("The build versions should be equal.", IntellijBuildVersion.fromString("1.2.3.4"),
                IntellijBuildVersion.fromString("1.2.3.4"));

        assertNotEquals("The build versions should not be equal.", IntellijBuildVersion.fromString("1"),
                IntellijBuildVersion.fromString("2"));
        assertNotEquals("The build versions should not be equal.", IntellijBuildVersion.fromString("1"),
                IntellijBuildVersion.fromString("1.2"));
        assertNotEquals("The build versions should not be equal.", IntellijBuildVersion.fromString("1.2"),
                IntellijBuildVersion.fromString("1.3"));
        assertNotEquals("The build versions should not be equal.", IntellijBuildVersion.fromString("1.2"),
                IntellijBuildVersion.fromString("1.2.4"));
    }

    @Test(expected = IntellijBuildVersionFormatException.class)
    public void verifyInvalidBuildNumberThrowsAnException() {
        IntellijBuildVersion.fromString("1..2");
    }

    @Test(expected = IntellijBuildVersionFormatException.class)
    public void verifyNullBuildNumberThrowsAnException() {
        IntellijBuildVersion.fromString(null);
    }

    @Test
    public void verifyEqualityCheckWithIncompatibleOrNullObjectEvaluatesToFalse() {
        assertFalse("The check against 'null' should return false.", IntellijBuildVersion.fromString("1").equals(null));
        assertFalse("The check against an incompatible object should return false.",
                IntellijBuildVersion.fromString("1").equals(new Object()));
    }

    @Test
    public void verifyEqualBuildVersionIsNeitherBeforeNorAfter() {
        IntellijBuildVersion buildVersion = IntellijBuildVersion.fromString("1");

        assertFalse("The build version should not be before itself.", buildVersion.isBefore(buildVersion));
        assertFalse("The build version should not be after itself.", buildVersion.isAfter(buildVersion));
    }

    @Test
    public void verifyToStringWorksCorrectly() {
        assertEquals("The toString implementation is not correct.", IntellijBuildVersion.fromString("171.4.3.324").toString(), "171.4.3.324");
        assertEquals("The open build number is not formatted correctly.", IntellijBuildVersion.fromString("171.*").toString(), "171.*");
        assertEquals("The branch-number-only build number is not formatted correctly.", IntellijBuildVersion.fromString("171").toString(), "171");
    }

}

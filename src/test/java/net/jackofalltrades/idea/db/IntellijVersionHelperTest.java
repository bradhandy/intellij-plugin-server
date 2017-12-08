package net.jackofalltrades.idea.db;

import net.jackofalltrades.idea.IntellijBuildVersionFormatException;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntellijVersionHelperTest {

    @Test
    public void verifyIntellijBuildVersionCanBeVerified() {
        assertTrue("The value should be a valid IntellijBuildVersion.", IntellijVersionHelper.isIntellijVersion("1.2.3"));
    }

    @Test
    public void verifyNonIntellijBuildVersionFailsValidation() {
        assertFalse("The value should not be a valid IntellijBuildVersion.", IntellijVersionHelper.isIntellijVersion("1.2..3"));
    }

    @Test
    public void verifyNullIntellijBuildVersionVerifies() {
        assertTrue("The value should be a valid IntellijBuildVersion.", IntellijVersionHelper.isIntellijVersion(null));
    }

    @Test
    public void verifyRangeValidationFailsIfAnyOfTheBuildNumbersIsInvalid() {
        assertFalse("The build version should not be valid when the build number is invalid.",
                IntellijVersionHelper.isIntellijVersionInRange("1..2", "1.0", "1.*"));
        assertFalse("The build version should not be valid when the low end build number is invalid.",
                IntellijVersionHelper.isIntellijVersionInRange("1.2", "1..0", "1.*"));
        assertFalse("The build version should not be valid when the high end build number is invalid.",
                IntellijVersionHelper.isIntellijVersionInRange("1.2", "1.0", "1..*"));
    }

    @Test
    public void verifyBuildVersionIsNotInProvidedRange() {
        assertFalse("The build version should not be in the provided range.",
                IntellijVersionHelper.isIntellijVersionInRange("2", "1.0", "1.*"));
    }

    @Test
    public void verifyBuildVersionIsInProvidedRange() {
        assertTrue("The build version should be in the provided range.",
                IntellijVersionHelper.isIntellijVersionInRange("1.2.3", "1.0", "1.*"));
    }

    @Test
    public void verifyBuildVersionIsInProvidedRangeWhenEqualToTheLowEnd() {
        assertTrue("The build version should be in the provided range.",
                IntellijVersionHelper.isIntellijVersionInRange("1.2.3", "1.0", "1.*"));
    }

    @Test
    public void verifyBuildVersionIsInProvidedRangeWhenEqualToTheHighEnd() {
        assertTrue("The build version should be in the provided range.",
                IntellijVersionHelper.isIntellijVersionInRange("1.2.3", "1.0", "1.2.3"));
    }

}
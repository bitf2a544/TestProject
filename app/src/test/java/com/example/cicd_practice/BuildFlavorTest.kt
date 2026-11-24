package com.example.cicd_practice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifies that every product flavor (dev, uat, production) is wired correctly.
 * The same test runs once per flavor in CI (test<Flavor>DebugUnitTest), so a
 * misconfigured applicationId / environment fails the pipeline.
 */
class BuildFlavorTest {

    private val baseId = "com.example.cicd_practice"

    @Test
    fun environment_isOneOfKnownFlavors() {
        assertTrue(
            "Unexpected ENVIRONMENT: ${BuildConfig.ENVIRONMENT}",
            BuildConfig.ENVIRONMENT in setOf("dev", "uat", "production")
        )
    }

    @Test
    fun applicationId_matchesEnvironment() {
        val expected = when (BuildConfig.ENVIRONMENT) {
            "dev" -> "$baseId.dev"
            "uat" -> "$baseId.uat"
            else -> baseId
        }
        assertEquals(expected, BuildConfig.APPLICATION_ID)
    }

    @Test
    fun versionName_matchesEnvironment() {
        when (BuildConfig.ENVIRONMENT) {
            "dev" -> assertTrue(BuildConfig.VERSION_NAME.endsWith("-dev"))
            "uat" -> assertTrue(BuildConfig.VERSION_NAME.endsWith("-uat"))
            else -> assertTrue(BuildConfig.VERSION_NAME.matches(Regex("""\d+(\.\d+)*""")))
        }
    }

    @Test
    fun productionBuild_isNeverDebuggableFlavorSuffix() {
        if (BuildConfig.ENVIRONMENT == "production") {
            assertTrue(!BuildConfig.APPLICATION_ID.endsWith(".dev"))
            assertTrue(!BuildConfig.APPLICATION_ID.endsWith(".uat"))
        }
    }
}

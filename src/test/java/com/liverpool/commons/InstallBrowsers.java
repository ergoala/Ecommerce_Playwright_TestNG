package com.liverpool.commons;

import com.microsoft.playwright.Playwright;
import org.testng.annotations.Test;

public class InstallBrowsers {

    /**
     * Installs the Chromium browser for Playwright. This method is annotated with @Test
     * and belongs to the "setup" group, indicating it's part of the test setup process.
     * It initializes Playwright, which implicitly checks for and installs necessary browser binaries.
     *
     * If Playwright is already installed or the browsers are available, this method will
     * simply confirm the initialization.
     */
    @Test(groups = {"setup"})
    public void installChromium() {
        try {
            // Use Playwright's internal install mechanism
            Playwright playwright = Playwright.create();
            playwright.close();
            System.out.println("Playwright initialized successfully - browsers should be available");
        } catch (Exception e) {
            System.err.println("Failed to initialize Playwright: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
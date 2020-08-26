package com.lazerycode.selenium;

import com.lazerycode.selenium.config.DriverFactory;
import com.lazerycode.selenium.listeners.ScreenshotListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Listeners(ScreenshotListener.class)
public class DriverBase {
    protected WebDriver driver;

    private static List<DriverFactory> webDriverThreadPool = Collections.synchronizedList(new ArrayList<DriverFactory>());
    private static ThreadLocal<DriverFactory> driverFactoryThread;

    @BeforeMethod
    public void setUp() throws Exception {
        this.driver = getWebDriver();
    }

    @BeforeSuite(alwaysRun = true)
    public static void instantiateDriverObject() {
        driverFactoryThread = ThreadLocal.withInitial(() -> {
            DriverFactory driverFactory = new DriverFactory();
            webDriverThreadPool.add(driverFactory);
            return driverFactory;
        });
    }

    public static RemoteWebDriver getDriver() throws Exception {
        return driverFactoryThread.get().getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public static void clearCookies() {
        try {
            driverFactoryThread.get().getStoredDriver().manage().deleteAllCookies();
        } catch (Exception ignored) {
            System.out.println("Unable to clear cookies, driver object is not viable...");
        }
    }

    @AfterSuite(alwaysRun = true)
    public static void closeDriverObjects() {
        for (DriverFactory driverFactory : webDriverThreadPool) {
            driverFactory.quitDriver();
        }
    }

    private WebDriver getWebDriver() throws Exception {
        // Create a new WebDriver instance
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.
        return getDriver();
    }
}
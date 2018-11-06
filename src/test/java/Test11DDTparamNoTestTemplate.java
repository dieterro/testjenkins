
package DataDrivenTesting;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import phptravelsPageObjectsFactory.User;
import phptravelsPageObjectsFactory.HomePage;
import phptravelsPageObjectsFactory.WelcomePage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import org.openqa.selenium.firefox.FirefoxDriver;  //to stop geckodriver logging

import io.github.bonigarcia.BrowserBuilder;
import io.github.bonigarcia.BrowsersTemplate.Browser;
import io.github.bonigarcia.SeleniumExtension;
import io.github.bonigarcia.SeleniumJupiter;

import static org.assertj.core.api.Assertions.*;  // assertThat and more
import org.assertj.core.api.SoftAssertions;

// Import log4j2 classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.stream.Stream;

public class Test11DDTparamNoTestTemplate {

    //TODO REMEMBER THAT DOCKER FOR MAC MUST BE RUNNING TO BE ABLE TO PULL DOCKER IMAGES!

    //TODO dus in Test9 (met Test10) probeerde ik @TestTemplate te laten werken met @ParameterizedTest

    //TODO https://stackoverflow.com/questions/53005926/combine-testtemplate-with-parameterizedtest
    //TODO https://github.com/bonigarcia/selenium-jupiter/issues/24
    //TODO WACHTEN OP ANTWOORD EN OPLOSSING

    @RegisterExtension
    static SeleniumExtension seleniumExtension = new SeleniumExtension();

    private String helloMessage = "Hello, %s!";

    private final String incorrectDetailsMsg =
            "Login Details Incorrect. Please try again.";

    private String emailValidationError =
            "Please include an '@' in the email address. " +
                    "'%s' is missing an '@'.";


    static {
        //System.setProperty("log4j.configurationFile", "log4j2.xml");   //tested first
        System.setProperty("log4j.configurationFile", "log4j2.properties");
        
    private static final Logger log = LogManager.getLogger(Test11DDTparamNoTestTemplate.class);

            /*
        use it like:
        log.trace("this is a trace level, lowest log level (600)");
        log.debug("This is a debug message (500)");
        log.info("This is an info message (400)");
        log.warn("This is a warn message (300)");
        log.error("This is an error message (200)");
        log.fatal("This is a fatal message (100)");
        */

    public void timeDelay(long t) {   //TODO put this in one of the utility classes
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {}
    }

    @BeforeAll
    static void setup() {
        //in case chrome and firefox are running in docker containers:
        //SeleniumJupiter.config().setVnc(true);      // you can interact with the browser!
        //SeleniumJupiter.config().setRecording(true);   // I expect two mp4 files in highest folder        //SeleniumJupiter.config().setRecordingVideoScreenSize("1280x972");

        //to stop geckodriver logging: (this is for firefox, not for firefoxInDocker)
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");
    }

    //TODO methodsource with factory method that must generate a stream of arguments:

    static Stream<Arguments> purposeEmailPasswordNameProvider() {
        return Stream.of(
                arguments("Login works", "alexsiminiuc3@gmail.com", "Password123!", "Alex"),
                arguments("Incorrect email", "alexsiminiuc2@gmail.com", "Password123!", "Alex"),
                arguments("Invalid email", "alexsiminiuc", "Password123!", "Alex")
        );
    }


    @ParameterizedTest
    @MethodSource("purposeEmailPasswordNameProvider")          // I could also use a @CsvSource or a @CsvFileSource
    void testArgumentAggregMethodSourceChrome(ArgumentsAccessor arguments,ChromeDriver driver) {
        String purpose = arguments.getString(0);
        System.out.println("purpose is: " + purpose);
        User user = new User(arguments.getString(1),
                arguments.getString(2),
                arguments.getString(3));

        testCASE(driver, user, purpose);


    }

    @ParameterizedTest
    @MethodSource("purposeEmailPasswordNameProvider")          // I could also use a @CsvSource or a @CsvFileSource
    void testArgumentAggregMethodSourceFirefox(ArgumentsAccessor arguments,FirefoxDriver driver) {
        String purpose = arguments.getString(0);
        System.out.println("purpose is: " + purpose);
        User user = new User(arguments.getString(1),
                arguments.getString(2),
                arguments.getString(3));

        testCASE(driver, user, purpose);

    }

    void testCASE(WebDriver driver, User user, String purpose){

        HomePage homePage = new HomePage(driver);
        homePage.open();

        if (purpose.equals("Login works")) {
            WelcomePage welcomePage = homePage.loginAndContinue(user);

            String expectedMsg = String.format(helloMessage,
                    user.name());

            log.info("does \""+purpose+ "\" work?");
            assertTrue(welcomePage.welcomeMessage().contains(expectedMsg),
                    "Hello message is not displayed!");
            //let it fail:
            //assertFalse(welcomePage.welcomeMessage().contains(expectedMsg),
            //        "Hello message is not displayed!");
            timeDelay(4000);
        }

        if (purpose.equals("Incorrect email")) {
            homePage.loginWith(user);

            log.info("does \""+purpose+ "\" work?");
            assertTrue(homePage.incorrectLoginMsg().equalsIgnoreCase(incorrectDetailsMsg),
                    "Incorrect Details Message is not displayed!");
            //let it fail:
            //assertFalse(homePage.incorrectLoginMsg().equalsIgnoreCase(incorrectDetailsMsg),
            //        "Incorrect Details Message is not displayed!");
            timeDelay(4000);
        }

        if (purpose.equals("Invalid email")) {
            homePage.loginWith(user);

            //System.out.println("driverclassname: "+driver.manage().getClass().getName());
            // output is driverclassname: org.openqa.selenium.remote.RemoteWebDriver$RemoteWebDriverOptions
            //TODO https://stackoverflow.com/questions/30238843/determine-type-of-selenium-driver-in-java
            String expectedError = String.format(emailValidationError,
                    user.email());
            if (driver instanceof FirefoxDriver){
                expectedError = "Please enter an email address.";
            }
            System.out.println("expectedError: "+expectedError);
            log.info("does \""+purpose+ "\" work?");
            assertEquals(expectedError, homePage.emailTooltip());
            //let it fail:
            //assertNotEquals(expectedError, homePage.emailTooltip());
            timeDelay(4000);
        }
    }
}

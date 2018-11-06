/*
derived from https://www.quora.com/How-do-I-write-Script-in-selenium-using-Java-for-a-login-module
*/

package phptravelsPageObjectsFactory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class WelcomePage {
    @FindBy(xpath = "//li[@id = 'Secondary_Navbar-Account']" +
                    "/a[@class='dropdown-toggle']")
    private WebElement helloMessage;

    private final WebDriver driver;

    public WelcomePage(WebDriver d) {
        this.driver = d;
        PageFactory.initElements(driver, this);
    }

    public String welcomeMessage() {
        return helloMessage.getText();
    }
}

/*
derived from https://www.quora.com/How-do-I-write-Script-in-selenium-using-Java-for-a-login-module


For example, these two annotations point to the same element:

 @FindBy(id = “userName”)
 WebElement txt_UserName;

 @FindBy(how = How.ID, using = “userName”) //needs import org.openqa.selenium.support.How;
 WebElement txt_UserName;
*/

package phptravelsPageObjectsFactory;

import phptravelsPageObjectsFactory.User;
import phptravelsPageObjectsFactory.WelcomePage;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
    private WebDriver driver;     //op toolsqa stond er final ipv private

    @FindBy(id = "inputEmail") private WebElement emailTextBox;
    @FindBy(id = "inputPassword") private WebElement passwordTextBox;
    @FindBy(id = "login") private WebElement loginButton;
    @FindBy(xpath = "//div[@class = 'alert alert-danger text-center']") private WebElement errorMessage;

    private final String HOME_URL = "https://phptravels.org/clientarea.php";
    private final String HOME_TITLE = "Client Area - PHPTRAVELS";


    public HomePage(WebDriver d) {
        this.driver = d;
        PageFactory.initElements(driver, this);
    }

    public void open() {
        driver.get(HOME_URL);
        if (driver.getTitle().equalsIgnoreCase(HOME_TITLE) == false)
            throw new RuntimeException(
                    "Home page is not displayed.");
    }

    public WelcomePage loginAndContinue(User user) {
        loginWith(user);
        return new WelcomePage(this.driver);
    }

    public void loginWith(User user) {
        emailTextBox.sendKeys(user.email());
        passwordTextBox.sendKeys(user.password());
        loginButton.click();
    }

    public String incorrectLoginMsg() {
        return errorMessage.getText().trim();
        //trim()  Returns a string whose value is this string, with any leading and trailing whitespace removed.
    }

    public String emailTooltip() {
        String toolTip = "function getTooltip() {"+
                "var tooltip=document.querySelector(\"input[id='inputEmail']\")"+
                ".validationMessage; return tooltip; }; "+
                "return getTooltip()";

    JavascriptExecutor jsExec = (JavascriptExecutor)this.driver;
		return (String)jsExec.executeScript(toolTip);
		// look here to learn a bit https://seleniumjava.com/2016/09/05/how-to-execute-javascript-code-in-selenium-webdriver/
        //https://stackoverflow.com/questions/18815749/how-to-inspect-jquery-ui-tooltip
    }

}

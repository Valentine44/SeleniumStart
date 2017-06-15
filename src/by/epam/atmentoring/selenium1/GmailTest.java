package by.epam.atmentoring.selenium1;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.*;

/**
 * class for Gmail critical path testing
 * @author Valiantsin_Ivashynka
 *
 */
public class GmailTest {
	
	final String SENDER = "iv.selenium.test@gmail.com";
	final String ADDRESSEE = "iv.selenium.test2@yopmail.com";
	final String PASSWORD = "$T123456";
	final String LETTER_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n\nKind regards,\nJohn Doe";
	String draftCreateTime = "";
	String sendTime = "";

	WebDriver driver;
	WebDriverWait wait;
	
	/**
	 * launch browser
	 */
	@BeforeClass
	public void setUp() {
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, 10);
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.MILLISECONDS);
	}
	/**
	 * open gmail.com login form
	 */
	@Test (priority = 0)
	public void openGmailTest() {
		driver.get("https://accounts.google.com/signin/v2/identifier");
		
		Assert.assertTrue(driver.getCurrentUrl().contains("https://accounts.google.com/signin/v2/identifier"));
	}
	/**
	 * log in to Gmail
	 */
	@Test (dependsOnMethods = { "openGmailTest" })
	public void logInTest() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='identifierId']")));
		driver.findElement(By.xpath("//input[@id='identifierId']")).sendKeys(SENDER);
		driver.findElement(By.xpath("//div[@id='identifierNext']/content/span")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='password']")));
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys(PASSWORD);
		driver.findElement(By.xpath("//div[@id='passwordNext']/content/span")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@aria-label='Mail']")));
		driver.findElement(By.xpath("//a[@aria-label='Mail']")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[title^='Google Account: ']")));
		Assert.assertEquals(driver.getTitle(), "Inbox - " + SENDER + " - Gmail");
	}
	/**
	 * create email
	 */
	@Test (dependsOnMethods = { "logInTest" }, alwaysRun = true)
	public void createEmailTest() {
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'COMPOSE')]")));
		driver.findElement(By.xpath("//div[contains(text(), 'COMPOSE')]")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@contenteditable='true']")));
		driver.findElement(By.xpath("//textarea[@aria-label='To']")).sendKeys(ADDRESSEE);
		driver.findElement(By.xpath("//textarea[@aria-label='To']")).sendKeys(" ");
		driver.findElement(By.xpath("//input[@placeholder='Subject']")).sendKeys("Test subject");
		driver.findElement(By.xpath("//div[@contenteditable='true']")).sendKeys(LETTER_TEXT);

		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//textarea[@aria-label='To']/preceding-sibling::div/span/div[1]")));
		Assert.assertEquals(driver.findElement(By.xpath("//form/div[2]/div/span")).getText(), ADDRESSEE);
		Assert.assertEquals(driver.findElement(By.xpath("//div[contains(text(), 'Compose:')]/following-sibling::div")).getText(), "Test subject");
		Assert.assertEquals(driver.findElement(By.xpath("//div[@aria-label='Message Body']")).getText(), LETTER_TEXT);
	}
	/**
	 * save created email in Drafts folder
	 */
	@Test (dependsOnMethods = { "createEmailTest" }, alwaysRun = true)
	public void saveAsDraftTest() {
		draftCreateTime = ActionTime.getTime();
		driver.findElement(By.xpath("//img[@data-tooltip='Save & Close']")).click();
		driver.findElement(By.partialLinkText("Drafts")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[1]/td[4]/div[2]/font[contains(text(), 'Draft')]")));
		Assert.assertTrue(driver.findElement(By.xpath("//tr[1]/td[4]/div[2]/font[contains(text(), 'Draft')]")).isDisplayed());
		Assert.assertEquals(driver.findElement(By.xpath("//*[contains(text(), \"" + draftCreateTime + "\")]")).getText(), sendTime);
	}
	/**
	 * open draft
	 */
	@Test (dependsOnMethods = { "saveAsDraftTest" }, alwaysRun = true)
	public void openDraftTest() {
		driver.findElement(By.partialLinkText("Drafts")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[1]/td[4]/div[2]/font[contains(text(), 'Draft')]")));
		Assert.assertTrue(driver.getTitle().contains("Drafts"));
		
		driver.findElement(By.xpath("//tr[1]/td[4]/div[2]/font[contains(text(), 'Draft')]")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form/div[2]/div/span")));
		Assert.assertEquals(driver.findElement(By.xpath("//form/div[2]/div/span")).getText(), ADDRESSEE);
		Assert.assertEquals(driver.findElement(By.xpath("//div[contains(text(), 'Compose:')]/following-sibling::div")).getText(), "Test subject");
		Assert.assertEquals(driver.findElement(By.xpath("//div[@aria-label='Message Body']")).getText(), LETTER_TEXT);
	}
	/**
	 * send email
	 */
	@Test (dependsOnMethods = { "openDraftTest" }, alwaysRun = true)
	public void sendEmailTest() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='button'][@aria-label='Send ‪(Ctrl-Enter)‬']")));
		//sendTime = (new SimpleDateFormat("hh:mm a").format(new Date()).toLowerCase()).replaceFirst("^0+(?!$)", "");
		sendTime = ActionTime.getTime();
		driver.findElement(By.xpath("//div[@role='button'][@aria-label='Send ‪(Ctrl-Enter)‬']")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Your message has been sent.')]")));
		driver.findElement(By.partialLinkText("Drafts")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), \"You don't have any saved drafts\")]")));
		Assert.assertEquals(driver.findElement(By.xpath("//*[contains(text(), \"You don't have any saved drafts\")]")).getText(), "You don't have any saved drafts.\nSaving a draft allows you to keep a message you aren't ready to send yet.");
		
		driver.findElement(By.linkText("Sent Mail")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), \"" + sendTime + "\")]")));
		Assert.assertTrue(driver.getTitle().contains("Sent Mail"));
		Assert.assertEquals(driver.findElement(By.xpath("//*[contains(text(), \"" + sendTime + "\")]")).getText(), sendTime);
	}
	/**
	 * log out from Gmail
	 */
	@Test (dependsOnMethods = { "sendEmailTest" }, alwaysRun = true)
	public void logOutTest() {
		driver.findElement(By.cssSelector("a[title^='Google Account: ']")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Sign out")));
		driver.findElement(By.linkText("Sign out")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='password']")));
		Assert.assertEquals(driver.getTitle(), "Gmail");
	}
	/**
	 * close browser
	 */
	@AfterClass
	public void tearDown() {
		driver.quit();
	}
}
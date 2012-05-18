package org.motechproject.whp.functional.page;

import org.motechproject.whp.functional.framework.MyPageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.util.List;

public class UpdateAdherencePage extends Page {

    @FindBy(how = How.ID, using = "weeklyAdherenceForm")
    private WebElement adherenceForm;

    private final String PAUSE_REASON_ID = "pauseReason";
    @FindBy(how = How.ID, using = PAUSE_REASON_ID)
    private WebElement pauseReason;

    @FindBy(how = How.CLASS_NAME, using = "adherenceRow")
    private List<WebElement> adherenceRows;

    public UpdateAdherencePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad("weeklyAdherenceForm");
    }

    public boolean isTaken(String dayOfWeek) {
        for (WebElement adherenceRow : adherenceRows) {
            String pillDay = adherenceRow.findElement(By.className("pillDay")).getAttribute("value");
            if (pillDay.equals(dayOfWeek)) {
                return adherenceRow.findElement(By.className("pillStatusTaken")).isSelected();
            }
        }
        return false;
    }

    public UpdateAdherencePage markAsTaken(String dayOfWeek) {
        for (WebElement adherenceRow : adherenceRows) {
            String pillDay = adherenceRow.findElement(By.className("pillDay")).getAttribute("value");
            if (pillDay.equals(dayOfWeek)) {
                adherenceRow.findElement(By.className("pillStatusTaken")).click();
                break;
            }
        }
        return this;
    }

    public boolean isReadOnly() {
        return !adherenceRows.get(0).findElement(By.className("pillStatusTaken")).isEnabled();
    }

    public boolean isTreatmentNotPausedDuringCurrentWeek() {
        return safeFindElement(By.id(PAUSE_REASON_ID)) == null;
    }

    public String getPauseReasonText() {
        return pauseReason.getText();
    }

    public ProviderPage submit() {
        adherenceForm.submit();
        return MyPageFactory.initElements(webDriver, ProviderPage.class);
    }

}

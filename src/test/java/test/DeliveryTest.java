package test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import data.DataGenerator;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class DeliveryTest {

    private final DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
    private final int daysToAdd = 4;
    private final int daysToAddSeven = 7;
    private final String firstMeetingDate = DataGenerator.generateDate(daysToAdd);
    private final String firstMeetingDateSeven = DataGenerator.generateDate(daysToAddSeven);

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
        Configuration.holdBrowserOpen = true;
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessful() {
        $("[data-test-id=\"city\"] input").setValue(validUser.getCity());
        $("[data-test-id=\"date\"] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=\"date\"] input").setValue(firstMeetingDate);
        $("[data-test-id=\"name\"] input").setValue(validUser.getName());
        $("[data-test-id=\"phone\"] input").setValue(validUser.getPhone());

        $("[data-test-id=\"agreement\"]").click();
        $(byText("Запланировать")).click();

        $("[data-test-id=\"success-notification\"]")
                .shouldHave(Condition.text("Успешно! Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(15));
    }


    @Test
    public void testPositiveReschedule() {
        //1 часть.
        $("[data-test-id=\"city\"] input").setValue(validUser.getCity());
        $("[data-test-id=\"date\"] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=\"date\"] input").setValue(firstMeetingDate);
        $("[data-test-id=\"name\"] input").setValue(validUser.getName());
        $("[data-test-id=\"phone\"] input").setValue(validUser.getPhone());

        $("[data-test-id=\"agreement\"]").click();
        $(byText("Запланировать")).click();

        $("[data-test-id=\"success-notification\"]")
                .shouldHave(Condition.text("Успешно! Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(15));
        //2 часть.
        $("[data-test-id=\"date\"] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=\"date\"] input").setValue(firstMeetingDateSeven);

        $(byText("Запланировать")).click();

        $("[data-test-id=\"replan-notification\"]").shouldHave(Condition.text("Необходимо подтверждение У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $(byText("Перепланировать")).click();

        $("[data-test-id=\"success-notification\"]").shouldHave(Condition.text("Успешно! Встреча успешно запланирована на " + firstMeetingDateSeven), Duration.ofSeconds(15));
    }
}

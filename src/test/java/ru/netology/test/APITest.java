package ru.netology.test;

import org.junit.jupiter.api.*;
import ru.netology.data.APIHelper;
import ru.netology.data.DataHelper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.netology.data.APIHelper.*;
import static ru.netology.data.SQLHelper.*;

public class APITest {

    @AfterEach
    void tearDown() {
        cleanAuthCodes();
    }

    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
    }

    @Test
    @DisplayName("Should successfully login with exist login and password")
    void shouldSuccessLogin() {
        APIHelper.validLogin();
    }

    @Test
    @DisplayName("Should obtain non-null authorization token")
    void shouldObtainNonNullAuthorizationToken() {
        String token = getToken();
        Assertions.assertNotNull(token);
    }

    @Test
    @DisplayName("Should fetch user's card information")
    void shouldFetchUsersCardInfo() {
        List<DataHelper.CardsInfo> userCards = APIHelper.getUserCardsInfo();
        Assertions.assertNotNull(userCards, "The list of cards must not be null.");
    }

    @Test
    @DisplayName("An attempt to log in with incorrect credentials returns AUTH_INVALID")
    void shouldReturnsAuthInvalidWithInvalidLogin() {
        String errorCode = invalidLogin();
        Assertions.assertEquals("AUTH_INVALID", errorCode);
    }

    @Test
    @DisplayName("Verification fails with AUTH_INVALID on invalid code")
    public void shouldNotVerifyWithoutToken() {
        String errorVerify = invalidVerify();
        Assertions.assertEquals("AUTH_INVALID", errorVerify);
    }

    @Test
    @DisplayName("Should not get user cards info without token")
    public void shouldNotGetUserCardsInfoWithoutToken() {
        try {
            APIHelper.getUserCardsInfoWithoutToken();
            assertTrue(true, "Method completed successfully with expected status code 401");
        } catch (AssertionError | Exception e) {
            fail("Test failed due to unexpected error: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Successful transfer from first card to second card")
    public void shouldSuccessTransferFromFirstToSecondCards() {
        List<DataHelper.CardsInfo> userCardsInfo = getUserCards();
        int initialFirstCardBalance = userCardsInfo.get(0).getBalance();
        int initialSecondCardBalance = userCardsInfo.get(1).getBalance();
        int amount = DataHelper.generateValidAmount(initialSecondCardBalance);
        int expectedFirstCardBalance = initialFirstCardBalance - amount;
        int expectedSecondCardBalance = initialSecondCardBalance + amount;

        APIHelper.moneyTransfer(userCardsInfo, 0, 1, amount);
        List<DataHelper.CardsInfo> newUserCardsInfo = getUserCards();

        Assertions.assertEquals(expectedFirstCardBalance, newUserCardsInfo.get(0).getBalance());
        Assertions.assertEquals(expectedSecondCardBalance, newUserCardsInfo.get(1).getBalance());
    }

//    @Test
//    @DisplayName("Money transfer fails when requested amount exceeds account balance")
//    public void shouldNotTransferWhenAmountMoreThanBalance() {
//        List<DataHelper.CardsInfo> userCardsInfo = getUserCards();
//        int initialFirstCardBalance = userCardsInfo.get(0).getBalance();
//        int initialSecondCardBalance = userCardsInfo.get(1).getBalance();
//        int amount = DataHelper.generateInvalidAmount(initialFirstCardBalance);
//
//        APIHelper.moneyTransfer(userCardsInfo, 0, 1, amount);
//        List<DataHelper.CardsInfo> newCardsInfo = getUserCards();
//
//        Assertions.assertEquals(initialFirstCardBalance, newCardsInfo.get(0).getBalance());
//        Assertions.assertEquals(initialSecondCardBalance, newCardsInfo.get(1).getBalance());
//    }
}
package ru.netology.test;

import org.junit.jupiter.api.*;
import ru.netology.data.APIHelper;
import ru.netology.data.DataHelper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

//    @Test
//    @DisplayName("Should successfully login with exist login and password")
//    void shouldSuccessLogin() {
//        String errorCode = login(DataHelper.getAuthInfoWithTestData(), 200);
//        assertEquals("", errorCode);
//    }

    @Test
    @DisplayName("An attempt to log in with incorrect credentials returns AUTH_INVALID")
    void shouldReturnsAuthInvalidWithInvalidLogin() {
        String errorCode = login(DataHelper.generateRandomUser(), 400);

        assertEquals("AUTH_INVALID", errorCode);
    }

//    @Test
//    @DisplayName("Should obtain non-null authorization token")
//    void shouldObtainNonNullAuthorizationToken() {
//        int statusCode = 200;
//        login(DataHelper.getAuthInfoWithTestData(), statusCode);
//        String token = getToken(DataHelper.getVerifyInfo(), statusCode);
//        Assertions.assertNotNull(token);
//    }

    @Test
    @DisplayName("Verification fails with AUTH_INVALID on invalid code")
    public void shouldNotVerifyWithoutToken() {
        login(DataHelper.getAuthInfoWithTestData(), 200);
        String errorVerify = getToken(DataHelper.getInvalidVerifyInfo(), 400);

        Assertions.assertEquals("AUTH_INVALID", errorVerify);
    }

//    @Test
//    @DisplayName("Should fetch user's card information")
//    void shouldFetchUsersCardInfo() {
//        int statusCode = 200;
//        login(DataHelper.getAuthInfoWithTestData(), statusCode);
//        String token = getToken(DataHelper.getVerifyInfo(), statusCode);
//        List<DataHelper.CardsInfo> userCards = APIHelper.getUserCards(token, statusCode);
//        Assertions.assertNotNull(userCards, "The list of cards must not be null.");
//    }

    @Test
    @DisplayName("Should not get user cards info without token")
    public void shouldNotGetUserCardsInfoWithoutToken() {
        String token = null;
        int statusCode = 401;

        List<DataHelper.CardsInfo> result = APIHelper.getUserCards(token, statusCode);

        assertTrue(result.isEmpty(), "The result should be an empty list when status code is 401");
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
        int statusCode = 200;
        login(DataHelper.getAuthInfoWithTestData(), statusCode);
        String token = getToken(DataHelper.getVerifyInfo(), statusCode);

        APIHelper.moneyTransfer(token, userCardsInfo, 0, 1, amount);
        List<DataHelper.CardsInfo> newUserCardsInfo = getUserCards();

        assertAll(
                () -> assertEquals(expectedFirstCardBalance, newUserCardsInfo.get(0).getBalance()),
                () -> assertEquals(expectedSecondCardBalance, newUserCardsInfo.get(1).getBalance())
        );
    }

    @Test
    @DisplayName("Money transfer fails when requested amount exceeds account balance")
    public void shouldNotTransferWhenAmountMoreThanBalance() {
        List<DataHelper.CardsInfo> userCardsInfo = getUserCards();
        int initialFirstCardBalance = userCardsInfo.get(0).getBalance();
        int initialSecondCardBalance = userCardsInfo.get(1).getBalance();
        int amount = DataHelper.generateInvalidAmount(initialFirstCardBalance);
        int statusCode = 200;
        login(DataHelper.getAuthInfoWithTestData(), statusCode);
        String token = getToken(DataHelper.getVerifyInfo(), statusCode);

        APIHelper.moneyTransfer(token, userCardsInfo, 0, 1, amount);
        List<DataHelper.CardsInfo> newCardsInfo = getUserCards();

        assertAll(
                () -> assertEquals(initialFirstCardBalance, newCardsInfo.get(0).getBalance()),
                () -> assertEquals(initialSecondCardBalance, newCardsInfo.get(1).getBalance())
        );
    }
}
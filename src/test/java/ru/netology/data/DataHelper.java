package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.Locale;

public class DataHelper {
    private static final Faker FAKER = new Faker(new Locale("en"));

    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    @Value
    public static class VerificationInfo {
        String login;
        String code;
    }

    @Data
    @NoArgsConstructor
    public static class CardsInfo {
        private String id;
        private String number;
        private int balance;
    }

    @Value
    public static class TransferData {
        String from;
        String to;
        int amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationCode {
        String code;
    }

    public static AuthInfo getAuthInfoWithTestData() {
        return new AuthInfo("vasya", "qwerty123");
    }

    private static String generateRandomLogin() {
        return FAKER.name().username();
    }

    private static String generateRandomPassword() {
        return FAKER.internet().password();
    }

    public static AuthInfo generateRandomUser() {
        return new AuthInfo(generateRandomLogin(), generateRandomPassword());
    }

    public static VerificationCode generateRandomVerificationCode() {
        return new VerificationCode(FAKER.numerify("######"));
    }

    public static VerificationInfo getVerifyInfo() {
        return new VerificationInfo(getAuthInfoWithTestData().getLogin(), SQLHelper.getVerificationCode());
    }

    public static VerificationInfo getInvalidVerifyInfo() {
        return new VerificationInfo(getAuthInfoWithTestData().getLogin(), generateRandomVerificationCode().getCode());
    }

    public static TransferData moneyTransfer(List<CardsInfo> cards, int indexFrom, int indexTo, int amount) {
        String cardFrom = cards.get(indexFrom).getNumber();
        String cardTo = cards.get(indexTo).getNumber();
        return new TransferData(cardFrom, cardTo, amount);
    }

    public static int generateValidAmount(int balance) {
        return Math.abs(balance) / 10;
    }

    public static int generateInvalidAmount(int balance) {
        return Math.abs(balance) + 1;
    }
}
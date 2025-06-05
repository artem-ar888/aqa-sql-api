package ru.netology.data;

import io.restassured.http.ContentType;
import ru.netology.api.Specifications;

import java.util.List;

import static io.restassured.RestAssured.given;

public class APIHelper {
    private static final String authPath = "api/auth";
    private static final String verificationPath = "api/auth/verification";
    private static final String cardsPath = "api/cards";
    private static final String transferPath = "api/transfer";

    private APIHelper() {
    }

    public static void validLogin() {
        given()
                .spec(Specifications.requestSpec())
                .body(DataHelper.getAuthInfoWithTestData())

                .when()
                .post(authPath)

                .then()
                .log().all()
                .statusCode(200)
                .header("Connection", "keep-alive")
                .header("Content-Length", "0")
        ;
    }

    public static String invalidLogin() {
        return given()
                .spec(Specifications.requestSpec())
                .body(DataHelper.generateRandomUser())

                .when()
                .log().ifValidationFails() // Выводит логи только в случае ошибки
                .post(authPath)

                .then()
                .statusCode(400)
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Connection", "keep-alive")
                .extract().path("code")
                ;
    }

    public static String getToken() {
        validLogin();
        return given()
                .spec(Specifications.requestSpec())
                .body(DataHelper.getVerifyInfo())

                .when()
                .log().ifValidationFails()
                .post(verificationPath)

                .then()
                .statusCode(200)
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Connection", "keep-alive")
                .extract().path("token")
                ;
    }

    public static String invalidVerify() {
        validLogin();
        return given()
                .spec(Specifications.requestSpec())
                .body(DataHelper.getInvalidVerifyInfo())

                .when()
                .log().ifValidationFails()
                .post(verificationPath)

                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .extract().path("code")
                ;
    }

    public static List<DataHelper.CardsInfo> getUserCardsInfo() {
        return given()
                .log().all()
                .spec(Specifications.requestSpec())
                .auth().oauth2(getToken())

                .when()
                .get(cardsPath)

                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath().getList(".", DataHelper.CardsInfo.class)
                ;
    }

    public static void getUserCardsInfoWithoutToken() {
        given()
                .log().all()
                .spec(Specifications.requestSpec())

                .when()
                .get(cardsPath)

                .then()
                .statusCode(401)
        ;
    }

    public static void moneyTransfer(List<DataHelper.CardsInfo> cards, int indexFrom, int indexTo, int amount) {
        given()
                .log().all()
                .spec(Specifications.requestSpec())
                .auth().oauth2(getToken())
                .body(DataHelper.moneyTransfer(cards, indexFrom, indexTo, amount))

                .when()
                .post(transferPath)

                .then()
                .statusCode(200)
        ;
    }
}
package ru.netology.data;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.netology.api.Specifications;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;

public class APIHelper {
    private static final String authPath = "api/auth";
    private static final String verificationPath = "api/auth/verification";
    private static final String cardsPath = "api/cards";
    private static final String transferPath = "api/transfer";

    private APIHelper() {
    }

    public static String login(DataHelper.AuthInfo user, int statusCode) {
        Response response = given()
                .spec(Specifications.requestSpec())
                .body(user)

                .when()
                .post(authPath)

                .then()
                .log().all()
                .statusCode(statusCode)
                .header("Connection", "keep-alive")
                .extract().response();

        if (response.asString().isBlank()) {
            return ""; // возвращаем пустую строку
        } else {
            return response.path("code").toString(); // возвращаем значение из ответа
        }
    }

    public static String getToken(DataHelper.VerificationInfo verifyInfo, int statusCode) {
        Response response = given()
                .spec(Specifications.requestSpec())
                .body(verifyInfo)

                .when()
                .log().ifValidationFails()
                .post(verificationPath)

                .then()
                .statusCode(statusCode)
                .contentType(ContentType.JSON)
                .extract().response();

        if (statusCode == 200) {
            return response.path("token").toString();
        }
        return response.path("code").toString();
    }

    public static List<DataHelper.CardsInfo> getUserCards(String token, int statusCode) {
        RequestSpecification spec = Specifications.requestSpec();

        if (token != null) {
            spec.auth().oauth2(token); // Применяем авторизацию только если токен есть
        }

        Response response = given()
                .log().all()
                .spec(spec)

                .when()
                .get(cardsPath)

                .then()
                .statusCode(statusCode)
                .extract().response();
        if (statusCode == 200) {
            return response.body().jsonPath().getList(".", DataHelper.CardsInfo.class);
        }
        if (!response.asString().isBlank()) {
            throw new IllegalStateException("Response body is not empty for status " + statusCode);
        }
        return Collections.emptyList(); // Если статус не 200, возвращаем пустой список
    }

    public static void moneyTransfer(String token, List<DataHelper.CardsInfo> cards, int indexFrom, int indexTo, int amount) {
        given()
                .log().all()
                .spec(Specifications.requestSpec())
                .auth().oauth2(token)
                .body(DataHelper.moneyTransfer(cards, indexFrom, indexTo, amount))

                .when()
                .post(transferPath)

                .then()
                .statusCode(200)
        ;
    }
}
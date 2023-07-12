import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.LocalDate;


public class APIMethods {

    private final int STATUS_CODE_200 = 200;
    private final int STATUS_CODE_201 = 201;
    private final String BOOKING = "/booking";
    private final String BOOKING_ID = "/booking/{id}";
    public static String TOKEN_VALUE;
    public static final String TOKEN = "token";

    @BeforeMethod
    public void setUp() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        CreateTokenBody body = new CreateTokenBody("admin", "password123");
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .build();

        Response response = RestAssured.given()
                .body(body)
                .post("/auth");
        TOKEN_VALUE = response.then().extract().jsonPath().get(TOKEN);
    }

    @Test
    public void createBookingTest() {
        BookingDates bookingDates = new BookingDates("2022-02-02", "2031-01-01");

        String firstNameExpected = "Natalie";
        String lastNameExpected = "Soloviova";
        Integer totalPriceExpected = 6000;
        Boolean depositpaidExpected = false;
        String additionalneedsExpected = "none";

        CreateBookingBody body = new CreateBookingBody().builder()
                .firstname(firstNameExpected)
                .lastname(lastNameExpected)
                .totalprice(totalPriceExpected)
                .depositpaid(depositpaidExpected)
                .bookingdates(bookingDates)
                .additionalneeds(additionalneedsExpected)
                .build();

        Response book = RestAssured.given()
                .body(body)
                .post(BOOKING);
        book.prettyPrint();
        book.then().statusCode(STATUS_CODE_200);
        book.as(ResponseBooking.class);
        String firstNameActual = book.as(ResponseBooking.class).getBooking().getFirstname();
        String lastNameActual = book.as(ResponseBooking.class).getBooking().getLastname();
        Integer totalpriceActual = book.as(ResponseBooking.class).getBooking().getTotalprice();
        Boolean depositpaidActual = book.as(ResponseBooking.class).getBooking().getDepositpaid();
        SoftAssert softAssertion= new SoftAssert();
        softAssertion.assertEquals(firstNameActual, firstNameExpected, "firstName is wrong");
        softAssertion.assertEquals(lastNameActual, lastNameExpected, "lastName is wrong");
        softAssertion.assertEquals(totalpriceActual, totalPriceExpected, "firstName is wrong");
        softAssertion.assertEquals(depositpaidExpected, depositpaidActual, "depositpaid is wrong");
    }

    private Integer findFirstBooking() {
        Response getBookings = RestAssured.get(BOOKING);
        return getBookings.then().extract().jsonPath().get("bookingid[0]");
    }

    private Integer findSecondBooking() {
        Response getBookings = RestAssured.get(BOOKING);
        return getBookings.then().extract().jsonPath().get("bookingid[1]");
    }

    @Test
    public void getAllBooking() {
        Response response = RestAssured.given().log().all().get(BOOKING);
        response.then().statusCode(STATUS_CODE_200);
    }

    @Test
    public void getBookingById() {
        Integer pathId = findFirstBooking();
        Response bookingId = RestAssured.given().log().all().get(BOOKING_ID, findFirstBooking());
        bookingId.prettyPrint();
        bookingId.then().statusCode(STATUS_CODE_200);
        Assert.assertEquals(bookingId.jsonPath().get("firstname"), "firstname", "Firstname is wrong");
    }

    @Test
    public void updatedTotalPrice() {
        Integer pathId = findFirstBooking();
        CreateBookingBody body = new CreateBookingBody();
        body.setTotalprice(2000);
        Response response = RestAssured.given()
                .header("Accept", "application/json")
                .contentType(ContentType.JSON)
                .cookie(TOKEN, TOKEN_VALUE)
                .body(body)
                .patch(BOOKING_ID, pathId);
        response.then().statusCode(STATUS_CODE_200);
        response.prettyPrint();
    }

    @Test
    public void deleteBookingTest() {
        Integer pathId = findSecondBooking();
        Response deleteBooking = RestAssured.given()
                .cookie(TOKEN, TOKEN_VALUE)
                .delete(BOOKING_ID, pathId);
        deleteBooking.prettyPrint();
        deleteBooking.then().statusCode(STATUS_CODE_201);
    }

    @Test
    public void updateNameLastnameAdditionalNeedsBooking() {
        Integer pathId = findFirstBooking();
        CreateBookingBody bookingId = RestAssured
                .given()
                .log()
                .all()
                .get(BOOKING_ID, pathId)
                .as(CreateBookingBody.class);

        CreateBookingBody body = new CreateBookingBody().builder()
                .firstname("firstname")
                .lastname("lastname")
                .totalprice(bookingId.getTotalprice())
                .depositpaid(bookingId.getDepositpaid())
                .bookingdates(new BookingDates(bookingId.getBookingdates().getCheckin(), bookingId.getBookingdates().getCheckout()))
                .additionalneeds("additionalneeds")
                .build();

        Response updatedBooking = RestAssured.given()
                .header("Accept", "application/json")
                .contentType(ContentType.JSON)
                .cookie(TOKEN, TOKEN_VALUE)
                .body(body)
                .put(BOOKING_ID, pathId);
        updatedBooking.prettyPrint();
        updatedBooking.then().statusCode(200);
        String firstNameExpected = updatedBooking.as(CreateBookingBody.class).getFirstname();
        String additionalneedsExpected = updatedBooking.as(CreateBookingBody.class).getAdditionalneeds();
        SoftAssert softAssertion= new SoftAssert();
        softAssertion.assertEquals("firstname", firstNameExpected, "firstname is wrong");
        softAssertion.assertEquals("additionalneeds", additionalneedsExpected, "additionalneeds is wrong");
    }
}

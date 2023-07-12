import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class APIMethods {

    private final int STATUS_CODE_200 = 200;
    private final int STATUS_CODE_201 = 201;
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
                .post("/booking");
        book.prettyPrint();
        book.then().statusCode(STATUS_CODE_200);
        book.as(ResponseBooking.class);
        String firstNameActual = book.as(ResponseBooking.class).getBooking().getFirstname();
        String lastNameActual = book.as(ResponseBooking.class).getBooking().getLastname();
        Integer totalpriceActual = book.as(ResponseBooking.class).getBooking().getTotalprice();
        Boolean depositpaidActual = book.as(ResponseBooking.class).getBooking().getDepositpaid();
        Assert.assertEquals(firstNameActual, firstNameExpected, "firstName is wrong");
        Assert.assertEquals(lastNameActual, lastNameExpected, "lastName is wrong");
        Assert.assertEquals(totalpriceActual, totalPriceExpected, "firstName is wrong");
        Assert.assertEquals(depositpaidExpected, depositpaidActual, "depositpaid is wrong");
    }
    private Integer findFirstBooking() {
        Response getBookings = RestAssured.get("/booking");
        return getBookings.then().extract().jsonPath().get("bookingid[0]");
    }
    private Integer findSecondBooking() {
        Response getBookings = RestAssured.get("/booking");
        return getBookings.then().extract().jsonPath().get("bookingid[1]");
    }
    @Test
    public void getAllBooking() {
        Response response = RestAssured.given().log().all().get("/booking");
        response.then().statusCode(STATUS_CODE_200);
    }

    @Test
    public void getBookingById() {
        Response bookingId = RestAssured.given().log().all().get("/booking/{id}", 1);
        bookingId.prettyPrint();
        bookingId.then().statusCode(STATUS_CODE_200);
        Assert.assertEquals(bookingId.jsonPath().get("firstname"), "Mark", "Firstname is wrong");
    }

    @Test
    public void updatedTotalPrice() {
        CreateBookingBody body = new CreateBookingBody();
        body.setTotalprice(2000);
        Response response = RestAssured.given()
                .header("Accept", "application/json")
                .contentType(ContentType.JSON)
                .cookie(TOKEN, TOKEN_VALUE)
                .body(body)
                .patch("/booking/{id}", 1);
        response.then().statusCode(STATUS_CODE_200);
        response.prettyPrint();
    }

    @Test
    public void deleteBookingTest() {
        Integer pathId = findSecondBooking();
        Response deleteBooking = RestAssured.given()
                .cookie(TOKEN, TOKEN_VALUE)
                .delete("/booking/{id}", pathId);
        deleteBooking.prettyPrint();
        deleteBooking.then().statusCode(STATUS_CODE_201);
    }

    @Test
    public void updateNameLastnameAdditionalNeedsBooking() {
        Integer pathId = findFirstBooking();
        Response bookingId = RestAssured.given().log().all().get("/booking/{id}", pathId);
        CreateBookingBody body = new CreateBookingBody().builder()
                .firstname("firstname")
                .lastname("lastname")
                .totalprice(bookingId.jsonPath().get("totalprice"))
                .depositpaid(bookingId.jsonPath().get("depositpaid"))
                .bookingdates(new BookingDates(bookingId.jsonPath().get("bookingdates.checkin").toString(), bookingId.jsonPath().get("bookingdates.checkout").toString()))
                .additionalneeds("additionalneeds")
                .build();

        Response updatedBooking = RestAssured.given()
                .header("Accept", "application/json")
                .contentType(ContentType.JSON)
                .cookie(TOKEN, TOKEN_VALUE)
                .body(body)
                .put("/booking/{id}", pathId);
        updatedBooking.prettyPrint();
        updatedBooking.then().statusCode(200);
        String firstNameExpected = updatedBooking.as(CreateBookingBody.class).getFirstname();
        String additionalneedsExpected=updatedBooking.as(CreateBookingBody.class).getAdditionalneeds();
        Assert.assertEquals("firstname", firstNameExpected,"firstname is wrong");
        Assert.assertEquals("additionalneeds", additionalneedsExpected,"additionalneeds is wrong");
    }
}

//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//
//public class APITests {
//
//
//    @Test
//    public void createBookingTest() {
//        String firstnameExpected = "Natali";
//        String lastnameExpected = "Soloviova";
//        Integer totalpriceExpected = 400;
//        Boolean depositPaidExpected = true;
//        String additionalneedsExpected = "nothing";
//        APIMethods apiMethods=new APIMethods();
//        apiMethods.setUp();
//        Response book = apiMethods.createBooking("Natali", "Soloviova",
//                400, true, "nothing", "2022-02-02", "2031-01-01");
//        book.as(ResponseBooking.class);
//
//        String firstnameActual = book.as(ResponseBooking.class).getBooking().getFirstname();
//        String lastnameActual = book.as(ResponseBooking.class).getBooking().getLastname();
//        Integer totalPriceActual = book.as(ResponseBooking.class).getBooking().getTotalprice();
//        Boolean depositPaidActual = book.as(ResponseBooking.class).getBooking().getDepositpaid();
//        String additionalneedsActual = book.as(ResponseBooking.class).getBooking().getAdditionalneeds();
//
//        Assert.assertEquals(firstnameActual, firstnameExpected, "The first name is wrong");
//        Assert.assertEquals(lastnameActual, lastnameExpected, "The last name is wrong");
//        Assert.assertEquals(totalPriceActual, totalpriceExpected, "The price is wrong");
//        Assert.assertEquals(depositPaidActual, depositPaidExpected, "The Deposit paid is wrong");
//        Assert.assertEquals(additionalneedsActual, additionalneedsExpected, "The additional needs is wrong");
//    }
//}

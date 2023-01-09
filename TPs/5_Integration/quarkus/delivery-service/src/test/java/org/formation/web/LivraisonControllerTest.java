package org.formation.web;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.net.URL;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class LivraisonControllerTest {

	@TestHTTPEndpoint(LivraisonController.class)
	@TestHTTPResource("")
	URL findAllUrl;
	
	@Test
    public void testLivraisonsEndpoint() {
        given()
          .when().get(findAllUrl.getPath())
          .then()
             .statusCode(200)
        .body("$.size()", is(4),
                "[0].id", is(1),
                "[0].noCommande", is("1"),
                "[1].noCommande", is("2"));
        }
}

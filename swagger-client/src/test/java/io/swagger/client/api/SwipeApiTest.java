/*
 * twinder
 * CS6650 assignment API
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.api;

import com.google.gson.Gson;
import io.swagger.client.ApiClient;
import io.swagger.client.model.ResponseMsg;
import io.swagger.client.model.SwipeDetails;
import org.junit.Test;
import org.junit.Ignore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * API tests for SwipeApi
 */
@Ignore
public class SwipeApiTest {

    /**
     * 
     *
     * Swipe left or right
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void swipeTest() throws Exception {
        ApiClient apiClient = new ApiClient();
//        apiClient.setBasePath("http://35.93.136.62:8080/TwinderProject_archive");
        apiClient.setBasePath("http://localhost:8081/TwinderProject_war_exploded/");
        SwipeApi api = new SwipeApi(apiClient);

        SwipeDetails body = new SwipeDetails();
        body.setSwipee("123");
        body.setSwiper("123");
        body.setComment("you loser");
        String leftorright = "left";
        api.swipe(new SwipeDetails(), leftorright);
        // TODO: test validations
    }
}

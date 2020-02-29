package com.akvelon.cdp;

import com.akvelon.cdp.clients.RequestServiceClient;
import lombok.val;

public class ClientApp {

    public static void main(String[] args) {
        RequestServiceClient requestServiceClient = new RequestServiceClient();
        final String url = "false";
        val response = requestServiceClient.redirectToSpecifiedUrlUpdateStatistic(url);
    }
}

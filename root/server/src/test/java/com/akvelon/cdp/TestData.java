package com.akvelon.cdp;

import com.akvelon.cdp.entities.Request;

public class TestData {

    public static final String EMPTY_STRING = "";
    public static final long DEFAULT_ID = 1L;
    public static final Request REQUEST_ENTITY = Request.builder()
                                                        .requestUrl("google.com")
                                                        .id(DEFAULT_ID)
                                                        .build();
}

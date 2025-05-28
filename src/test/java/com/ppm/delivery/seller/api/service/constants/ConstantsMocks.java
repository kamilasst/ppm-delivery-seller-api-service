package com.ppm.delivery.seller.api.service.constants;

public class ConstantsMocks {

    public static final String COUNTRY_CODE_BR = "BR";
    public static final String COUNTRY_CODE_AR = "AR";
    public static final String COUNTRY_CODE_US = "US";

    public static final String TIME_00h00m00 = "00:00:00";
    public static final String TIME_08h00m00 = "08:00:00";
    public static final String TIME_08h00 = "08:00";
    public static final String TIME_08h30m00 = "08:30:00";
    public static final String TIME_09h00m00 = "09:00:00";
    public static final String TIME_10h00m00 = "10:00:00";
    public static final String TIME_17h30m00 = "17:30:00";
    public static final String TIME_18h00m00 = "18:00:00";
    public static final String TIME_23h59m00 = "23:59:00";
    public static final String TIME_23h59m59 = "23:59:59";
    public static final String TIME_24h00m00 = "24:00:00";
    public static final String TIME_INVALID = "invalid";

    public static final String ADMIN_PROFILE = "ADMIN";

    public static final String URI_TEMPLATE_PATCH_UPDATE = "/api/seller/patch/{code}";
    public static final String URI_TEMPLATE_POST_CREATE = "/api/seller/create";

    public static final String JSON_PATH_ERROR = "$.error";
    public static final String JSON_INVALID_PROFILE = "INVALID_PROFILE";
    public static final String JSON_INVALID_REQUEST = """
                    {
                        "businessHours": [
                            {
                                "dayOfWeek": "abcde",
                                "openAt": "08:00:00",
                                "closeAt": "18:00:00"
                            }
                        ]
                    }
                """;

    public static final String JSON_EMPTY_REQUEST ="""
                    {
                        "businessHours": [
                            {
                                "dayOfWeek": " ",
                                "openAt": "08:00:00",
                                "closeAt": "18:00:00"
                            }
                        ]
                    }
                """;
}

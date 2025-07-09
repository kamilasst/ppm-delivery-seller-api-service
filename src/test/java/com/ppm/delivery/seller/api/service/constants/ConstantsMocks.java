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
    public static final String TIME_12h00m00 = "12:00:00";
    public static final String TIME_17h30m00 = "17:30:00";
    public static final String TIME_18h00m00 = "18:00:00";
    public static final String TIME_23h59m00 = "23:59:00";
    public static final String TIME_23h59m59 = "23:59:59";
    public static final String TIME_24h00m00 = "24:00:00";
    public static final String TIME_INVALID = "invalid";

    public static final String ADMIN_PROFILE = "ADMIN";

    public static final String URI_TEMPLATE_PATCH_UPDATE = "/api/seller/patch/{code}";
    public static final String URI_TEMPLATE_POST_CREATE = "/api/seller/create";
    public static final String URI_TEMPLATE_POST_AVAILABLE = "/api/seller/search-nearby";

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

    public static final double LATITUDE_RECIFE_BOAVISTA_1 = -8.0578;
    public static final double LONGITUDE_RECIFE_BOAVISTA_1 = -34.8824;
    public static final double LATITUDE_RECIFE_BOAVISTA_2 = -8.0570;
    public static final double LONGITUDE_RECIFE_BOAVISTA_2 = -34.8820;
    public static final double LATITUDE_RECIFE_BOAVISTA_3 = -8.0560;
    public static final double LONGITUDE_RECIFE_BOAVISTA_3 = -34.8810;
    public static final double LATITUDE_RECIFE_BOAVISTA_4 = -8.0575;
    public static final double LONGITUDE_RECIFE_BOAVISTA_4 = -34.8825;

    public static final double LATITUDE_RECIFE_BOAVIAGEM_1 = -8.13211;
    public static final double LONGITUDE_RECIFE_BOAVIAGEM_1 = -34.90104;

    public static final double LATITUDE_SAO_PAULO = -23.5489 ;
    public static final double LONGITUDE_SAO_PAULO = -46.6389 ;

    public static final double RAIO_2KM = 2000.0;



}

package com.ppm.delivery.seller.api.service.builder;

public class SellerInvalidJsonSamples {

    private SellerInvalidJsonSamples() {}

    public static String businessHoursMissingValue() {
        return """
            {
                "identification": {
                    "type": "CNPJ",
                    "code": "12345678901435"
                },
                "name": "Bar do Cuscuz LTDA",
                "displayName": "Bar do Cuscuz",
                "contacts": [{
                    "type": "MAIL",
                    "value": "+pTryOZ7hrzjbfz4OuXQ4g=="
                }],
                "address": {
                    "location": {
                        "geoCoordinates": {
                            "latitude": "-23.520930238344484",
                            "longitude": "-46.905673295259476"
                        },
                        "city": "-46.905673295259476",
                        "country": "BR",
                        "state": "São Paulo",
                        "number": "2161",
                        "zipCode": "51021-200",
                        "streetAddress": "Avenida Henrique Gonçalves Baptista"
                    }
                },
                "creatorId": "d41f2c7b-c04e-4c2a-b084-8bec13261637",
                "businessHours":
            }
        """;
    }
}

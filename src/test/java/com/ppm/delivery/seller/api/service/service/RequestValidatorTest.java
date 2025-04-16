package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.config.SellerConfig;
import com.ppm.delivery.seller.api.service.api.domain.request.header.Header;
import com.ppm.delivery.seller.api.service.api.domain.request.header.Metadata;
import com.ppm.delivery.seller.api.service.api.interceptor.RequestValidator;
import com.ppm.delivery.seller.api.service.exception.CountryNotSupportedException;
import com.ppm.delivery.seller.api.service.exception.ProfileNotSupportedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestValidatorTest {

    @InjectMocks
    private RequestValidator requestValidator;

    @Mock
    private SellerConfig sellerConfig;

    @Test
    void shouldThrowExceptionWhenCountryIsBlank() {

        Metadata metadata = mock(Metadata.class);
        when(metadata.country()).thenReturn("  "); // país em branco

        Header header = mock(Header.class);
        when(header.metadata()).thenReturn(metadata);

        assertThrows(CountryNotSupportedException.class, () -> requestValidator.validateHeader(header));
    }

    @Test
    void shouldThrowExceptionWhenCountryIsNotSupported() {

        Metadata metadata = mock(Metadata.class);
        when(metadata.country()).thenReturn("AR"); // país em branco

        Header header = mock(Header.class);
        when(header.metadata()).thenReturn(metadata);

        when(sellerConfig.getSupportedCountries()).thenReturn(List.of("BR", "US"));

        assertThrows(CountryNotSupportedException.class, () -> requestValidator.validateHeader(header));
    }

    @Test
    void shouldThrowExceptionWhenProfileIsBlank() {

        Metadata metadata = Mockito.mock(Metadata.class);
        when(metadata.country()).thenReturn("BR");
        when(metadata.profile()).thenReturn("  ");

        Header header = Mockito.mock(Header.class);
        when(header.metadata()).thenReturn(metadata);
        when(sellerConfig.getSupportedCountries()).thenReturn(List.of("BR"));

        assertThrows(ProfileNotSupportedException.class, () -> requestValidator.validateHeader(header));
    }

    @Test
    void shouldThrowExceptionWhenProfileIsInvalid() {

        Metadata metadata = Mockito.mock(Metadata.class);
        when(metadata.country()).thenReturn("BR");
        when(metadata.profile()).thenReturn("INVALID_PROFILE");

        Header header = Mockito.mock(Header.class);
        when(header.metadata()).thenReturn(metadata);
        when(sellerConfig.getSupportedCountries()).thenReturn(List.of("BR"));

        assertThrows(ProfileNotSupportedException.class, () -> requestValidator.validateHeader(header));
    }

    @Test
    void shouldNotThrowExceptionWhenHeaderIsValid() {

        Metadata metadata = Mockito.mock(Metadata.class);
        when(metadata.country()).thenReturn("BR");
        when(metadata.profile()).thenReturn("ADMIN");

        Header header = Mockito.mock(Header.class);
        when(header.metadata()).thenReturn(metadata);
        when(sellerConfig.getSupportedCountries()).thenReturn(List.of("BR"));

        assertDoesNotThrow(() -> requestValidator.validateHeader(header));
    }

}

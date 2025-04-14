package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.interceptor.ContextHolder;
import com.ppm.delivery.seller.api.service.domain.profile.Profile;
import com.ppm.delivery.seller.api.service.exception.BusinessException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {

    @InjectMocks
    private PermissionService permissionService;
    @Mock
    private ContextHolder contextHolder;

    @BeforeEach
    void setUp() {
        permissionService = new PermissionService(contextHolder);
    }

    @Test
    void shouldAllowAccessWhenProfileIsAdmin() {
        Mockito.when(contextHolder.getProfile()).thenReturn(Profile.ADMIN.name());

        Assertions.assertDoesNotThrow(() -> permissionService.validateAdminAccess());

    }

    @Test
    void shouldThrowExceptionWhenProfileIsNotAdmin() {
        Mockito.when(contextHolder.getProfile()).thenReturn(Profile.USER.name());

        BusinessException exception = Assertions.assertThrows(
                BusinessException.class,
                () -> permissionService.validateAdminAccess()
        );

        Assertions.assertEquals(
                MessageErrorConstants.ERROR_OPERATION_NOT_PERMITTED_FOR_THIS_PROFILE,
                exception.getMessage()
        );
    }

}

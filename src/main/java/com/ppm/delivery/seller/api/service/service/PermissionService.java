package com.ppm.delivery.seller.api.service.service;

import com.ppm.delivery.seller.api.service.api.interceptor.ContextHolder;
import com.ppm.delivery.seller.api.service.domain.profile.Profile;
import com.ppm.delivery.seller.api.service.exception.BusinessException;
import com.ppm.delivery.seller.api.service.exception.MessageErrorConstants;
import org.springframework.stereotype.Service;

@Service
public class PermissionService implements IPermissionService {

    private final ContextHolder contextHolder;

    public PermissionService(ContextHolder contextHolder) {
        this.contextHolder = contextHolder;
    }

    @Override
    public void validateAdminAccess() {

        if (!Profile.ADMIN.name().equals(contextHolder.getProfile())) {
            throw new BusinessException(MessageErrorConstants.ERROR_OPERATION_NOT_PERMITTED_FOR_THIS_PROFILE);
        }
    }

}
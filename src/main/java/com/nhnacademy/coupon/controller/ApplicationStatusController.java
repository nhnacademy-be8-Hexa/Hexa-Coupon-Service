package com.nhnacademy.coupon.controller;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.nhnacademy.coupon.actuator.ApplicationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/actuator/status")
public class ApplicationStatusController {

    private final ApplicationInfoManager applicationInfoManager;
    private final ApplicationStatus applicationStatus;

    public ApplicationStatusController(ApplicationInfoManager applicationInfoManager, ApplicationStatus applicationStatus) {
        this.applicationInfoManager = applicationInfoManager;
        this.applicationStatus = applicationStatus;
    }

    @PostMapping("/down")
    @ResponseStatus(value = HttpStatus.OK)
    public void stopStatus(){
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.DOWN);
        applicationStatus.stopService();
    }

    @PostMapping("/up")
    @ResponseStatus(value = HttpStatus.OK)
    public void startStatus(){
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        applicationStatus.startService();
    }
}

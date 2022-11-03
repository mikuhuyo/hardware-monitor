package com.example.backend.controller.chain;

import com.example.backend.service.chain.IDeviceGeoService;
import com.example.chain.dto.DeviceGeoDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@RestController
@RequestMapping("/api")
public class DeviceGeoController {
    @Autowired
    private IDeviceGeoService deviceGeoService;

    @GetMapping("/gps/device-details/{latitude}/{longitude}/{distance}")
    public List<DeviceGeoDetails> deviceGeoDetailsList(@PathVariable("latitude") Double latitude, @PathVariable("longitude") Double longitude, @PathVariable("distance") String distance) {
        return deviceGeoService.searchDeviceGeoLocationDetails(longitude, latitude, distance);
    }

}

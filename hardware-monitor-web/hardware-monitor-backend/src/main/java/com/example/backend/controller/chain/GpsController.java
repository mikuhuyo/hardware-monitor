package com.example.backend.controller.chain;

import com.example.backend.service.chain.IGpsService;
import com.example.chain.dto.GpsDTO;
import com.example.chain.vo.GpsVO;
import com.example.common.domain.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@RestController
@RequestMapping("/api")
public class GpsController {
    @Autowired
    private IGpsService gpsService;

    @GetMapping("/gps")
    public GpsDTO findGps() {
        return gpsService.findGps();
    }

    @PutMapping("/gps")
    public RestResponse<String> updateGps(@RequestBody GpsVO gpsVO) {
        gpsService.updateGpsById(gpsVO);

        return RestResponse.success();
    }

    @DeleteMapping("/gps/{id}")
    public RestResponse<String> removeGps(@PathVariable("id") Long id) {
        gpsService.removeGpsById(id);

        return RestResponse.success();
    }

    @PostMapping("/gps")
    public RestResponse<String> createGps(@RequestBody GpsVO gpsVO) {
        gpsService.creatGps(gpsVO);
        return RestResponse.success();
    }
}


package com.example.waterwatch.controller;

import com.example.waterwatch.entity.Device;
import com.example.waterwatch.repository.DeviceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceRepository deviceRepository;

    public DeviceController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @GetMapping
    public List<Device> getDevices() {
        return deviceRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Device createDevice(@RequestBody Device device) {
        if (device.getDeviceName() == null
                || device.getDeviceName().isBlank()
                || device.getLocation() == null
                || device.getLocation().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Device name and location are required"
            );
        }

        device.setStatus("ONLINE");
        return deviceRepository.save(device);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDevice(@PathVariable Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Device not found"
            );
        }

        deviceRepository.deleteById(id);
    }
}

package com.jdh.ms_security.Controllers;

import com.jdh.ms_security.Models.Permission;
import com.jdh.ms_security.Repositories.PermissionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/permissions")

public class PermissionController {
    @Autowired
    PermissionRepository thePermissionRepository;

    @GetMapping("")
    public List<Permission> find() {
        return this.thePermissionRepository.findAll();
    }

    @GetMapping("{id}")
    public Permission findById(@PathVariable String id) {
        Permission thePermission = this.thePermissionRepository.findById(id).orElse(null);
        return thePermission;
    }

    //Prueba para comprobar si funciona BD
    /*@PostConstruct
    public void testDb() {
        Permission testPermission = new Permission("testUrl", "testMethod");
        this.thePermissionRepository.save(testPermission);
        System.out.println("Saved permission: " + testPermission);
    }*/

    @PostMapping
    public Permission create(@RequestBody Permission newPermission) {
        System.out.println("Received permission: " + newPermission);
        return this.thePermissionRepository.save(newPermission);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        Permission thePermission = this.thePermissionRepository.findById(id).orElse(null);
        if (thePermission != null) {
            this.thePermissionRepository.delete(thePermission);
        }
    }

    @PutMapping("{id}")
    public Permission update(@PathVariable String id, @RequestBody Permission newPermission) {
        Permission actualPermission = this.thePermissionRepository.findById(id).orElse(null);
        if (actualPermission != null) {
            actualPermission.setUrl(newPermission.getUrl());
            actualPermission.setMethod(newPermission.getMethod());
            this.thePermissionRepository.save(actualPermission);
            return actualPermission;

        } else {
            return null;
        }
    }

}
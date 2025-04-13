package com.aws.optimizer.controller;

import com.aws.optimizer.service.CostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cost")
public class CostController {

    @Autowired
    private CostService costService;

    @GetMapping("/ec2-unused")
    public List<String> getUnusedEC2Instances() {
        return costService.getUnusedEC2Instances();
    }

    @GetMapping("/s3-all")
    public List<String> getAllS3Buckets() {
        return costService.getAllS3Buckets();
    }

    @GetMapping("/rds-all")
    public List<String> getAllRDSInstances() {
        return costService.getAllRDSInstances();
    }

    @GetMapping("/total-cost")
    public String getTotalMonthlyCost() {
        return costService.getTotalMonthlyCost();
    }



}


package com.aws.optimizer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.*;


import java.util.ArrayList;
import java.util.List;

@Service
public class CostService {

    @Value("${aws.region}")
    private String awsRegion;

    public List<String> getUnusedEC2Instances() {
        List<String> unusedInstances = new ArrayList<>();

        Ec2Client ec2Client = Ec2Client.builder()
                .region(Region.of(awsRegion))
                .build();

        DescribeInstancesResponse response = ec2Client.describeInstances();

        for (Reservation reservation : response.reservations()) {
            for (Instance instance : reservation.instances()) {
                if (instance.state().name().equals(InstanceStateName.STOPPED)) {
                    unusedInstances.add(instance.instanceId());
                }
            }
        }

        return unusedInstances;
    }
    public List<String> getAllS3Buckets() {
        List<String> buckets = new ArrayList<>();

        S3Client s3Client = S3Client.builder()
                .region(Region.of(awsRegion))
                .build();

        ListBucketsResponse response = s3Client.listBuckets();

        for (Bucket bucket : response.buckets()) {
            buckets.add(bucket.name());
        }

        return buckets;
    }

    public List<String> getAllRDSInstances() {
        List<String> instances = new ArrayList<>();

        RdsClient rdsClient = RdsClient.builder()
                .region(Region.of(awsRegion))
                .build();

        DescribeDbInstancesResponse response = rdsClient.describeDBInstances();

        for (DBInstance dbInstance : response.dbInstances()) {
            instances.add(dbInstance.dbInstanceIdentifier());
        }

        return instances;
    }

    public String getTotalMonthlyCost() {

        CostExplorerClient ceClient = CostExplorerClient.builder()
                .region(Region.US_EAST_1) // Cost Explorer is always in us-east-1
                .build();

        GetCostAndUsageRequest request = GetCostAndUsageRequest.builder()
                .granularity(Granularity.MONTHLY)
                .metrics("BlendedCost")
                .timePeriod(DateInterval.builder()
                        .start("2024-04-01")  // This Month Start
                        .end("2024-04-30")    // This Month End
                        .build())
                .build();

        GetCostAndUsageResponse response = ceClient.getCostAndUsage(request);

        return response.resultsByTime().get(0).total().get("BlendedCost").amount();
    }

}

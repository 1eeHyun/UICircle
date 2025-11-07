package edu.uic.marketplace.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${AWS_ACCESS_KEY_ID}")
    private String accessKey;

    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String secretKey;

    @Value("${AWS_REGION}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {

        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);

        // DefaultAWSCredentialsProviderChain:
        // 1. Environment (AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY)
        // 2. ~/.aws/credentials
        // 3. EC2/ECS IAM Role
        // Search in order
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();
    }
}

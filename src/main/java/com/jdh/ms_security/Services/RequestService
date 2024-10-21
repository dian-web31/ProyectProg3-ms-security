package com.jdh.ms_security.Services;

import com.jdh.ms_security.entities.EmailContent;
import com.jdh.ms_security.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class RequestService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${apis.ms-notification-url}")
    private String notificationUrl;

    public List<UserEntity> getUsers(){
        String endpointName = "get-users";
        String url = notificationUrl + endpointName;
        ResponseEntity<UserEntity[]> response = restTemplate.getForEntity(url, UserEntity[].class);
        UserEntity[] users = response.getBody();
        return Arrays.asList(users);
    }

    public void sendEmail(EmailContent content){
        String endpointName = "send-email";
        String url = notificationUrl + endpointName;
        restTemplate.postForObject(url, content, String.class);

    }
}

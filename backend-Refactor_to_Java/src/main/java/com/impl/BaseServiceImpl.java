package com.impl;

import com.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public abstract class BaseServiceImpl implements BaseService {

    @Autowired
    protected RestTemplate restTemplate;
}
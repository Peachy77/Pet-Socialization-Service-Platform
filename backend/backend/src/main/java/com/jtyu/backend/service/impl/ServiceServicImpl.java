package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.ServiceMapper;
import com.jtyu.backend.model.Service;
import com.jtyu.backend.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ServiceServicImpl implements ServiceService {
    @Autowired
    private ServiceMapper serviceMapper;

    @Override
    public List<Service> getAllServices() {
        return serviceMapper.findAll();
    }
}

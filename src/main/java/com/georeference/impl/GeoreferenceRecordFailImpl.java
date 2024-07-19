package com.georeference.impl;

import com.georeference.process.entities.GeoreferenceRecordFail;
import com.georeference.process.repositories.GeoreferenceRecordFailRepository;
import com.georeference.services.GeoreferenceRecordFailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeoreferenceRecordFailImpl implements GeoreferenceRecordFailService {

    @Autowired
    private GeoreferenceRecordFailRepository georeferenceRecordFailRepository;

    @Override
    public List<GeoreferenceRecordFail> getGeoreferenceRecordFails(Long recordRequestId) {
        return georeferenceRecordFailRepository.getByRecordRequestId(recordRequestId);
    }
}

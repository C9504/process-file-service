package com.georeference.services;

import com.georeference.process.entities.GeoreferenceRecordFail;

import java.util.List;

public interface GeoreferenceRecordFailService {
    List<GeoreferenceRecordFail> getGeoreferenceRecordFails(Long recordRequestId);
}

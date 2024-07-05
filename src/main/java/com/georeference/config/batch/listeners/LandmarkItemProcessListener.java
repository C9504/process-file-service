package com.georeference.config.batch.listeners;

import com.georeference.entities.Landmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

public class LandmarkItemProcessListener implements ItemProcessListener<Landmark, Landmark> {

    public static final Logger logger = LoggerFactory.getLogger(LandmarkItemProcessListener.class);

    @Override
    public void beforeProcess(Landmark input) {
        logger.info("Landmark record has been read: " + input);
    }

    @Override
    public void afterProcess(Landmark input, Landmark result) {
        logger.info("Landmark record has been processed to : " + result);
    }

    @Override
    public void onProcessError(Landmark input, Exception e) {
        logger.error("Error in reading the landmark record : " + input);
        logger.error("Error in reading the landmark record : " + e);
    }
}

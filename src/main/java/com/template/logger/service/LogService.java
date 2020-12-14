package com.template.logger.service;

import com.template.logger.entities.Action;
import com.template.logger.rest.interceptors.CommonLogData;

import java.util.List;

public interface LogService {

    void logAction(CommonLogData data, long itemId);

    void logAction(CommonLogData data, String text);

    List<Action> getAll();
}

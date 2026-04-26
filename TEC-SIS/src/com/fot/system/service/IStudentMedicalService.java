package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import java.util.List;

public interface IStudentMedicalService {
    List<AbsentSessionOption> getAbsentSessionsForPeriod(int studentUserId, String startDate, String endDate);
    void submitMedical(AddStudentMedicalRequest request);
}

package com.sirma.employees.service;

import com.sirma.employees.models.ProjectEmployee;
import com.sirma.employees.utils.EmployeePair;
import com.sirma.employees.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ProjectEmployeeServiceImpl implements ProjectEmployeeService {

    Logger logger = LoggerFactory.getLogger(ProjectEmployeeServiceImpl.class);


    @Override
    public EmployeePair pairEmployeesWithCommonProjects(List<ProjectEmployee> pE) throws Exception {

        EmployeePair employeePair = new EmployeePair();
        employeePair.setEmpId1(0);
        employeePair.setEmpId2(0);
        employeePair.setMaxWorkingDays(0L);

        for (int i = 0; i < pE.size() ; i++) {
            for (int j = i+1; j < pE.size(); j++) {
                if (pE.get(i).getProjectId() == pE.get(j).getProjectId() && TimeUtil.wasTimeOverlapped(pE.get(i).getDateFrom(), pE.get(i).getDateTo(), pE.get(j).getDateFrom(), pE.get(j).getDateTo())) {
                    long overlappedDays = TimeUtil.getOverlapDays(pE.get(i).getDateFrom(), pE.get(i).getDateTo(), pE.get(j).getDateFrom(), pE.get(j).getDateTo());
                    if (employeePair.getMaxWorkingDays() < overlappedDays){
                        employeePair.setEmpId1(pE.get(i).getEmpId());
                        employeePair.setEmpId2(pE.get(j).getEmpId());
                        employeePair.setMaxWorkingDays(overlappedDays);
                    }
                }
            }
        }

        if (employeePair.getMaxWorkingDays().equals(0L)) {
            throw new Exception("There is no overlapped times between the employees on csv");
        }

        return employeePair;
    }

    @Override
    public List<ProjectEmployee> rowsToProjectEmployees(List<String[]> rows) {
        if (rows.size() == 0) {
            return Collections.emptyList();
        }

        List<ProjectEmployee> result = new ArrayList<>();

        logger.info("Converting rows into objects...");

        rows.forEach(array -> {
            Integer empId = Integer.parseInt(array[0]);
            Integer projectId = Integer.parseInt(array[1]);
            LocalDate dateFrom = TimeUtil.parseDate(array[2]);
            LocalDate dateTo = array[3].equals("NULL") ? LocalDate.now() : TimeUtil.parseDate(array[3]);
            result.add(ProjectEmployee.builder()
                            .empId(empId)
                            .projectId(projectId)
                            .dateFrom(dateFrom)
                            .dateTo(dateTo)
                    .build());
        });

        return result;
    }
}
package com.runcita.api.runningType;

import com.runcita.api.shared.models.RunningType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Running level service
 */
@Service
public class RunningTypeService {

    private final RunningTypeRepository runningTypeRepository;

    RunningTypeService(RunningTypeRepository runningTypeRepository) {
        this.runningTypeRepository = runningTypeRepository;
    }

    /**
     * Recover all running types
     * @return list running type
     */
    public List<RunningType> getRunningTypes() {
        return runningTypeRepository.findAll();
    }
}

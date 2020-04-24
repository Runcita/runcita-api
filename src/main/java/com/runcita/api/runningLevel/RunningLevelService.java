package com.runcita.api.runningLevel;

import com.runcita.api.shared.models.RunningLevel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Running level service
 */
@Service
public class RunningLevelService {

    private final RunningLevelRepository runningLevelRepository;

    RunningLevelService(RunningLevelRepository runningLevelRepository) {
        this.runningLevelRepository = runningLevelRepository;
    }

    /**
     * Recover all running levels
     * @return list running level
     */
    public List<RunningLevel> getRunningLevels() {
        return runningLevelRepository.findAll();
    }
}

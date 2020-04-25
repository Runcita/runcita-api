package com.runcita.api.runningLevel;

import com.runcita.api.shared.models.RunningLevel;
import com.runcita.api.shared.models.User;
import com.runcita.api.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Running level controller
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/running-levels")
public class RunningLevelController {

    private final RunningLevelService runningLevelService;

    RunningLevelController(RunningLevelService runningLevelService) {
        this.runningLevelService = runningLevelService;
    }

    /**
     * Recover all running levels
     * @return list running level
     */
    @GetMapping
    public ResponseEntity<List<RunningLevel>> recoverRunningLevels() {
        List<RunningLevel> runningLevelList = runningLevelService.getRunningLevels();
        return new ResponseEntity<>(runningLevelList, HttpStatus.OK);
    }
}

package com.runcita.api.runningType;

import com.runcita.api.shared.models.RunningType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Running level controller
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/running-types")
public class RunningTypeController {

    private final RunningTypeService runningTypeService;

    RunningTypeController(RunningTypeService runningTypeService) {
        this.runningTypeService = runningTypeService;
    }

    /**
     * Recover all running types
     * @return list running type
     */
    @GetMapping
    public ResponseEntity<List<RunningType>> recoverRunningTypes() {
        List<RunningType> runningTypeList = runningTypeService.getRunningTypes();
        return new ResponseEntity<>(runningTypeList, HttpStatus.OK);
    }
}

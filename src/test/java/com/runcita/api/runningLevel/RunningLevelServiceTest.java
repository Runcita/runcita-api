package com.runcita.api.runningLevel;

import com.runcita.api.Application;
import com.runcita.api.shared.models.RunningLevel;
import com.runcita.api.user.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class RunningLevelServiceTest {

    @Autowired
    RunningLevelService runningLevelService;

    @MockBean
    RunningLevelRepository runningLevelRepository;

    private RunningLevel runningLevel1;
    private RunningLevel runningLevel2;

    @BeforeEach
    void initBeforeTest() {
        runningLevel1 = RunningLevel.builder()
                .id(1L)
                .name(RunningLevel.RunningLevelEnum.GAZELLE)
                .build();

        runningLevel2 = RunningLevel.builder()
                .id(2L)
                .name(RunningLevel.RunningLevelEnum.TORTUE)
                .build();
    }

    @Test
    void getRunningLevels_test() {
        when(runningLevelRepository.findAll()).thenReturn(Arrays.asList(runningLevel1, runningLevel2));
        assertThat(runningLevelService.getRunningLevels(), hasItems(runningLevel1, runningLevel2));
    }

    @Test
    void getRunningLevels_with_no_result_test() {
        when(runningLevelRepository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(runningLevelService.getRunningLevels().isEmpty());
    }

}

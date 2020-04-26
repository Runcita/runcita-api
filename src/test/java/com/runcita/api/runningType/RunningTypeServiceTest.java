package com.runcita.api.runningType;

import com.runcita.api.Application;
import com.runcita.api.shared.models.RunningType;
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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class RunningTypeServiceTest {

    @Autowired
    RunningTypeService runningTypeService;

    @MockBean
    RunningTypeRepository runningTypeRepository;

    private RunningType runningType1;
    private RunningType runningType2;

    @BeforeEach
    void initBeforeTest() {
        runningType1 = RunningType.builder()
                .id(1L)
                .name(RunningType.RunningTypeEnum.FOOTING)
                .build();

        runningType2 = RunningType.builder()
                .id(2L)
                .name(RunningType.RunningTypeEnum.FRACTIONNE)
                .build();
    }

    @Test
    void getRunningTypes_test() {
        when(runningTypeRepository.findAll()).thenReturn(Arrays.asList(runningType1, runningType2));
        assertThat(runningTypeService.getRunningTypes(), hasItems(runningType1, runningType2));
    }

    @Test
    void getRunningTypes_with_no_result_test() {
        when(runningTypeRepository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(runningTypeService.getRunningTypes().isEmpty());
    }

}

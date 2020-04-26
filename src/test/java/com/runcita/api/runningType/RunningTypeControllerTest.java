package com.runcita.api.runningType;

import com.runcita.api.Application;
import com.runcita.api.shared.models.RunningType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RunningTypeControllerTest {

    @MockBean
    RunningTypeService runningTypeService;

    @Autowired
    private MockMvc mockMvc;

    private final String RECOVER_RUNNING_TYPES_PATH = "/api/running-types";

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
    public void recoverRunningLevels_test() throws Exception {
        Mockito.when(runningTypeService.getRunningTypes()).thenReturn(Arrays.asList(runningType1, runningType2));

        mockMvc.perform(get(RECOVER_RUNNING_TYPES_PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(runningType1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(runningType2.getId()));
    }
}

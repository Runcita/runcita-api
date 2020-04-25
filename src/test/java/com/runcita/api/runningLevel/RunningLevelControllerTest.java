package com.runcita.api.runningLevel;

import com.runcita.api.Application;
import com.runcita.api.shared.models.RunningLevel;
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
class RunningLevelControllerTest {

    @MockBean
    RunningLevelService runningLevelService;

    @Autowired
    private MockMvc mockMvc;

    private final String RECOVER_RUNNING_LEVELS_PATH = "/api/running-levels";

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
    public void recoverRunningLevels_test() throws Exception {
        Mockito.when(runningLevelService.getRunningLevels()).thenReturn(Arrays.asList(runningLevel1, runningLevel2));

        mockMvc.perform(get(RECOVER_RUNNING_LEVELS_PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(runningLevel1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(runningLevel2.getId()));
    }
}

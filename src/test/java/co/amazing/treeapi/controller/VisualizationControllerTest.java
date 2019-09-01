package co.amazing.treeapi.controller;

import co.amazing.treeapi.TestConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class VisualizationControllerTest {

    @ClassRule
    public static final TestConfig config = new TestConfig();
    @Autowired
    private WebTestClient api;

    @Test
    public void csv() throws IOException {
        List<String> lines = Files.readAllLines(new ClassPathResource("visualization.csv").getFile().toPath());
        TreeSet<String> expected = new TreeSet<>(lines);
        api.get().uri("/visualization").exchange().expectStatus().isOk().expectBody(String.class).value(s -> {
            TreeSet<String> actual = new TreeSet<>(Arrays.asList(s.split("\n")));
            Assert.assertEquals(expected, actual);
        });
    }
}
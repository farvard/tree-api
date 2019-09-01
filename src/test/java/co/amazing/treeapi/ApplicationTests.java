package co.amazing.treeapi;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"create.random.tree=true"})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ApplicationTests {

    @ClassRule
    public static final TestConfig config = new TestConfig();

    @Test
    public void contextLoads() throws Exception {
        Application.main(new String[0]);
    }

}

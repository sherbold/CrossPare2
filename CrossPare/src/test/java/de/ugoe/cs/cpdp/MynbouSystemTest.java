package de.ugoe.cs.cpdp;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MynbouSystemTest {

    @Parameters(name = "file {index}: {0}")
    public static Iterable<String> data() {
        File dir = new File("testdata/mynbou-test");
        return Arrays.asList(dir.listFiles())
                .stream().map(x->x.getName())
                .collect(Collectors.toList());
    }

    @Parameter
    public String fileName;

    @Test
    public void test() {
        Runner.main(new String[]{fileName});
    }
    
}
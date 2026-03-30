import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class ConsoleRunnerTest {
    @Test
    public void testOnePlayerGuessOk() throws Exception {
        MockGame game = new MockGame(3);

        Reader fakeInput = new CharArrayReader("Test Student\n20\n80\n45\n50\n".toCharArray());

        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);
        PrintStream err = new PrintStream(new ByteArrayOutputStream());

        ConsoleRunner runner = new ConsoleRunner(game, new BufferedReader(fakeInput), out, err);
        runner.runGame();

        BufferedReader runOutput = new BufferedReader(new CharArrayReader(outBytes.toString().toCharArray()));

        String[] expectedLines = {
                "[For you] What's your name?",
                "[For everyone] Test Student joined",
                "[For Test Student] enter your guess->",
                "[For Test Student] 20",
                "[For Test Student] enter your guess->",
                "[For Test Student] 80",
                "[For Test Student] enter your guess->",
                "[For Test Student] 45",
                "Game Over!"
        };

        for (String expectedLine : expectedLines) {
            String actualLine = runOutput.readLine();
            assertEquals("Console output isn't as expected", expectedLine, actualLine.trim());
        }
    }
}
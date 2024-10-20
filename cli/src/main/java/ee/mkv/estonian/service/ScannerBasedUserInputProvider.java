package ee.mkv.estonian.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
@Slf4j
public class ScannerBasedUserInputProvider implements UserInputProvider {
    @Override
    public int getUserChoice(String[] options) {
        Scanner in = new Scanner(System.in);
        int input = -1;
        boolean validInput = false;
        do {
            try {
                input = in.nextInt();
                validInput = true;
            } catch (NoSuchElementException e) {
                log.error("Invalid input", e);
            }
        } while (!validInput);
        return input;
    }

    @Override
    public String getFreeFormInput() {
        Scanner in = new Scanner(System.in);
        var s = in.nextLine();
        if (s.isEmpty()) {
            log.error("Empty input");
            throw new IllegalArgumentException("Empty input");
        }
        return s;
    }
}

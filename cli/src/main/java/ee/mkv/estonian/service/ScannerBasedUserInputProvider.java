package ee.mkv.estonian.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
@Slf4j
public class ScannerBasedUserInputProvider implements UserInputProvider {

    private static final Scanner scanner = new Scanner(System.in);

    @Override
    public int getUserChoice(String[] options) {
        scanner.reset();
        int input = -1;
        boolean validInput = false;
        do {
            try {
                input = scanner.nextInt();
                validInput = true;
            } catch (NoSuchElementException e) {
                scanner.reset();
                log.error("Invalid input", e);
            }
        } while (!validInput);
        scanner.nextLine(); // Clear the newline character from the input buffer
        scanner.reset();
        return input;
    }

    @Override
    public String getFreeFormInput() {
        var s = scanner.nextLine();
        if (s.isEmpty()) {
            log.error("Empty input");
            throw new IllegalArgumentException("Empty input");
        }
        scanner.reset();
        return s;
    }
}

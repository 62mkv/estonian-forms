package ee.mkv.estonian.service;

import org.springframework.stereotype.Component;

@Component
public class DummyInputProvider implements UserInputProvider {
    @Override
    public int getUserChoice() {
        return 0;
    }

    @Override
    public String getFreeFormInput() {
        return "dummy";
    }
}

package ee.mkv.estonian.service;

import org.springframework.stereotype.Component;

@Component
public class DummyInputProvider implements UserInputProvider {
    @Override
    public int getUserChoice(String[] options) {
        return 0;
    }

    @Override
    public String getFreeFormInput() {
        return "dummy";
    }
}

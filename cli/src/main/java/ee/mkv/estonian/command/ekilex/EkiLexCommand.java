package ee.mkv.estonian.command.ekilex;

import ee.mkv.estonian.ekilex.EkilexService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;

@Component
@CommandLine.Command(name = "ekilex")
@RequiredArgsConstructor
public class EkiLexCommand implements Runnable {

    private final EkilexService ekilexService;

    @CommandLine.Option(names = {"-i", "--id-list"})
    private String idList;

    @CommandLine.Option(names = {"-w", "--word"}, split = ",")
    private List<String> words = new ArrayList<>();

    @CommandLine.Option(names = {"-f", "--force"})
    private boolean force;

    @Override
    public void run() {
        ekilexService.runEkilex(idList, words, force);
    }

}

package ee.mkv.estonian.command.wiki;


import ee.mkv.estonian.service.WikiService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "wikidata")
@ConditionalOnProperty("wikidata.site")
@RequiredArgsConstructor
public class WikiCommand implements Runnable {

    private final WikiService wikiService;

    @Override
    public void run() {
        wikiService.runWikiUpload();
    }

}

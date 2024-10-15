# Things to consider

1. Some lexemes have multiple paradigms. This is defined as combination of "declination type" + "option_number" in the fmsynth output
2. Currently, option number is ignored during import, which is *bad*. it has to be added, with a caveat:
   1. For some lexemes, fmsynth will report same paradigm twice (see "saks"). Duplicated paradigms must be ignored while imported.
3. From the article, "initial forms" could be parsed to compare with fmsynth-provided paradigms, and non-compliant paradigms must be ignored.
4. This should provide for much safer articleForm sets.

# Running in IntelliJ IDEA

In order to run JFX application in IntelliJ IDEA Community:

- download JavaFX SDK into some folder
- create "Application" type Run Configuration
- add VM options:
  `--module-path /path/to/javafx-sdk-21.0.x/lib --add-modules javafx.controls,javafx.fxml` 
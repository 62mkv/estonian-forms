package ee.mkv.estonian.service;

/**
 *
 */
public interface UserInputProvider {

    /**
     * Returns the index of the selected option
     *
     * @param options - list of options
     * @return index of the selected option
     */
    int getUserChoice(String[] options);

    /**
     * Returns user input in free-form as a string
     *
     * @return user input
     */
    String getFreeFormInput();

}

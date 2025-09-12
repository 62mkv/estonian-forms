package ee.mkv.estonian.service;

/**
 *
 */
public interface UserInputProvider {

    /**
     * Returns the index of the selected option
     *
     * @return index of the selected option
     */
    int getUserChoice();

    /**
     * Returns user input in free-form as a string
     *
     * @return user input
     */
    String getFreeFormInput();

}

package straw.polito.it.straw;

/**
 * Created by sylva on 26/04/2016.
 */
public interface MessageSender {
    String[] getAddresses(boolean email);
    String getMessage();
    void displayConfirmationToast(int count);
}

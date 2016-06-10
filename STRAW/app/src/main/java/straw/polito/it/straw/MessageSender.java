package straw.polito.it.straw;

/**
 * Created by Sylvain on 26/04/2016.
 */
public interface MessageSender {
    String[] getAddresses(boolean email);
    String getMessage();
    String getSubject();
    void displayConfirmationToast(int count);
}

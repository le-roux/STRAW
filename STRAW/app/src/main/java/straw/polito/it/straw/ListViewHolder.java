package straw.polito.it.straw;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Andres Camilo Jimenez on 03/04/2016.
 */
public class ListViewHolder {
    private TextView itmName;
    private EditText itmText;
    private Spinner itmSpn;
    private int pos;
    private int id;
    private boolean sw;

    public ListViewHolder() {
    }

    public boolean isSw() {
        return sw;
    }

    public void setSw(boolean sw) {
        this.sw = sw;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public Spinner getItmSpn() {
        return itmSpn;
    }

    public void setItmSpn(Spinner itmSpn) {
        this.itmSpn = itmSpn;
    }

    public TextView getItmName() {
        return itmName;
    }

    public void setItmName(TextView itmName) {
        this.itmName = itmName;
    }

    public EditText getItmText() {
        return itmText;
    }

    public void setItmText(EditText itmText) {
        this.itmText = itmText;
    }
}

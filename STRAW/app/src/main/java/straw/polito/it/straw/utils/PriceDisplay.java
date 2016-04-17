package straw.polito.it.straw.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import straw.polito.it.straw.R;

import static straw.polito.it.straw.utils.PriceDisplay.Currency.*;

/**
 * Created by Sylvain on 17/04/2016.
 */
public class PriceDisplay extends TextView {

    enum Currency {EURO};
    private Currency currency;
    private double price;

    public PriceDisplay(Context context) {
        this(context, null, 0);
    }

    public PriceDisplay(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PriceDisplay(Context context, AttributeSet attributeSet, int style) {
        super(context, attributeSet, style);
        this.currency = EURO;
    }

    public void setPrice(double price) {
        this.price = price;
        StringBuilder builder = new StringBuilder();
        builder.append(this.getContext().getResources().getString(R.string.Price))
                .append((" : "))
                .append(price);
        switch(this.currency) {
            case EURO:
                builder.append('€');
                break;
        }
        this.setText(builder.toString());
    }

    public double getPrice() {
        return this.price;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}

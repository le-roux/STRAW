package straw.polito.it.straw;

import straw.polito.it.straw.utils.PriceDisplay;

/**
 * Created by Sylvain on 19/04/2016.
 */
public interface PriceContainer {
    PriceDisplay getPriceDisplay();
    void updatePrice();
}

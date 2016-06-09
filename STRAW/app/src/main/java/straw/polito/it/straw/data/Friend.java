package straw.polito.it.straw.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.json.JSONException;
import org.json.JSONObject;

import straw.polito.it.straw.utils.Logger;

/**
 * Created by Sylvain on 13/05/2016.
 */
public class Friend {

    private String name;
    private String phoneNumber;
    private String emailAddress;

    public static final String NAME = "name";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String EMAIL_ADDRESS = "emailAddress";

    /**
     * This constructor is needed by Firebase in order to rebuild the Friend objects.
     */
    public Friend() {

    };

    public Friend(String name, String phoneNumber, String emailAddress) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    public Friend (String description) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(description);
            this.setName(jsonObject.getString(NAME));
        } catch (JSONException e) {
            Logger.d("Error reconstructing friend from string representation");
        }
        try {
            this.setPhoneNumber(jsonObject.getString(PHONE_NUMBER));
        } catch (JSONException e) {
        }
        try {
            this.setEmailAddress(jsonObject.getString(EMAIL_ADDRESS));
        } catch (JSONException e) {
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    };

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * WARNING : Don't use this method to store a Friend object as a String. Use
     * saveAsString() instead.
     * @return : a string describing this Friend object
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name);
        String address = this.getAddresses();
        if (!address.equals("")) {
            builder.append(" (")
                    .append(address)
                    .append(")");
        }
        return builder.toString();
    }

    /**
     *
     * @return : a string describing the 'addresses (email and phone number) of a Friend
     */
    @JsonIgnore
    public String getAddresses() {
        StringBuilder builder = new StringBuilder();
        if (this.phoneNumber != null) {
            builder.append(this.phoneNumber);
            if (this.emailAddress != null)
                builder.append(", ");
        }
        if (this.emailAddress != null) {
            builder.append(this.emailAddress);
        }
        return  builder.toString();
    }

    /**
     * Convert the Friend object in a JSONObject.
     * @return : the JSON representation of a Friend object.
     */
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NAME, this.name);
            jsonObject.put(PHONE_NUMBER, this.phoneNumber);
            jsonObject.put(EMAIL_ADDRESS, this.emailAddress);
        } catch (JSONException e) {
            Logger.d("Error converting the user in JSONObject");
            return null;
        }
        return jsonObject;
    }


    /**
     * Convert the Friend object in a String.
     * @return : the stringified JSON representation of the Friend object.
     */
    public String saveAsString() {
        return this.toJSONObject().toString();
    }
}

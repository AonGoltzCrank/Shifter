package alon.com.shifter.shift_utils;

import java.io.Serializable;

/**
 * Created by Alon on 11/22/2016.
 */

public class Comment implements Serializable {

    private String[] mComments = new String[4];

    public String[] getComments() {
        return mComments;
    }

    public void setComments(String[] mComments) {
        this.mComments = mComments;
    }

}

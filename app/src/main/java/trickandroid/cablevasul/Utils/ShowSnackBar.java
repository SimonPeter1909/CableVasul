package trickandroid.cablevasul.Utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Simon Peter on 03-Nov-17.
 */

public class ShowSnackBar {

    public void snackBar(View view, String m) {
        Snackbar snackbar = Snackbar.make(view, m, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

}

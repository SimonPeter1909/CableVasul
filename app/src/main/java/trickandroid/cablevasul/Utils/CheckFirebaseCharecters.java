package trickandroid.cablevasul.Utils;

import android.content.Context;
import android.widget.EditText;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Simon on 2/9/2018.
 */

public class CheckFirebaseCharecters {

    public boolean checkFirebaseCharecters(String text){
        return !(text.contains(".") || text.contains("$") || text.contains("[") || text.contains("]") || text.contains("#") || text.contains("/"));
    }

    public SweetAlertDialog errorDialog(Context context){
        return new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("Don't use . $ [ ] # / ")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog1) {
                        sweetAlertDialog1.dismissWithAnimation();
                    }
                });
    }
}

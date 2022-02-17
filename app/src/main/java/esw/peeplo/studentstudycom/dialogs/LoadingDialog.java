package esw.peeplo.studentstudycom.dialogs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import esw.peeplo.studentstudycom.R;

public class LoadingDialog {

    private Context context;
    private Activity activity;
    private String text;

    //loading
    private android.app.AlertDialog theDialog;

    public LoadingDialog(Context context, Activity activity, String text) {
        this.context = context;
        this.activity = activity;
        this.text = text;
    }

    public void showDialog(){
        //create dialog
        theDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.general_loading_dialog,null);

        //widget
        TextView dialogText = viewOptions.findViewById(R.id.dialogText);

        //dialog props
        theDialog.setView(viewOptions);
        theDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        theDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //lock dialog
        theDialog.setCancelable(false);
        theDialog.setCanceledOnTouchOutside(false);

        //set message
        dialogText.setText(text);

        //show dialog
        theDialog.show();
    }

    public void cancelDialog(){
        if (theDialog != null && !activity.isDestroyed()) {
            theDialog.dismiss();
        }
    }

}

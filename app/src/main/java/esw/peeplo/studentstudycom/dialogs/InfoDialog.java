package esw.peeplo.studentstudycom.dialogs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.interfaces.InfoClickListener;

public class InfoDialog {

    private Context context;
    private Activity activity;
    private String[] params;
    private InfoClickListener listener;

    //loading
    private android.app.AlertDialog theDialog;

    //constructor
    public InfoDialog(Context ctx, Activity act, String[] params, InfoClickListener listener){
        this.context = ctx;
        this.activity = act;
        this.params = params;
        this.listener = listener;
    }

    //show dialog
    public void showDialog(){
        //create dialog
        theDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.general_info_dialog,null);

        //widget
        TextView dialogTitle = viewOptions.findViewById(R.id.dialogTitle);
        TextView dialogText = viewOptions.findViewById(R.id.dialogText);
        TextView okayBtn = viewOptions.findViewById(R.id.okayBtn);

        //dialog props
        theDialog.setView(viewOptions);
        theDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        theDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //set message
        dialogTitle.setText(params[0]);
        dialogText.setText(params[1]);
        okayBtn.setText(params[2]);

        //okay
        okayBtn.setOnClickListener(view -> listener.onButtonClick());

        //show dialog
        if (activity.getWindow().getDecorView().getRootView().isShown()) {
            theDialog.show();
        }
    }

    //close dialog
    public void cancelDialog(){
        if (theDialog != null && !activity.isDestroyed()) {
            theDialog.dismiss();
        }
    }

}

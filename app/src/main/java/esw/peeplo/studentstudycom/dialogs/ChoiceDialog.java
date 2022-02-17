package esw.peeplo.studentstudycom.dialogs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import esw.peeplo.studentstudycom.R;
import esw.peeplo.studentstudycom.interfaces.ChoiceClickListener;

public class ChoiceDialog {

    private Context context;
    private Activity activity;
    private String[] params;
    private ChoiceClickListener listener;
    private boolean lockDialog;

    //loading
    private android.app.AlertDialog theDialog;

    //constructor
    public ChoiceDialog (Context ctx, Activity act, String[] params, ChoiceClickListener listener, boolean lockDialog){
        this.context = ctx;
        this.activity = act;
        this.params = params;
        this.listener = listener;
        this.lockDialog = lockDialog;
    }

    //show dialog
    public void showDialog(){
        //create dialog
        theDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View viewOptions = inflater.inflate(R.layout.general_choice_dialog,null);

        //widget
        TextView dialogTitle = viewOptions.findViewById(R.id.dialogTitle);
        TextView dialogText = viewOptions.findViewById(R.id.dialogText);
        TextView negativeBtn = viewOptions.findViewById(R.id.negativeBtn);
        TextView positiveBtn = viewOptions.findViewById(R.id.positiveBtn);

        //dialog props
        theDialog.setView(viewOptions);
        theDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        theDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //lock dialog
        theDialog.setCancelable(!lockDialog);
        theDialog.setCanceledOnTouchOutside(!lockDialog);

        //set message
        dialogTitle.setText(params[0]);
        dialogText.setText(params[1]);
        negativeBtn.setText(params[2]);
        positiveBtn.setText(params[3]);

        //okay
        negativeBtn.setOnClickListener(view -> listener.onNegativeClick());
        positiveBtn.setOnClickListener(view -> listener.onPositiveClick());

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

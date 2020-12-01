package com.chath.agenda;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.chath.agenda.data.ContentModel;
import com.chath.agenda.data.DatabaseHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ContentPage extends DialogFragment {

    private MainPage main;
    private ContentModel data;
    private String originalTitle;

    public ContentPage(MainPage main, ContentModel data) {
        this.main = main;
        this.data = data;
    }

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_page, container, false);

        TextView scope = view.findViewById(R.id.title_nc);

        EditText subject = view.findViewById(R.id.text1);
        EditText created = view.findViewById(R.id.text2);
        EditText title = view.findViewById(R.id.text3);
        EditText description = view.findViewById(R.id.text4);

        boolean editMode = data != null;
        scope.setText(getResources().getString(editMode ? R.string.edit_content_item : R.string.new_content_item));

        View delete_gap = view.findViewById(R.id.delete_gap);

        Button btn_delete = view.findViewById(R.id.delete_nc);
        btn_delete.setOnClickListener(view1 -> {
            new MaterialAlertDialogBuilder(getContext(), R.style.SquareAlert)
                    .setTitle(data.getTitle())
                    .setMessage(R.string.perform_del_content)
                    .setPositiveButton(R.string.alert_yes, (dialogInterface, i) -> {

                        boolean out = main.dataHelper.deleteContent(data);
                        Toast.makeText(getContext(), getString(R.string.alert_deleted) + out, Toast.LENGTH_SHORT).show();

                        if (out) {
                            AppUtilities.hideKeyboard(getContext());
                            dismiss();
                            main.updateRecyler();
                        }
                    })
                    .setNeutralButton(R.string.cancel, null)
                    .show();
        });

        Button btn_cancel = view.findViewById(R.id.cancel_nc);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtilities.hideKeyboard(getContext());
                dismiss();
            }
        });

        Button btn_submit = view.findViewById(R.id.submit_nc);
        btn_submit.setOnClickListener(view1 -> {

            String t = title.getText().toString();
            String d = description.getText().toString();
            String c = created.getText().toString();

            if (!t.isEmpty()) {
                if (data != null)
                    main.updateContent(originalTitle, t, d, c);
                else
                    main.createContent(t, d, c);

                AppUtilities.hideKeyboard(getContext());
                dismiss();
            } else {
                Toast.makeText(getContext(), getString(R.string.not_available), Toast.LENGTH_SHORT).show();
            }
        });

        subject.setText(main.getSubjectTitle());

        if (editMode) {
            delete_gap.setVisibility(View.VISIBLE);
            btn_delete.setVisibility(View.VISIBLE);
            originalTitle = data.getTitle();

            title.setText(originalTitle);
            description.setText(data.getDescription());
            created.setText(data.getCreatedAt());
        } else {
            created.setText(DatabaseHelper.getDateTime());
            title.requestFocus();
        }

        subject.setClickable(false);
        created.setClickable(false);
        subject.setFocusable(false);
        created.setFocusable(false);

        // Inflate the layout to use as dialog or embedded fragment
        return view;
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }
}
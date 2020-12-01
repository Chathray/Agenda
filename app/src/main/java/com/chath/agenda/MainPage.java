package com.chath.agenda;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chath.agenda.data.ContentModel;
import com.chath.agenda.data.DatabaseHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;


public class MainPage extends Fragment {

    BottomSheetBehavior sheetBehavior;
    LinearLayout bottom_sheet;

    TextView sheet_toggle, btn_spacing, btn_arrange, empty_view, btn_settings, btn_about, btn_contact;

    EditText bar_search;
    RecyclerView recyclerView;

    DatabaseHelper dataHelper;
    ContentAdapter adapter;

    MainActivity main;

    private int subject_key;

    public MainPage(int key) {
        this.subject_key = key;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.main_page, container, false);

        main = (MainActivity) getActivity();
        empty_view = view.findViewById(R.id.empty_view);

        int type = AppUtilities.getDefaultInterger(getString(R.string.key_arrange_content), getContext(), 1);
        dataHelper = new DatabaseHelper(getContext());
        adapter = new ContentAdapter(dataHelper.getAllContent(subject_key, type), this);

        detectEmptyContent();

        recyclerView = view.findViewById(R.id.subject_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(adapter, main) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();
                final ContentModel kitty = adapter.getData().get(position);

                adapter.removeItem(position);

                View snack_view = main.findViewById(android.R.id.content);
                String snack_mes = getString(R.string.item_removed);
                final Snackbar snackbar = Snackbar.make(snack_view, snack_mes, Snackbar.LENGTH_LONG);

                final Snackbar.Callback call = new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        dataHelper.deleteContent(kitty);
                        updateRecyler();
                    }
                };
                snackbar.addCallback(call);
                snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.removeCallback(call);

                        adapter.restoreItem(kitty, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        bar_search = view.findViewById(R.id.search_view);
        bar_search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    SearchingOut();
                    AppUtilities.buttonTinting(main, main.btn_search, "#505050");
                    return true;
                }
                return false;
            }
        });
        bar_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() > (bar_search.getRight() - bar_search.getCompoundDrawables()[2].getBounds().width())) {
                        bar_search.getText().clear();
                        return false;
                    }
                }
                return true;
            }
        });
        bar_search.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                adapter.getFilter().filter(c);
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

        btn_spacing = view.findViewById(R.id.bs_spacing);
        btn_spacing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectSpacingMode(true);
            }
        });

        detectSpacingMode(false);

        btn_arrange = view.findViewById(R.id.bs_arrange);
        btn_arrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectArrangeMode(true);
            }
        });

        detectArrangeMode(false);

        btn_settings = view.findViewById(R.id.bs_settings);
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                main.overridePendingTransition(0,0);
            }
        });

        btn_about = view.findViewById(R.id.bs_about);
        btn_about.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar
                        .make(view, "Agenda Version 1.0" +
                                "\nCopyright 2020 Chath. All rights reserved" +
                                "\n~" +
                                "\nDeveloped by Chath Nguyen" +
                                "\nComputer Scrience K40A" +
                                "\nQuy Nhon University" +
                                "\n~" +
                                "\nchithachnguyen@outlook.com", Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.alert_close, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });

                snackbar.setBackgroundTint(getResources().getColor(R.color.colorBackground));
                snackbar.setActionTextColor(getResources().getColor(R.color.colorOnBackground));
                snackbar.setTextColor(getResources().getColor(R.color.colorOnBackground));
                snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);

                View snackbarView = snackbar.getView();
                TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                Button active = snackbarView.findViewById(com.google.android.material.R.id.snackbar_action);
                textView.setMaxLines(10);
                textView.setLineSpacing(1.2f, 1.2f);
                textView.setGravity(Gravity.CENTER);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                active.setTextColor(Color.parseColor("#2BA9C4"));
                active.setAllCaps(false);
                snackbar.show();
            }
        });

        btn_contact = view.findViewById(R.id.bs_contact);
        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"chithachnguyen@outlook.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "AGENDA-CONTACT[" + Build.MODEL + "]");
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.send_mail_title)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), getString(R.string.email_not_installed), Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottom_sheet = view.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        sheet_toggle = view.findViewById(R.id.bottom_sheet_toggle);
        sheet_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bar_search.isShown())
                    Toast.makeText(getActivity(), R.string.not_available, Toast.LENGTH_SHORT).show();
                else if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        return view;
    }

    private void detectEmptyContent() {
        if (adapter.getItemCount() == 0)
            emptyVisiblity(View.VISIBLE);
        else
            emptyVisiblity(View.GONE);
    }


    // Function
    protected boolean OnSearch() {
        return bar_search.getVisibility() == View.VISIBLE;
    }

    public void SearchingOut() {
        bar_search.setVisibility(View.GONE);
        AppUtilities.hideKeyboard(main);
    }

    public void SearchingIn() {
        bar_search.setVisibility(View.VISIBLE);
        bar_search.requestFocus();
        AppUtilities.showKeyboard(main);
    }

    public int getContentKey() {
        return this.subject_key;
    }

    public void setContentKey(int subject_id) {
        this.subject_key = subject_id;
    }

    public String getSubjectTitle() {
        return dataHelper.getSubjectTitle(subject_key);
    }

    public void updateRecyler() {
        int sortType = AppUtilities.getDefaultInterger(getString(R.string.key_arrange_content), getContext(), 1);

        adapter.updateData(dataHelper.getAllContent(subject_key, sortType));
        adapter.notifyDataSetChanged();
        detectEmptyContent();
    }

    public void createContent(String t, String d, String c) {
        ContentModel data = new ContentModel(subject_key, t, d, c);
        long out = dataHelper.createContent(data);
        Toast.makeText(getContext(), getString(R.string.alert_created) + out, Toast.LENGTH_SHORT).show();

        updateRecyler();
    }

    public void updateContent(String ot, String t, String d, String c) {
        ContentModel data = new ContentModel(subject_key, t, d, c);
        long out = dataHelper.updateContent(ot, data);
        Toast.makeText(getContext(), getString(R.string.alert_updated) + out, Toast.LENGTH_SHORT).show();

        updateRecyler();
    }

    private void detectSpacingMode(boolean growValue) {
        int mode = AppUtilities.getDefaultInterger(getString(R.string.key_spacing_content), getContext(), 1);

        mode = AppUtilities.circleRange(1, 3, growValue ? ++mode : mode);

        switch (mode) {
            case 1:
                btn_spacing.setText(R.string.space_compact);
                break;
            case 2:
                btn_spacing.setText(R.string.space_normal);
                break;
            case 3:
                btn_spacing.setText(R.string.space_extended);
                break;
        }

        AppUtilities.setDefaults(getString(R.string.key_spacing_content), mode, getContext());

        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
    }

    private void detectArrangeMode(boolean growValue) {
        int mode = AppUtilities.getDefaultInterger(getString(R.string.key_arrange_content), getContext(), 1);

        mode = AppUtilities.circleRange(1, 4, growValue ? ++mode : mode);

        switch (mode) {
            case 1:
                btn_arrange.setText(R.string.arrange_asc);
                break;
            case 2:
                btn_arrange.setText(R.string.arrange_desc);
                break;
            case 3:
                btn_arrange.setText(R.string.arrange_newest);
                break;
            case 4:
                btn_arrange.setText(R.string.arrange_oldest);
                break;
        }

        AppUtilities.setDefaults(getString(R.string.key_arrange_content), mode, getContext());

        updateRecyler();
    }

    public void emptyVisiblity(int vis) {
        empty_view.setVisibility(vis);
    }

    public int getSpacing() {
        int mode = AppUtilities.getDefaultInterger(getString(R.string.key_spacing_content), getContext(), 1);
        switch (mode) {
            case 1:
                return AppUtilities.dpToPx(getResources().getInteger(R.integer.spacing_compact), getContext());
            case 3:
                return AppUtilities.dpToPx(getResources().getInteger(R.integer.spacing_unabridged), getContext());
        }
        return AppUtilities.dpToPx(getResources().getInteger(R.integer.spacing_normal), getContext());
    }
}
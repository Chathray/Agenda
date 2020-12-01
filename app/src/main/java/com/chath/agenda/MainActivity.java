package com.chath.agenda;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chath.agenda.data.ContentModel;
import com.chath.agenda.data.DatabaseHelper;
import com.chath.agenda.data.SubjectModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    MainPage main;
    DatabaseHelper dataHelper;
    String[] subject_list;

    Button btn_search;
    TextView btn_subject;
    Toolbar topbar;

    String currentLanguage;
    boolean largeLayout;
    Integer currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AgendaMain);

        currentTheme = AppUtilities.getDefaultInterger(getString(R.string.key_dark_theme), this, 1);
        AppCompatDelegate.setDefaultNightMode(currentTheme);

        currentLanguage = AppUtilities.getDefaultString(getString(R.string.key_lang), this, "default");
        AppUtilities.initializeLanguage(currentLanguage, this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        largeLayout = AppUtilities.isXLargeTablet(this);
        dataHelper = new DatabaseHelper(this);

        dataHelper.createSubject(new SubjectModel("Welcome to Agenda", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Khách hàng thân thiết Lens Cafe", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Danh sách hàng nhập kho tháng 1/2020", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Danh sách hàng nhập kho tháng 2/2020", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Danh sách hàng nhập kho tháng 4/2020", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Hộ gia đình phường Nguyễn Văn Cừ", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Hộ gia đình phường Ngô Mây", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Học Viên Lớp Tiếng Anh Sunday - 7PM", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Học Viên Lớp Tiếng Anh Monday - 6AM", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Sưu tầm ứng dụng Windows", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Sưu tầm ứng dụng Linux", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Danh sách quán Cafe Quy Nhơn", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Danh sách nhà hàng Quy Nhơn", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Danh sách nhà trọ Quy Nhơn", DatabaseHelper.getDateTime()));
        dataHelper.createSubject(new SubjectModel("Danh sách tiệm Photo Quy Nhơn", DatabaseHelper.getDateTime()));

        dataHelper.createContent(new ContentModel(1, "Câu hỏi thường găp", "", DatabaseHelper.getDateTime()));
        dataHelper.createContent(new ContentModel(1, "Hướng dẫn sử dụng", "", DatabaseHelper.getDateTime()));
        dataHelper.createContent(new ContentModel(1, "Lịch sử phiên bản", "", DatabaseHelper.getDateTime()));
        dataHelper.createContent(new ContentModel(1, "Nhận xét từ người dùng", "", DatabaseHelper.getDateTime()));
        dataHelper.createContent(new ContentModel(1, "Thông tin giấy phép", "", DatabaseHelper.getDateTime()));

//        dataHelper.createContent(new ContentModel(1, "", "", DatabaseHelper.getDateTime()));


        topbar = findViewById(R.id.topbar);
        btn_search = findViewById(R.id.btn_search);

        btn_subject = findViewById(R.id.btn_subject);
        btn_subject.setMaxWidth(getResources().getDisplayMetrics().widthPixels - AppUtilities.dpToPx(96, this));
        btn_subject.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onLongClick(View v) {
                Context wrapper = new ContextThemeWrapper(MainActivity.this, R.style.SubjectMenuTheme);
                PopupMenu popup = new PopupMenu(wrapper, v);
                final MenuBuilder menu = (MenuBuilder) popup.getMenu();

                menu.setGroupDividerEnabled(true);
                menu.setOptionalIconsVisible(true);
                popup.getMenuInflater().inflate(R.menu.subject_menu, menu);

                popup.show();
                return true;
            }
        });

        int sid = SmartDetectSubject(true);

        main = new MainPage(sid);
        AppUtilities.addFragment(this, main, "MP", R.id.frame_container);

        setSupportActionBar(topbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (main.sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                main.bottom_sheet.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    main.sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String lang = AppUtilities.getDefaultString(getString(R.string.key_lang), this, "default");
        if (!this.currentLanguage.equals(lang)) {
            AppUtilities.restartApp(this);
            return;
        }

        int key_theme = AppUtilities.getDefaultInterger(getString(R.string.key_dark_theme), this, 1);
        if (!this.currentTheme.equals(key_theme)) {
            AppUtilities.restartApp(this);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // App Handling
    public void SearchSide(View view) {
        if (main.OnSearch()) {
            main.SearchingOut();
            AppUtilities.buttonTinting(this, btn_search, "#505050");
        } else {
            main.SearchingIn();
            AppUtilities.buttonTinting(this, btn_search, "#E5AB09");
        }
    }

    public void SubjectPopup(View view) {
        SmartDetectSubject(false);

        int checked = (int) btn_subject.getTag(R.integer.subject_index);

        ContextThemeWrapper cw = new ContextThemeWrapper(this, R.style.SubjectDialogTheme);
        AlertDialog.Builder builder = new AlertDialog.Builder(cw);

        builder.setSingleChoiceItems(subject_list, checked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item
                btn_subject.setText(subject_list[which]);

                int sid = SmartDetectSubject(true);
                UpdateMainContent(sid);

                dialog.dismiss();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void CreateContent(View view) {
        SmartContentView(null);
    }


    // Menu Handling
    public void DoCreateSubject(MenuItem item) {
        LayoutInflater li = LayoutInflater.from(this);
        final View input = li.inflate(R.layout.input_view, null);

        AlertDialog createDialog = new MaterialAlertDialogBuilder(MainActivity.this, R.style.AgendaAlert)
                .setTitle(R.string.subject_new)
                .setView(input)
                .setPositiveButton(R.string.alert_create, (dialogInterface, i) -> {
                    EditText e = input.findViewById(R.id.text1);
                    String title = e.getText().toString();

                    long out = dataHelper.createSubject(new SubjectModel(title, DatabaseHelper.getDateTime()));
                    Toast.makeText(MainActivity.this, getString(R.string.alert_created) + out, Toast.LENGTH_SHORT).show();

                    if (out != -1) {
                        btn_subject.setText(title);
                        int sid = SmartDetectSubject(true);
                        UpdateMainContent(sid);
                    }
                })
                .setNeutralButton(getText(R.string.cancel), null)
                .create();

        createDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createDialog.show();
    }

    public void DoRenameSubject(MenuItem item) {
        LayoutInflater li = LayoutInflater.from(this);
        final View input = li.inflate(R.layout.input_view, null);

        AlertDialog renameDialog = new MaterialAlertDialogBuilder(MainActivity.this, R.style.AgendaAlert)
                .setTitle(R.string.subject_rename)
                .setView(input)
                .setPositiveButton(R.string.alert_rename, (dialogInterface, i) -> {
                    EditText e = input.findViewById(R.id.text1);
                    int sid = (int) btn_subject.getTag(R.integer.subject_key);

                    if(sid == 1) {
                        Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int out = dataHelper.updateSubject(sid, e.getText().toString());
                    Toast.makeText(MainActivity.this, getString(R.string.alert_renamed) + out, Toast.LENGTH_SHORT).show();

                    if (out != -1) {
                        btn_subject.setText(e.getText().toString());
                        SmartDetectSubject(true);
                    }
                })
                .setNeutralButton(getText(R.string.cancel), null)
                .create();
        renameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        renameDialog.show();

        EditText subject_name = input.findViewById(R.id.text1);
        subject_name.setText(btn_subject.getText());
    }

    public void DoDeleteSubject(MenuItem item) {
        new MaterialAlertDialogBuilder(MainActivity.this, R.style.SquareAlert)
                .setTitle(btn_subject.getText())
                .setMessage(R.string.perform_del_subject)
                .setPositiveButton(getString(R.string.alert_yes), (dialogInterface, i) -> {
                    int sid = (int) btn_subject.getTag(R.integer.subject_key);

                    if(sid == 1) {
                        Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean out = dataHelper.deleteSubject(sid);
                    Toast.makeText(MainActivity.this, getString(R.string.alert_deleted) + out, Toast.LENGTH_SHORT).show();

                    if (out) {
                        AgendaDefault();
                    }
                })
                .setNeutralButton(getText(R.string.cancel), null)
                .show();
    }

    // Function
    public void AgendaDefault() {
        btn_subject.setText(R.string.agenda_start);
        int sid = SmartDetectSubject(true);
        UpdateMainContent(sid);
    }

    public void SmartContentView(ContentModel data) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ContentPage newFragment = new ContentPage(main, data);

        if (largeLayout) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(data == null ? R.anim.top_enter : R.anim.swipe_enter,
                    data == null ? R.anim.top_exit : R.anim.swipe_exit)
                    .add(android.R.id.content, newFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private int SmartDetectSubject(boolean wantSID) {
        int sort = AppUtilities.getDefaultInterger(getString(R.string.key_arrange_subject), this, 1);
        ArrayList<String> als = dataHelper.getAllSubjectTitle(sort);

        String title = btn_subject.getText().toString();

        int stt = als.indexOf(title);
        int sid = dataHelper.getSubjectKey(title);

        btn_subject.setTag(R.integer.subject_index, stt);
        btn_subject.setTag(R.integer.subject_key, sid);

        subject_list = als.toArray(new String[0]);

        return wantSID ? sid : stt;
    }

    public void UpdateMainContent(int sid) {
        main.setContentKey(sid);
        main.updateRecyler();
    }
}
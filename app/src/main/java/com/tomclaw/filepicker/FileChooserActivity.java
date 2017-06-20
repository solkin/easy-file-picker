package com.tomclaw.filepicker;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.tomclaw.filepicker.files.FileAdapter;
import com.tomclaw.filepicker.files.FileItem;
import com.tomclaw.filepicker.util.AppsMenuHelper;
import com.tomclaw.filepicker.util.DirScanner;
import com.tomclaw.filepicker.util.FileHelper;
import com.tomclaw.filepicker.util.GroupMenuListener;
import com.tomclaw.filepicker.util.MainExecutor;
import com.tomclaw.filepicker.util.MenuHelper;
import com.tomclaw.filepicker.util.StringUtil;
import com.tomclaw.filepicker.util.TimeHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileChooserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_FILE_RESULT_CODE = 4;

    private TimeHelper timeHelper = new TimeHelper(this);

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_chooser_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar navHeaderToolbar = (Toolbar) navigationView.getHeaderView(0).findViewById(R.id.nav_header_toolbar);
        navHeaderToolbar.setTitle(R.string.sources_list);
        final Menu menu = navigationView.getMenu();
        navigationView.setItemIconTintList(null);

        MenuHelper menuHelper = new MenuHelper(getResources(), menu);
        menuHelper.addMenuItem(1, R.string.recent, R.drawable.history, true);
        menuHelper.addMenuItem(1, R.string.internal_storage, R.drawable.cellphone_android, false);
        Set<String> points = FileHelper.getExternalMounts();
        for (int c = 0; c < points.size(); c++) {
            menuHelper.addMenuItem(1, R.string.external_storage, R.drawable.sd, false);
        }
        menu.setGroupCheckable(1, true, true);

        menuHelper.addMenuItem(2, R.string.camera, R.drawable.camera, false);
        menuHelper.addMenuItem(2, R.string.pictures, R.drawable.image, false);
        menuHelper.addMenuItem(2, R.string.music, R.drawable.music, false);
        menuHelper.addMenuItem(2, R.string.video, R.drawable.video, false);
        menuHelper.addMenuItem(2, R.string.documents, R.drawable.file_document, false);
        menuHelper.addMenuItem(2, R.string.downloads, R.drawable.download, false);
        menu.setGroupCheckable(2, true, true);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        AppsMenuHelper.fillMenuItemMenu(this, menu, menuHelper.getIndex(), intent, PICK_FILE_RESULT_CODE);

        fileAdapter = new FileAdapter(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(fileAdapter);

        List<File> dirs = FileHelper.getExternalStorageDirs();
        DirScanner dirScanner = new DirScanner(5);
        dirScanner.scan(new DirScanner.Callback() {
            @Override
            public void onCompleted(List<File> dirs) {
                final List<File> files = FileHelper.findRecentFiles(30, dirs);
                MainExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        setFiles(files);
                    }
                });
            }
        }, dirs.toArray(new File[dirs.size()]));
    }

    private void setFiles(List<File> files) {
        List<FileItem> items = new ArrayList<>();
        for (File file : files) {
            String path = file.getAbsolutePath();
            String mimeType = FileHelper.getMimeType(path);
            String title = file.getName();
            String info = String.format("%s, %s",
                    StringUtil.formatBytes(getResources(), file.length()),
                    timeHelper.getFormattedDate(file.lastModified()));
            int icon = FileHelper.getMimeTypeResPicture(mimeType);
            items.add(new FileItem(title, info, icon, path));
        }
        fileAdapter.setFileItems(items);
        fileAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        }

        Log.d("~@~", "menu clicked: " + id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

package com.tomclaw.filepicker;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.SubMenu;
import android.view.View;
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
import com.tomclaw.filepicker.util.MainExecutor;
import com.tomclaw.filepicker.util.StringUtil;
import com.tomclaw.filepicker.util.TimeHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Set<String> points = FileHelper.getExternalMounts();
        int i = 0;
        menu.add(1, i, i++, getString(R.string.recent))
                .setIcon(R.drawable.history)
                .setOnMenuItemClickListener(new GroupMenuListener(menu));
        menu.add(1, i, i++, getString(R.string.internal_storage))
                .setIcon(R.drawable.cellphone_android)
                .setOnMenuItemClickListener(new GroupMenuListener(menu));
        points.add("");
        for (String point : points) {
            menu.add(1, i, i++, getString(R.string.external_storage))
                    .setIcon(R.drawable.sd)
                    .setOnMenuItemClickListener(new GroupMenuListener(menu));
        }
        menu.setGroupCheckable(1, true, true);
        menu.add(2, i, i++, getString(R.string.camera))
                .setIcon(R.drawable.camera)
                .setOnMenuItemClickListener(new GroupMenuListener(menu));
        menu.add(2, i, i++, getString(R.string.pictures))
                .setIcon(R.drawable.image)
                .setOnMenuItemClickListener(new GroupMenuListener(menu));
        menu.add(2, i, i++, getString(R.string.music))
                .setIcon(R.drawable.music)
                .setOnMenuItemClickListener(new GroupMenuListener(menu));
        menu.add(2, i, i++, getString(R.string.video))
                .setIcon(R.drawable.video)
                .setOnMenuItemClickListener(new GroupMenuListener(menu));
        menu.add(2, i, i++, getString(R.string.documents))
                .setIcon(R.drawable.file_document)
                .setOnMenuItemClickListener(new GroupMenuListener(menu));
        menu.add(2, i, i++, getString(R.string.downloads))
                .setIcon(R.drawable.download)
                .setOnMenuItemClickListener(new GroupMenuListener(menu));
        menu.setGroupCheckable(2, true, true);
        AppsMenuHelper.fillMenuItemMenu(FileChooserActivity.this, menu, i, intent, PICK_FILE_RESULT_CODE);

        fileAdapter = new FileAdapter(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(fileAdapter);

        File[] dirsArray = Environment.getExternalStorageDirectory().listFiles();
        List<File> dirs = new ArrayList<>();
        for (File file : dirsArray) {
            if (file.isDirectory()) {
                String name = file.getName();
                if (file.isHidden() || name.equals("Android")) {
                    continue;
                }
                dirs.add(file);
            }
        }
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

    private class GroupMenuListener implements MenuItem.OnMenuItemClickListener {

        private Menu menu;

        public GroupMenuListener(Menu menu) {
            this.menu = menu;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            for (int c = 0; c < menu.size(); c++) {
                MenuItem item = menu.getItem(c);
                if (item.isCheckable()) {
                    item.getIcon().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP));
                }
            }
            menuItem.getIcon().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.primary_color), PorterDuff.Mode.SRC_ATOP));
            return false;
        }
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

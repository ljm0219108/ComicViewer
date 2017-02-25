package com.example.ljm.comicviewer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FileAdapter.OnItemClickListener, Comparator<File> {

    private static final int REQUEST_READ_STORAGE = 1;

    private Toolbar toolbar;
    private File currDirectory;
    private String initPath;
    private List<File> files;
    private FileAdapter fileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currDirectory = Environment.getExternalStorageDirectory();
        initPath = currDirectory.getPath();
        toolbar.setSubtitle(currDirectory.getPath());
        initUI();

        requestPermission();
    }

    private void initUI() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        files = new ArrayList<>();
        fileAdapter = new FileAdapter(files);
        fileAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(fileAdapter);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, REQUEST_READ_STORAGE);
        } else {
            updateData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_STORAGE) {
            boolean granted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }
            if (granted) {
                updateData();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void updateData() {
        files.clear();
        File[] fileList = currDirectory.listFiles();
        Arrays.sort(fileList, this);
        Collections.addAll(files, fileList);
        fileAdapter.notifyDataSetChanged();
    }

    private void updateCurrDirectory(File file) {
        currDirectory = file;
        toolbar.setSubtitle(currDirectory.getPath());
        updateData();
    }

    @Override
    public int compare(File file1, File file2) {
        if (file1.isDirectory()) {
            if (file2.isDirectory()) {
                return file1.compareTo(file2);
            } else {
                return -1;
            }
        } else {
            if (file2.isDirectory()) {
                return 1;
            } else {
                return file1.compareTo(file2);
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currDirectory.getPath().equals(initPath)) {
                return super.onKeyUp(keyCode, event);
            } else {
                updateCurrDirectory(currDirectory.getParentFile());
                return true;
            }
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, int position, Object item) {
        File file = (File) item;
        if (file.isDirectory()) {
            updateCurrDirectory(file);
        } else {
            Toast.makeText(this, file.getName(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, CurlActivity.class);
            intent.putExtra("file", file);
            startActivity(intent);
        }
    }
}

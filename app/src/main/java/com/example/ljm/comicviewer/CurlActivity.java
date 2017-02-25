/*
   Copyright 2012 Harri Smatt
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.example.ljm.comicviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Simple Activity for curl testing.
 *
 * @author harism
 */
public class CurlActivity extends AppCompatActivity {

    // Bitmap resources.
    private CurlView mCurlView;
    private static int index;
    private List<File> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curl);

        initData();

        initUI();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent.hasExtra("file")) {
            File file = (File) intent.getSerializableExtra("file");
            File path = file.getParentFile();
            File[] imgs = path.listFiles();
            Arrays.sort(imgs);
            files = Arrays.asList(imgs);
            index = files.indexOf(file);
        }
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurlView = (CurlView) findViewById(R.id.content_curl);
        mCurlView.setPageProvider(new PageProvider());
        mCurlView.setSizeChangedObserver(new SizeChangedObserver());
        mCurlView.setCurrentIndex(index);
        mCurlView.setBackgroundColor(0xFF202830);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCurlView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurlView.onResume();
    }

    /*@Override
    public Object onRetainNonConfigurationInstance() {
        return mCurlView.getCurrentIndex();
    }*/

    @Override
    protected void onStop() {
        Toast.makeText(this, files.get(index).getName(), Toast.LENGTH_LONG).show();
        super.onStop();
    }

    /**
     * Bitmap provider.
     */
    private class PageProvider implements CurlView.PageProvider {

        @Override
        public int getPageCount() {
            return files.size();
        }

        private Bitmap loadBitmap(int width, int height, int index) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            bitmap.eraseColor(0xFFFFFFFF);
            Canvas canvas = new Canvas(bitmap);
            Drawable drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(files.get(CurlActivity.index = index).getPath()));

            Rect rect = new Rect(0, 0, width, height);

            int imageWidth = rect.width();
            int imageHeight = imageWidth * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
            if (imageHeight > rect.height()) {
                imageHeight = rect.height();
                imageWidth = imageHeight * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
            }

            rect.left += ((rect.width() - imageWidth) / 2);
            rect.right = rect.left + imageWidth;
            rect.top += ((rect.height() - imageHeight) / 2);
            rect.bottom = rect.top + imageHeight;

            Paint p = new Paint();
            p.setColor(0xFFC0C0C0);
            canvas.drawRect(rect, p);

            drawable.setBounds(rect);
            drawable.draw(canvas);

            return bitmap;
        }

        @Override
        public void updatePage(CurlPage page, int width, int height, int index) {
            Bitmap front = loadBitmap(width, height, index);
            page.setTexture(front, CurlPage.SIDE_FRONT);
        }
    }

    /**
     * CurlView size changed observer.
     */
    private class SizeChangedObserver implements CurlView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int width, int height) {
            if (width > height) {
                mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
            } else {
                mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
            }
        }
    }
}

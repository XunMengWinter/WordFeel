package top.wefor.wordfeel.ui;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.wefor.wordfeel.App;
import top.wefor.wordfeel.Constants;
import top.wefor.wordfeel.R;
import top.wefor.wordfeel.model.entity.ImageEntity;
import top.wefor.wordfeel.utils.FileUtil;

/**
 * Created on 2016/12/4.
 *
 * @author ice
 * @GitHub https://github.com/XunMengWinter
 */
public class ImagesActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    private String mImageFolderPath;

    private List<ImageEntity> mImageList = new ArrayList<>();
    private ImageAdapter mImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            // 默认左上角按钮可以点击
            getSupportActionBar().setHomeButtonEnabled(true);
            // 默认显示左上角返回按钮
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            Logger.i("getSupportActionBar is null");
        }

        mImageAdapter = new ImageAdapter(this, mImageList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(mImageAdapter);

        getImagesThenShow();
    }

    /**
     * 获取图片，并显示
     * 优先获取指定路径的本地图片，如果没有，则获取网络图片的url.
     */
    private void getImagesThenShow() {
        mImageFolderPath = Constants.IMAGE_DIR;
        Logger.i(mImageFolderPath);

        File file = new File(mImageFolderPath);
        if (!file.exists()) {
            Logger.i("file not exists, I will make this dir");
            FileUtil.makeDirs(mImageFolderPath);
        }

        Observable
                .create((Observable.OnSubscribe<List<File>>) subscriber -> {
                    List<File> fileList = FileUtil.getFileList(mImageFolderPath);
                    subscriber.onNext(fileList);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(files -> {
                    mImageList.clear();
                    if (files != null && !files.isEmpty()) {
                        Logger.i("get image from local file");
                        for (File imageFile : files) {
                            ImageEntity imageEntity = new ImageEntity();
                            imageEntity.localPath = imageFile.getAbsolutePath();
                            imageEntity.name = imageFile.getName();
                            mImageList.add(imageEntity);
                        }
                    } else {
                        Logger.i("get image from image_urls");
                        String imageUrls = getString(R.string.image_urls);
                        imageUrls = imageUrls.replace(" ", "");
                        Logger.i(imageUrls);
                        String[] imageUrlArray = imageUrls.split("https://");
                        for (String imageUrl : imageUrlArray) {
                            if (TextUtils.isEmpty(imageUrl)) continue;
                            imageUrl = "https://" + imageUrl;
                            ImageEntity imageEntity = new ImageEntity();
                            imageEntity.url = imageUrl;
                            mImageList.add(imageEntity);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(files -> mImageAdapter.notifyDataSetChanged());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 左上角返回按钮，点击返回
            case android.R.id.home:
                onBackPressed();
                return true;
            // 打开图片文件夹
            case R.id.action_open_folder:
                FileUtil.openFolder(this, Constants.IMAGE_DIR);
                return true;
            // 删除图片文件夹
            case R.id.action_delete_folder:
                new AlertDialog.Builder(this)
                        .setMessage("Are you want to delete the image folder?")
                        .setPositiveButton("yes", (dialogInterface, i) -> {
                            FileUtil.deleteFolder(Constants.IMAGE_DIR);
                            App.showToast("Image folder was deleted.");
                        })
                        .setNegativeButton("no", null)
                        .create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_images, menu);
        return true;
    }
}

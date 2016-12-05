package top.wefor.wordfeel.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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

public class ImageAdapter extends RecyclerView.Adapter {

    public interface OnAdapterItemClickListener {
        void onItemClick(int position);
    }

    private Context mContext;
    private List<ImageEntity> mList;
    private OnAdapterItemClickListener mOnAdapterItemClickListener;

    // <String,Integer>表示<图片链接，重试次数>
    private HashMap<String, Integer> mRetryMap = new HashMap<>();
    // 图片加载失败，最多重新加载的次数。
    private int mMaxRetryTimes = 5;
    // 图片加载失败后重新加载的间隔。
    private int mRetryInterval = 3000;

    public ImageAdapter(Context context, List<ImageEntity> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        ImageEntity imageEntity = mList.get(position);
        if (imageEntity == null) return;

        // 图片加载优先级 本地>网络图片>默认图片。
        if (!TextUtils.isEmpty(imageEntity.localPath)) {
            Uri uri = Uri.fromFile(new File(imageEntity.localPath));
            Picasso.with(mContext).load(uri).centerCrop()
                    .resize(360, 640)
                    .error(R.mipmap.img_default)
                    .into(myViewHolder.mImageView);
        } else if (!TextUtils.isEmpty(imageEntity.url)) {
            loadImageFromUrl(position, myViewHolder.mImageView);
        } else {
            Picasso.with(mContext).load(R.mipmap.img_default).centerCrop()
                    .resize(360, 640)
                    .into(myViewHolder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    /**
     * 加载图片并保存至本地。
     * 未对有无网络进行判断，一切交给Picasso，这样无论是无网络还是加载失败都会回调onError()，便于统一处理重试。
     */
    private void loadImageFromUrl(int position, ImageView imageView) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Logger.i("onBitmapLoaded");
                String imageUrl = mList.get(position).url;
                String[] fileNameSplit = imageUrl.split("/");
                String fileName = fileNameSplit[fileNameSplit.length - 1];
                // 在io线程保存图片
                Observable
                        .create((Observable.OnSubscribe<File>) subscriber -> {
                            File file = FileUtil.saveBitmap(bitmap, fileName, Constants.IMAGE_DIR);
                            subscriber.onNext(file);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(file -> {

                        });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        imageView.setTag(target);

        final String imageUrl = mList.get(position).url;
        Picasso.with(mContext).load(imageUrl).centerCrop()
                .resize(360, 640)
                .error(R.mipmap.img_default)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // 先加载，再保存图片。巧妙利用Picasso缓存，避免一些列表下载图片的问题。
                        Picasso.with(mContext).load(imageUrl)
                                .into((Target) imageView.getTag());
                    }

                    @Override
                    public void onError() {
                        Logger.i("onError");
                        if (!mRetryMap.containsKey(imageUrl)) {
                            mRetryMap.put(imageUrl, 0);
                        }
                        int currentRetryTimes = mRetryMap.get(imageUrl);
                        if (currentRetryTimes < mMaxRetryTimes) {
                            imageView.postDelayed(() -> {
                                if (mContext == null)
                                    return;
                                Activity activity = (Activity) mContext;
                                if (activity.isFinishing())
                                    return;
                                //重新加载
                                loadImageFromUrl(position, imageView);
                                Logger.i("retry");
                            }, mRetryInterval);
                            mRetryMap.put(imageUrl, currentRetryTimes + 1);
                        }
                    }
                });
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView) ImageView mImageView;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(view1 -> {
                if (mOnAdapterItemClickListener != null)
                    mOnAdapterItemClickListener.onItemClick(getAdapterPosition());
            });
        }
    }
}

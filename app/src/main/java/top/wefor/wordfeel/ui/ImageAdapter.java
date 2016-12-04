package top.wefor.wordfeel.ui;

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
import top.wefor.wordfeel.utils.NetworkUtil;

/**
 * Created on 2016/12/4.
 *
 * @author ice
 */

public class ImageAdapter extends RecyclerView.Adapter {

    public interface OnAdapterItemClickListener {
        void onItemClick(int position);
    }

    private Context mContext;
    private List<ImageEntity> mList;
    private OnAdapterItemClickListener mOnAdapterItemClickListener;

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
        } else if (NetworkUtil.isNetworkConnected() && !TextUtils.isEmpty(imageEntity.url)) {
            initPicasso(position, myViewHolder.mImageView);
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

    private void initPicasso(int position, ImageView imageView) {
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

        Picasso.with(mContext).load(mList.get(position).url).centerCrop()
                .resize(360, 640)
                .error(R.mipmap.img_default)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // 先加载，再保存图片。巧妙利用Picasso缓存，避免一些列表下载图片的问题。
                        Picasso.with(mContext).load(mList.get(position).url)
                                .into((Target) imageView.getTag());
                    }

                    @Override
                    public void onError() {

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

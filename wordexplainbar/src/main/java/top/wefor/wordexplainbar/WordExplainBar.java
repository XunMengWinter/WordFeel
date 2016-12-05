package top.wefor.wordexplainbar;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created on 2016/12/3.
 * <p>
 * 自定义控件：查词框。
 * <p>
 *
 * @author ice
 * @GitHub https://github.com/XunMengWinter
 */

public class WordExplainBar extends LinearLayout {

    //公开控件，最大限度自定义。结合编辑器的自动补全，.m可筛选出所有控件。
    public LinearLayout mExplainLayout;
    public AppCompatImageButton mExplainShrinkIb;  //收起按钮
    public TextView mExplainWordTv;    //用于显示单词
    public TextView mExplainPhoneticsTv;   //用于显示音标
    public TextView mExplainExplainTv; //用于显示解释
    public AppCompatImageButton mExplainAudioIb;   //音频按钮，点击后根据audioUrl发音。

    private WordExplainEntity mWordExplainEntity;
    private OnHideListener mOnHideListener;
    private boolean isVisible = true;


    public WordExplainBar(Context context) {
        this(context, null);
    }

    public WordExplainBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WordExplainBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.lay_word_explain, this);
        mExplainLayout = (LinearLayout) findViewById(R.id.explain_layout);
        mExplainWordTv = (TextView) findViewById(R.id.explain_word_tv);
        mExplainPhoneticsTv = (TextView) findViewById(R.id.explain_phonetics_tv);
        mExplainExplainTv = (TextView) findViewById(R.id.explain_explain_tv);

        initClickEvent();
    }

    private void initClickEvent() {
        mExplainShrinkIb = (AppCompatImageButton) findViewById(R.id.explain_shrink_ib);
        mExplainAudioIb = (AppCompatImageButton) findViewById(R.id.explain_audio_ib);

        if (mExplainShrinkIb != null)
            mExplainShrinkIb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    hide();
                }
            });

        if (mExplainAudioIb != null)
            mExplainAudioIb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mWordExplainEntity != null && mWordExplainEntity.audioUrl != null)
                        playAudio(mWordExplainEntity.audioUrl);
                }
            });
    }

    /* mExplainAudioIb 出现过空指针，暂时这样解决*/
    private void ensureInitClickEvent() {
        if (mExplainShrinkIb != null && mExplainAudioIb != null) return;
        initClickEvent();
    }

    /*音频播放*/
    private void playAudio(String audioUrl) {
        try {
            mExplainAudioIb.setEnabled(false);
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(audioUrl);
            player.prepare();
            player.start();
            player.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mExplainAudioIb.setEnabled(true);
                        }
                    });
        } catch (Exception e) {
            mExplainAudioIb.setEnabled(true);
        }
    }

    /*显示该控件，并根据传入实体更新内容*/
    public void show(WordExplainEntity wordExplainEntity) {
        mWordExplainEntity = wordExplainEntity;
        mExplainWordTv.setText(mWordExplainEntity.word);
        mExplainPhoneticsTv.setText(mWordExplainEntity.phonetics);
        mExplainExplainTv.setText(mWordExplainEntity.explain);
        mExplainAudioIb.setEnabled(true);
        show();
    }

    /*显示该控件*/
    public void show() {
        isVisible = true;
        ensureInitClickEvent();
        mExplainLayout.animate().y(0).withStartAction(new Runnable() {
            @Override
            public void run() {
                mExplainLayout.setVisibility(VISIBLE);
            }
        });
    }

    /*隐藏该控件,带隐藏动画*/
    public void hide() {
        hide(true);
    }

    /**
     * 隐藏该控件
     *
     * @param isShowAnimation 是否显示隐藏动画
     */
    public void hide(boolean isShowAnimation) {
        if (isShowAnimation)
            mExplainLayout.animate().y(mExplainLayout.getHeight()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mExplainLayout.setVisibility(GONE);
                }
            });
        else
            mExplainLayout.setVisibility(GONE);

        isVisible = false;
        if (mOnHideListener != null)
            mOnHideListener.onHide();
    }

    public boolean isVisible() {
        return isVisible;
    }

    /*为该控件设置隐藏监听器*/
    public void setOnHideListener(OnHideListener onHideListener) {
        mOnHideListener = onHideListener;
    }

    public static class WordExplainEntity {
        // 单词、音标、解释、音频Url
        public String word, phonetics, explain, audioUrl;
    }

    public interface OnHideListener {
        void onHide();
    }

}

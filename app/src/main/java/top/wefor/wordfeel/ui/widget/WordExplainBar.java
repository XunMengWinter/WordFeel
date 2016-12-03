package top.wefor.wordfeel.ui.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import top.wefor.wordfeel.R;

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

    LinearLayout mExplainLayout;
    AppCompatImageButton mExplainShrinkIb;  //收起按钮
    TextView mExplainWordTv;    //用于显示单词
    TextView mExplainPhoneticsTv;   //用于显示音标
    TextView mExplainExplainTv; //用于显示解释
    AppCompatImageButton mExplainAudioIb;   //音频按钮，点击后根据audioUrl发音。

    private WordExplainEntity mWordExplainEntity;
    private OnHideListener mOnHideListener;


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
            mExplainShrinkIb.setOnClickListener(view -> hide());

        if (mExplainAudioIb != null)
            mExplainAudioIb.setOnClickListener(view -> {
                if (mWordExplainEntity != null && mWordExplainEntity.audioUrl != null)
                    playAudio(mWordExplainEntity.audioUrl);
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
            player.setOnCompletionListener(mediaPlayer -> mExplainAudioIb.setEnabled(true));
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
        show();
    }

    /*显示该控件*/
    public void show() {
        ensureInitClickEvent();
        mExplainLayout.animate().scaleY(1).withStartAction(() -> mExplainLayout.setVisibility(VISIBLE));
    }

    /*隐藏该控件*/
    public void hide() {
        mExplainLayout.animate().scaleY(0).withEndAction(() -> mExplainLayout.setVisibility(GONE));
        if (mOnHideListener != null)
            mOnHideListener.onHide();
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

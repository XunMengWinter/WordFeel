package top.wefor.wordfeel.WordTextView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import top.wefor.wordfeel.R;

/**
 * <p>
 * Desc:A TextView that can get every word in content.
 * </p>
 *
 * @author _SOLID
 * @感谢开源 https://github.com/burgessjp/GetWordTextView
 * Created by ice at 16/12/3
 */

public class WordTextView extends AppCompatTextView {

    private CharSequence mText;
    private BufferType mBufferType;

    private OnWordClickListener mOnWordClickListener;
    private SpannableString mSpannableString;

    private BackgroundColorSpan mSelectedBackSpan;
    private ForegroundColorSpan mSelectedForeSpan;

    private int highlightColor;
    private String highlightText;
    private int selectedColor;
    private int language;//0:english,1:chinese

    public WordTextView(Context context) {
        this(context, null);
    }

    public WordTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WordTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WordTextView);
        highlightColor = ta.getColor(R.styleable.WordTextView_highlightColor, Color.RED);
        highlightText = ta.getString(R.styleable.WordTextView_highlightText);
        selectedColor = ta.getColor(R.styleable.WordTextView_selectedColor, Color.BLUE);
        language = ta.getInt(R.styleable.WordTextView_language, 0);
        ta.recycle();
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        this.mText = text;
        mBufferType = type;
        setHighlightColor(Color.TRANSPARENT);
        setMovementMethod(LinkMovementMethod.getInstance());
        setText();
    }

    private void setText() {
        mSpannableString = new SpannableString(mText);
        //set highlight text
        setHighLightSpan(mSpannableString);
        //separate word
        if (language == 0) {//deal english
            dealEnglish();
        } else {//deal chinese
            dealChinese();
        }
        super.setText(mSpannableString, mBufferType);

    }

    private void dealChinese() {
        for (int i = 0; i < mText.length(); i++) {
            char ch = mText.charAt(i);
            if (Utils.isChinese(ch)) {
                mSpannableString.setSpan(getClickableSpan(), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private void dealEnglish() {
        List<WordInfo> wordInfoList = Utils.getEnglishWordIndices(mText.toString());
        for (WordInfo wordInfo : wordInfoList) {
            mSpannableString.setSpan(getClickableSpan(), wordInfo.getStart(), wordInfo.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
    }


    private void setHighLightSpan(SpannableString spannableString) {
        if (TextUtils.isEmpty(highlightText)) {
            return;
        }
        int hIndex = mText.toString().indexOf(highlightText);
        while (hIndex != -1) {
            spannableString.setSpan(new ForegroundColorSpan(highlightColor), hIndex, hIndex + highlightText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            hIndex += highlightText.length();
            hIndex = mText.toString().indexOf(highlightText, hIndex);
        }
    }

    private void setSelectedSpan(AppCompatTextView tv) {
        if (mSelectedBackSpan == null || mSelectedForeSpan == null) {
            mSelectedBackSpan = new BackgroundColorSpan(selectedColor);
            mSelectedForeSpan = new ForegroundColorSpan(Color.WHITE);
        } else {
            mSpannableString.removeSpan(mSelectedBackSpan);
            mSpannableString.removeSpan(mSelectedForeSpan);
        }
        mSpannableString.setSpan(mSelectedBackSpan, tv.getSelectionStart(), tv.getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSpannableString.setSpan(mSelectedForeSpan, tv.getSelectionStart(), tv.getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordTextView.super.setText(mSpannableString, mBufferType);
    }

    public void dismissSelected() {
        mSpannableString.removeSpan(mSelectedBackSpan);
        mSpannableString.removeSpan(mSelectedForeSpan);
        WordTextView.super.setText(mSpannableString, mBufferType);
    }

    private ClickableSpan getClickableSpan() {
        return new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                AppCompatTextView tv = (AppCompatTextView) widget;
                String word = tv
                        .getText()
                        .subSequence(tv.getSelectionStart(),
                                tv.getSelectionEnd()).toString();
                setSelectedSpan(tv);

                if (mOnWordClickListener != null) {
                    mOnWordClickListener.onClick(word);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
            }
        };
    }


    public void setOnWordClickListener(OnWordClickListener listener) {
        this.mOnWordClickListener = listener;
    }

    public void setHighLightText(String text) {
        highlightText = text;
    }

    public void setHighLightColor(int color) {
        highlightColor = color;
    }

    public interface OnWordClickListener {
        void onClick(String word);
    }
}

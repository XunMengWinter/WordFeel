package top.wefor.wordfeel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.wefor.wordfeel.R;
import top.wefor.wordfeel.WordTextView.WordTextView;
import top.wefor.wordfeel.model.entity.WordEntity;
import top.wefor.wordfeel.model.result.WordResult;
import top.wefor.wordfeel.net.HttpObserver;
import top.wefor.wordfeel.net.MainApi;
import top.wefor.wordfeel.ui.widget.WordExplainBar;

/**
 * Created on 2016/12/3.
 *
 * @author ice
 * @GitHub https://github.com/XunMengWinter
 */
public class ScrollingActivity extends AppCompatActivity {

    MainApi mMainApi = new MainApi();

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar) AppBarLayout mAppBar;
    @BindView(R.id.article_wtv) WordTextView mArticleWtv;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.wordExplainBar) WordExplainBar mWordExplainBar;

    private WordExplainBar.WordExplainEntity mWordExplainEntity = new WordExplainBar.WordExplainEntity();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mArticleWtv.setOnWordClickListener(this::getWordExplain);

        mArticleWtv.setText(getString(R.string.shanbey_english_text));

        mFab.setOnClickListener(view -> sendEmail(getString(R.string.winters_email)));
        mWordExplainBar.setOnHideListener(() -> mArticleWtv.dismissSelected());
        mWordExplainBar.hide();
    }

    /*获取单词的解释*/
    private void getWordExplain(String word) {
        mMainApi.getWord(word).subscribe(new HttpObserver<WordResult>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(@NonNull WordResult model) {
                WordEntity wordEntity = model.wordEntity;
                if (wordEntity == null)
                    return;
                mWordExplainEntity.word = wordEntity.content;
                mWordExplainEntity.phonetics = "/" + wordEntity.pronunciation + "/";
                mWordExplainEntity.explain = wordEntity.definition;
                mWordExplainEntity.audioUrl = wordEntity.audio;

                mWordExplainBar.show(mWordExplainEntity);
            }
        });
    }

    /*给指定邮箱发送邮件*/
    private void sendEmail(@NonNull String emailAddress) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});

        intent.putExtra(Intent.EXTRA_SUBJECT, "Hello Ice");
        intent.putExtra(Intent.EXTRA_TEXT, "Hi, Winter. I just want to tell you ...");

        startActivity(Intent.createChooser(intent, "Send Email"));
    }

}

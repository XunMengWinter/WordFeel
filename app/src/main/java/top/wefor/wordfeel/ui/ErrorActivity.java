package top.wefor.wordfeel.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.wefor.wordfeel.App;
import top.wefor.wordfeel.R;
import top.wefor.wordfeel.ui.widget.crazycat.Playground;

/**
 * Created on 2016/11/12.
 *
 * @author ice
 * @GitHub https://github.com/XunMengWinter
 */
public class ErrorActivity extends AppCompatActivity {

    @BindView(R.id.game_layout) FrameLayout mGameLayout;
    @BindView(R.id.error_btn) AppCompatButton mErrorBtn;
    @BindView(R.id.restartBtn_btn) AppCompatButton mRestartBtnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        ButterKnife.bind(this);

        mGameLayout.addView(new Playground(this, new Playground.ResultCallback() {
            @Override
            public void lose() {
                App.showToast("bug跑掉了T_T");
                mErrorBtn.setText("bug跑掉了～～再玩一次");
            }

            @Override
            public void win() {
                App.showToast("恭喜你抓到了bug ^^～");
                mErrorBtn.setText("成功捕获到bug～再玩一次");
            }
        }));

        mRestartBtnBtn.setOnClickListener(view -> restart(ErrorActivity.this));
    }


    /**
     * 关闭当前Activity,重新启动app
     */
    public static void restart(Activity activity) {
        Intent launchIntent = activity.getApplication().getPackageManager().getLaunchIntentForPackage(activity.getPackageName());
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(launchIntent);
        }
        activity.finish();
    }
}

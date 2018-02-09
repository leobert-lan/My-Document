package com.lht.creationspace.test.anim;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.daimajia.androidanimations.library.YoYo;

public class TestAnimActivity extends Activity {

    private ListView mListView;
    private EffectAdapter mAdapter;
    private View mTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test_anim);
//        mListView = (ListView)findViewById(R.id.list_items);
//        mTarget = findViewById(R.id.hello_world);
//        mAdapter = new EffectAdapter(this);
//        mListView.setAdapter(mAdapter);
//        rope = YoYo.with(Techniques.FadeIn).duration(1000).playOn(mTarget);// after start,just click mTarget view, rope is not init
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Techniques technique = (Techniques)view.getTag();
//                rope =  YoYo.with(technique)
//                            .duration(1200)
//                            .interpolate(new AccelerateDecelerateInterpolator())
//                            .withListener(new Animator.AnimatorListener() {
//                                @Override
//                                public void onAnimationStart(Animator animation) {
//
//                                }
//
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//
//                                }
//
//                                @Override
//                                public void onAnimationCancel(Animator animation) {
//                                    Toast.makeText(TestAnimActivity.this, "canceled", Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void onAnimationRepeat(Animator animation) {
//
//                                }
//                            })
//                            .playOn(mTarget);
//            }
//        });
//        findViewById(R.id.hello_world).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (rope != null) {
//                    rope.stop(true);
//                }
//            }
//        });
    }
    private YoYo.YoYoString rope;


}

package com.lht.creationspace.base.anim;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

public class OptAnimationLoader {

    public static Animation loadAnimation(Context context, int id)
            throws Resources.NotFoundException {

        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getAnimation(id);
            return createAnimationFromXml(context, parser);
        } catch (XmlPullParserException | IOException ex) {
            Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" +
                    Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } finally {
            if (parser != null) parser.close();
        }
    }

    private static Animation createAnimationFromXml(Context c, XmlPullParser parser)
            throws XmlPullParserException, IOException {

        return createAnimationFromXml(c, parser, null, Xml.asAttributeSet(parser));
    }

    private static final HashMap<String, AnimCreator> normalAnimCreators
            = new HashMap<>();

    private static final String ANIM_SET = "set";
    private static final String ANIM_ALPHA = "alpha";
    private static final String ANIM_SCALE = "scale";
    private static final String ANIM_ROTATE = "rotate";
    private static final String ANIM_TRANSLATE = "translate";

    static {
        normalAnimCreators.put(ANIM_SET, AnimCreator.setCreator);
        normalAnimCreators.put(ANIM_ALPHA, AnimCreator.alphaCreator);
        normalAnimCreators.put(ANIM_ROTATE, AnimCreator.rotateCreator);
        normalAnimCreators.put(ANIM_SCALE, AnimCreator.scaleCreator);
        normalAnimCreators.put(ANIM_TRANSLATE, AnimCreator.translateCreator);
    }


    private static Animation createAnimationFromXml(Context c,
                                                    XmlPullParser parser,
                                                    AnimationSet parent,
                                                    AttributeSet attrs)
            throws XmlPullParserException, IOException {

        Animation anim = null;

        // Make sure we are on a start tag.
        int type;
        int depth = parser.getDepth();

        while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
                && type != XmlPullParser.END_DOCUMENT) {

            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (normalAnimCreators.containsKey(name)) {
                anim = normalAnimCreators.get(name).create(c, attrs);
                if (name.equals(ANIM_SET))
                    createAnimationFromXml(c, parser, (AnimationSet) anim, attrs);
            } else {
                try {
                    anim = (Animation) Class.forName(name).getConstructor(Context.class, AttributeSet.class).newInstance(c, attrs);
                } catch (Exception te) {
                    throw new RuntimeException("Unknown animation name: " + parser.getName() + " error:" + te.getMessage());
                }
            }

            if (parent != null) {
                parent.addAnimation(anim);
            }
        }
        return anim;
    }

    private interface AnimCreator {
        Animation create(Context c,
                         AttributeSet attrs);

        //***********************//
        AnimCreator setCreator = new AnimCreator() {
            @Override
            public Animation create(Context c, AttributeSet attrs) {
                return new AnimationSet(c, attrs);
            }
        };

        AnimCreator alphaCreator = new AnimCreator() {
            @Override
            public Animation create(Context c, AttributeSet attrs) {
                return new AlphaAnimation(c, attrs);
            }
        };

        AnimCreator scaleCreator = new AnimCreator() {
            @Override
            public Animation create(Context c, AttributeSet attrs) {
                return new ScaleAnimation(c, attrs);
            }
        };

        AnimCreator rotateCreator = new AnimCreator() {
            @Override
            public Animation create(Context c, AttributeSet attrs) {
                return new RotateAnimation(c, attrs);
            }
        };

        AnimCreator translateCreator = new AnimCreator() {
            @Override
            public Animation create(Context c, AttributeSet attrs) {
                return new TranslateAnimation(c, attrs);
            }
        };
    }
}

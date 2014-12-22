package com.rndapp.mtamap;

import android.content.Context;
import android.util.AttributeSet;
import com.rndapp.subway_lib.TouchImageView;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 9/29/13
 * Time: 2:35 PM
 */
public class MtaTouchImageView extends TouchImageView{

    public MtaTouchImageView(Context context) {
        super(context);
    }

    public MtaTouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected float getDoubleTapScaleFactor() {
        return 6.f;
    }
}

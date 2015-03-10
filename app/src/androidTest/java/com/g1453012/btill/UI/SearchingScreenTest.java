package com.g1453012.btill.UI;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.g1453012.btill.R;

/**
 * Created by luke on 10/03/2015.
 */
public class SearchingScreenTest extends ActivityInstrumentationTestCase2<AppStartup>{

    public SearchingScreenTest(){
        super(AppStartup.class);
    }
    @Override
    public void setUp() throws Exception {
        super.setUp();

    }

    public void testLogo(){
        ImageView logo = (ImageView)getActivity().findViewById(R.id.appIcon);
        assertEquals( getActivity().getResources().getDrawable(R.drawable.home_icon),logo.getDrawable());
    }

    public void testText(){
        TextView text = (TextView)getActivity().findViewById(R.id.textView);
        assertEquals(getActivity().getText(R.string.searching), text.getText());

    }

    public void testProgressBar(){
        ProgressBar bar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
        assertEquals(true, bar.isIndeterminate());
    }

}

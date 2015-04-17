package com.g1453012.btill.UI.HomeScreenFragments.Order.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.g1453012.btill.R;

/**
 * Created by dlmiddlecote on 16/04/15.
 */
public class WelcomeDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "WelcomeDialogFragment";

    private String restaurantName;

    public static WelcomeDialogFragment newInstance(String restaurant) {
        WelcomeDialogFragment welcomeDialogFragment = new WelcomeDialogFragment();
        welcomeDialogFragment.setRestaurantName(restaurant);
        return welcomeDialogFragment;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog mWelcomeDialog = new Dialog(getActivity());
        mWelcomeDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mWelcomeDialog.setContentView(R.layout.welcome_dialog);

        TextView restaurant = (TextView) mWelcomeDialog.findViewById(R.id.welcomeSubTitle);
        restaurant.setText("Welcome to \'" + restaurantName + "\'.");

        Button okButton = (Button) mWelcomeDialog.findViewById(R.id.welcomeOKButton);
        okButton.setOnClickListener(this);

        Button walkthroughButton = (Button) mWelcomeDialog.findViewById(R.id.welcomeWalkthroughButton);
        walkthroughButton.setOnClickListener(this);

        return mWelcomeDialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcomeOKButton:
                dismiss();
                break;
            case R.id.welcomeWalkthroughButton:
                dismiss();
                break;
        }
    }
}

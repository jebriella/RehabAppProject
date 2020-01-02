package com.example.rehabappproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class StatusBarHandler extends AppCompatActivity {
    ImageView userImage;
    TextView userView;

    Context context2 = GlobalApplication.getAppContext();

    StatusBarHandler(ImageView imageView, TextView textView, boolean showUserText){
        this.userImage = imageView;
        this.userView = textView;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //handle user image
        handleImage(user);

        //handle text in status bar
        if (showUserText==true) {
        textView.setText(new StringBuilder().append("Welcome back, ").append(user.getDisplayName()).append("!").toString());
        } else {
            textView.setText("");
        }
    }

    private void handleImage(FirebaseUser user){
        Picasso.with(context2)
                .load(user.getPhotoUrl())
                .into(new Target() {

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context2.getResources(), bitmap);

                        circularBitmapDrawable.setCircular(true);
                        userImage.setImageDrawable(circularBitmapDrawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }
                });
    }

}

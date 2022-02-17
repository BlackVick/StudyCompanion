package esw.peeplo.studentstudycom.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import esw.peeplo.studentstudycom.R;

public class BindingAdapters {

    @BindingAdapter("android:imageURL")
    public static void setImageURL(ImageView imageView, String URL) {
        try {

            if (URL == null || TextUtils.isEmpty(URL)){

                imageView.setImageResource(R.drawable.image_placeholder);

            } else {

                imageView.setAlpha(0f);
                Picasso.get()
                        .load(URL)
                        .noFade()
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.image_placeholder)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                imageView.animate()
                                        .setDuration(700)
                                        .alpha(1f)
                                        .start();
                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(URL)
                                        .noFade()
                                        .placeholder(R.drawable.image_placeholder)
                                        .into(imageView);
                            }
                        });

            }

        } catch (Exception e){

        }
    }

    @BindingAdapter("android:avatarURL")
    public static void setAvatarURL(RoundedImageView imageView, String URL) {
        try {

            if (URL == null || TextUtils.isEmpty(URL)){

                imageView.setImageResource(R.drawable.avatar_placeholder);

            } else {

                Picasso.get()
                        .load(Uri.parse(URL))
                        .config(Bitmap.Config.RGB_565)
                        .fit().centerCrop()
                        .into(imageView);

            }

        } catch (Exception e){

        }
    }

}

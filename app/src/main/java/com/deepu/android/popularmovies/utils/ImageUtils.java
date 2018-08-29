package com.deepu.android.popularmovies.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.deepu.android.popularmovies.R;
import com.deepu.android.popularmovies.data.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    public static void setImageView(final Movie movie, final ImageView imageView) {
       byte[] byteArray = movie.getDbPosterImage();
        if (byteArray == null) {
            Picasso.get()
                    .load(movie.getImageURL())
                    .placeholder(R.drawable.image_placeholder)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                            byte[] imageInByte = stream.toByteArray();
                            movie.setDbPosterImage(stream.toByteArray());
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        } else {
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageView.setImageBitmap(bmp);
        }
    }

}

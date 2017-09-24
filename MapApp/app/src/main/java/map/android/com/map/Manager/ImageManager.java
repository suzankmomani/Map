package map.android.com.map.Manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class ImageManager {


    public static void loadImg(Context context, String url, ImageView imgView) {

        Picasso picasso = Picasso.with(context);
        picasso.load(url).config(Bitmap.Config.RGB_565)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imgView);
    }
}

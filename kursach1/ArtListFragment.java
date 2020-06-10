package com.example.kursach1;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kursach1.utils.BitmapUtils;
import com.example.kursach1.utils.SpacesItemDecoration;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;



public class ArtListFragment extends Fragment implements ThumbnailsAdapter.ThumbnailsAdapterListener {


    @BindView(R.id.artrecycler_view)
    RecyclerView recyclerView;

    ThumbnailsAdapter adapter;
    List<ThumbnailItem> thumbnailItems;

    FiltersListFragment.FiltersListFragmentListener listner;


    private static final int INPUT_SIZE = 256;
    private int[] intValues;
    private float[] floatValues;

    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output_new";
    private static final String MODEL_FILE_B = "file:///android_asset/bossK_float.pb";
    private static final String MODEL_FILE_C = "file:///android_asset/cubist_float.pb";
    private static final String MODEL_FILE_D = "file:///android_asset/denoised_starry_float.pb";
    private static final String MODEL_FILE_F = "file:///android_asset/feathers_float.pb";
    private static final String MODEL_FILE_M = "file:///android_asset/mosaic_float.pb";
    private static final String MODEL_FILE_S = "file:///android_asset/scream_float.pb";
    private static final String MODEL_FILE_U = "file:///android_asset/udnie_float.pb";
    private static final String MODEL_FILE_W = "file:///android_asset/wave_float.pb";
    private static final String MODEL_FILE_CR = "file:///android_asset/crayon_float.pb";
    private static final String MODEL_FILE_I = "file:///android_asset/ink_float.pb";

    private String[] Pathes={MODEL_FILE_B,MODEL_FILE_C,MODEL_FILE_D,MODEL_FILE_F,
    MODEL_FILE_M,MODEL_FILE_S,MODEL_FILE_U,MODEL_FILE_W,MODEL_FILE_CR,MODEL_FILE_I};
    //public void SetFiltersListner(FiltersListFragment.FiltersListFragmentListener Listner){
     //   listner=Listner;
    //}


    @Override
    public void onFilterSelected(Filter filter) {

    }

    public ArtListFragment() {

    }

    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_art_list, container, false);

        thumbnailItems=new ArrayList<>();
        adapter=new ThumbnailsAdapter(getActivity(),thumbnailItems,this);

        recyclerView=(RecyclerView) view.findViewById(R.id.artrecycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        recyclerView.setAdapter(adapter);

        prepareThumbnail(null);

        return view;
    }
    public void prepareThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            public void run() {
                Bitmap thumbImage;

                if (bitmap == null) {
                    thumbImage = BitmapUtils.getBitmapFromAssets(getActivity(), MainActivity.IMAGE_NAME, 100, 100);
                } else {
                    thumbImage = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                }

                if (thumbImage == null)
                    return;
                thumbnailItems.clear();

                // add normal bitmap first
                //ThumbnailItem thumbnailItem = new ThumbnailItem();
                //thumbnailItem.image = thumbImage;
                //thumbnailItem.filterName = getString(R.string.filter_normal);
                //ThumbnailsManager.addThumb(thumbnailItem);
                Transformer transformer=new Transformer(getContext().getAssets(),Pathes[0]);
                for (String str : Pathes) {
                    ThumbnailItem tI = new ThumbnailItem();

                    tI.filterName = str;
                    transformer.SetModel(getContext().getAssets(),str);
                    transformer.stylizeImage(thumbImage,100);
                    tI.image = thumbImage;
                }

                thumbnailItems.addAll(ThumbnailsManager.processThumbs(getActivity()));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };

        new Thread(r).start();
    }
}

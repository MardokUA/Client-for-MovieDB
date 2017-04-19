package laktionov.filmsraiting.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import laktionov.filmsraiting.R;
import laktionov.filmsraiting.rest.BaseApi;
import laktionov.filmsraiting.rest.model.Poster;

public class MovieListAdapter extends BaseAdapter {

    private Context context;
    private List<Poster> posterList;

    public MovieListAdapter(Context context, List<Poster> posterList) {
        this.context = context;
        this.posterList = posterList;
    }

    @Override
    public int getCount() {
        return posterList.size();
    }

    @Override
    public Object getItem(int i) {
        return posterList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return posterList.get(i).getId();
    }

    @Override
    public View getView(int index, View convertedView, ViewGroup viewGroup) {

        if (convertedView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertedView = layoutInflater.inflate(R.layout.fragment_poster_list_item, viewGroup, false);
        }

        ImageView lvPoster = (ImageView) convertedView.findViewById(R.id.posters_list_item_iv_poster);
        TextView tvFilmTitle = (TextView) convertedView.findViewById(R.id.posters_list_item_tv_title);

        Poster poster = posterList.get(index);

        if (poster.getPoster_path() == null) {
            lvPoster.setImageResource(R.drawable.no_image_avavailable);
        } else {
            Picasso.with(context)
                    .setIndicatorsEnabled(true);
            Picasso.with(context)
                    .load(BaseApi.API_URL_IMAGE + poster.getPoster_path())
                    .resize(500,700)
                    .placeholder(R.drawable.progress_animation)
                    .into(lvPoster);
        }


        if (poster.getOriginal_title() == null) {
            tvFilmTitle.setText(poster.getOriginal_name());
        } else {
            tvFilmTitle.setText(poster.getOriginal_title());
        }

        return convertedView;
    }
}

package laktionov.filmsraiting.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import laktionov.filmsraiting.R;

public class EmptyFragment extends Fragment {

    public static final String TAG = "EMPTY FRAGMENT";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_empty, container, false);
        TextView textView = (TextView) root.findViewById(R.id.tv_empty);
        Log.d(TAG, "onCreateView: ");

        return root;
    }
}

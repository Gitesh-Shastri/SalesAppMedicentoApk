package com.developer.medicento.salesappmedicento.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.developer.medicento.salesappmedicento.R;

public class AddPhotoBottomDialogFragment extends BottomSheetDialogFragment {

    public static AddPhotoBottomDialogFragment newInstance() {
        return new AddPhotoBottomDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_layout, container,
                false);

        // get the views and attach the listener

        return view;

    }
}

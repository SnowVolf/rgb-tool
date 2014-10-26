package com.fastebro.androidrgbtool.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fastebro.androidrgbtool.R;
import com.fastebro.androidrgbtool.adapters.SelectPictureListAdapter;
import com.fastebro.androidrgbtool.ui.MainActivity;

/**
 * Created by danielealtomare on 21/06/14.
 */
public class SelectPictureDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    public SelectPictureDialogFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_picture, container);

        SelectPictureListAdapter mAdapter = new SelectPictureListAdapter(getActivity(),
                R.layout.select_picture_row,
                getResources().getStringArray(R.array.pick_color_array));

        ListView listview = (ListView) view.findViewById(android.R.id.list);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(this);

        getDialog().setTitle(getString(R.string.pick_color));

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                if (getActivity() != null) {
                    ((MainActivity)getActivity()).openRGBToolGallery();
                    dismiss();
                }
                break;
            case 1:
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).openDeviceGallery();
                    dismiss();
                }
                break;
            case 2:
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).dispatchTakePictureIntent();
                    dismiss();
                }
                break;
            default:
                break;
        }
    }
}
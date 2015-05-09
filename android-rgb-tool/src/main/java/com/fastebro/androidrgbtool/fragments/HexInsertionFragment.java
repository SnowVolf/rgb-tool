package com.fastebro.androidrgbtool.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.fastebro.android.rgbtool.model.events.PrintColorEvent;
import com.fastebro.android.rgbtool.model.events.PrintPaletteEvent;
import com.fastebro.androidrgbtool.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by danielealtomare on 26/03/14.
 */
public class HexInsertionFragment extends DialogFragment {
    @InjectView(R.id.new_hex_value)
    EditText newHexValue;

    public static String ARG_HEX_VALUE = "HEX_VALUE";

    public HexInsertionFragment() { }

    private String hexValue;

    public static HexInsertionFragment newInstance(String hexValue) {
        HexInsertionFragment fragment = new HexInsertionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HEX_VALUE, hexValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            hexValue = getArguments().getString(ARG_HEX_VALUE, "000000");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_hex_insertion, null);
        ButterKnife.inject(this, view);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setTitle(getString(R.string.hex_insertion_title))
                .setPositiveButton(getString(R.string.action_common_set),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EventBus.getDefault().post(new PrintPaletteEvent(newHexValue.getText().toString()));
                                HexInsertionFragment.this.getDialog().cancel();
                            }
                        }
                )
                .setNegativeButton(getString(R.string.action_common_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                HexInsertionFragment.this.getDialog().cancel();
                            }
                        }
                );

        newHexValue.setText(hexValue);
        newHexValue.setSelection(newHexValue.getText().length());

        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
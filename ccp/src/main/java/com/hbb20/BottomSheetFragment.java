package com.hbb20;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class BottomSheetFragment extends BottomSheetDialogFragment implements CountrySearchListener {
    private EditText editText_search;
    private View rootView;
    private ImageView imgClearQuery;
    private RecyclerView recyclerView_countryDialog;
    private CountryCodeAdapterNew ccAdapter;
    private RelativeLayout rlContainer;
    private TextView tvNoResult;
    private CCPCountry selectedCountry;
    private CountrySelectListener countrySelectListener;


    public BottomSheetFragment(CountrySelectListener _countrySelectListener) {
        this.countrySelectListener = _countrySelectListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        View view = getView();
        view.post(new Runnable() {
            @Override
            public void run() {
                View parent = (View) view.getParent();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
                CoordinatorLayout.Behavior behavior = params.getBehavior();
                BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
                bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
            }
        });
    }

    /*@NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return  dialog;
    }*/



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getActivity()!= null){
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        rootView = inflater.inflate(R.layout.bottom_sheet, container, false);
        editText_search = (EditText) rootView.findViewById(R.id.editText_search);
        imgClearQuery = (ImageView) rootView.findViewById(R.id.img_clear_query);
        recyclerView_countryDialog = (RecyclerView) rootView.findViewById(R.id.recycler_countryDialog);
        rlContainer = (RelativeLayout) rootView.findViewById(R.id.rlContainer);
        tvNoResult = (TextView) rootView.findViewById(R.id.textView_noresult);


        editText_search.requestFocus();


        setQueryClearListener();
        setTextWatcher();

        setupCountryDataToRecyclerView();
        setupFastScroll();
        return rootView;
    }

    private  void hideKeyboard() {
        if (editText_search != null && getActivity() != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText_search.getWindowToken(), 0);
        }
    }

    private void setQueryClearListener() {
        imgClearQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_search.setText("");
            }
        });
    }

    /**
     * add textChangeListener, to apply new query each time editText get text changed.
     */
    /**
     * add textChangeListener, to apply new query each time editText get text changed.
     */
    private void setTextWatcher() {
        if (this.editText_search != null) {
            this.editText_search.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //TODO: here perform operation on adapter
                    ccAdapter.applyQuery(s.toString());
                    if (s.toString().trim().equals("")) {
                        imgClearQuery.setVisibility(View.GONE);
                    } else {
                        imgClearQuery.setVisibility(View.VISIBLE);
                    }
                }
            });

            this.editText_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(editText_search.getWindowToken(), 0);
                        return true;
                    }

                    return false;
                }
            });
        }
    }

    private void setupCountryDataToRecyclerView() {
        List<CCPCountry> masterCountries = CCPCountry.getLibraryMasterCountriesEnglish();
        ccAdapter = new CountryCodeAdapterNew(getActivity(), this, masterCountries);
        recyclerView_countryDialog.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_countryDialog.setAdapter(ccAdapter);
    }

    private void setupFastScroll() {
        FastScroller fastScroller = (FastScroller) rootView.findViewById(R.id.fastscroll);
        fastScroller.setRecyclerView(recyclerView_countryDialog);
        /*if (codePicker.isShowFastScroller()) {
            if (codePicker.getFastScrollerBubbleColor() != 0) {
                fastScroller.setBubbleColor(codePicker.getFastScrollerBubbleColor());
            }

            if (codePicker.getFastScrollerHandleColor() != 0) {
                fastScroller.setHandleColor(codePicker.getFastScrollerHandleColor());
            }

            if (codePicker.getFastScrollerBubbleTextAppearance() != 0) {
                try {
                    fastScroller.setBubbleTextAppearance(codePicker.getFastScrollerBubbleTextAppearance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            fastScroller.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void noSearchResult() {
        tvNoResult.setVisibility(View.VISIBLE);
        //rlContainer.setVisibility(View.GONE);
    }

    @Override
    public void showCountryContainer() {
        tvNoResult.setVisibility(View.GONE);
        //rlContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCountryContainer(CCPCountry ccpCountry) {
        this.selectedCountry = ccpCountry;
        if (countrySelectListener != null) {
            countrySelectListener.getSelectedCountry(ccpCountry);
        }
        //manageKeyboard(false);
        //hideKeyboard(getActivity());

        ///View view = editText_search.getCurrentFocus();
        hideKeyboard();

        dismiss();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        hideKeyboard();
        super.onCancel(dialog);

        //Toast.makeText(getActivity(), "Cancel called", Toast.LENGTH_SHORT).show();
    }

    public CCPCountry getSelectedCountry() {
        return selectedCountry;
    }


}

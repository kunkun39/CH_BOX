package com.changhong.common.fragment;

import android.app.Activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.changhong.common.R;
import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecycleViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecycleViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecycleViewFragment extends Fragment {

    protected static final String ARG_PARAM1 = "param1";
    protected static final String ARG_PARAM2 = "param2";
    protected static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private RecycleViewAdapter mAdapter;
    private String mParam2;
    private int mCol = 1;

    private OnFragmentInteractionListener mListener;

    public RecycleViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param adapter Parameter 1.
     * @param param Parameter 2.
     * @return A new instance of fragment RecycleViewFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static  RecycleViewFragment newInstance(RecycleViewAdapter  adapter, String param) {
        RecycleViewFragment fragment = new RecycleViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1,adapter);
        args.putString(ARG_PARAM2, param);
        fragment.setArguments(args);
        return fragment;
    }

    public static  RecycleViewFragment newInstance(RecycleViewAdapter  adapter, String param,int col) {
        RecycleViewFragment fragment = newInstance(adapter,param);
        fragment.getArguments().putInt(ARG_PARAM3, col);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAdapter = (RecycleViewAdapter)getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mCol = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView v = (RecyclerView)inflater.inflate(R.layout.recyclerview,container,false);
        RecyclerView.LayoutManager  layoutManager;
        if(mCol > 1){
            layoutManager = new GridLayoutManager(getActivity(),mCol);
        }else {
            layoutManager = new LinearLayoutManager(getActivity());
        }
        v.setLayoutManager(layoutManager);
        v.setAdapter(mAdapter);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getmParam2() {
        return mParam2;
    }

    public void setmParam2(String mParam2) {
        this.mParam2 = mParam2;
    }

    public RecycleViewAdapter getmAdapter() {
        return mAdapter;
    }

    public void setmAdapter(RecycleViewAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static abstract class RecycleViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements Serializable {
        @Override
        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

        @Override
        public abstract void onBindViewHolder(VH holder, int position);

        @Override
        public abstract int getItemCount();
    }
}

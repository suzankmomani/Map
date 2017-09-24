package map.android.com.map.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import map.android.com.map.Manager.DbQueryManager;
import map.android.com.map.Objects.Place;
import map.android.com.map.R;

/**
 * A fragment representing a list of places in a search list.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private MyItemRecyclerViewAdapter mAdapter;
    private List<Place> mPlaceList = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListener = (OnListFragmentInteractionListener) getActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener.fragmentCreated();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        setRecyclerView(view);

        return view;
    }


    public void refreshAdapter(String searchString) {
        /*make query function based on name*/
        /*get curser*/
        /*get search string*/
        /*find the match and fill the adapter */
        mPlaceList = DbQueryManager.getPlacesByName(getActivity(), searchString);

        setRecyclerView(getView());


    }

    private void setRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new MyItemRecyclerViewAdapter(mPlaceList, mListener);
        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onItemSelected(Place place);

        void onSelectCompleteItemIconFromList(String newStr);

        /*activate search query listeners when fragment is created*/
        void fragmentCreated();

    }
}

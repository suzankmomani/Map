package map.android.com.map.Fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import map.android.com.map.Fragment.ItemFragment.OnListFragmentInteractionListener;
import map.android.com.map.Objects.Place;
import map.android.com.map.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Place} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Place> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<Place> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mDescView.setText(mValues.get(position).getDesc());
        holder.mContentView.setText(mValues.get(position).getName());

        holder.mSelectSelectionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSelectCompleteItemIconFromList(mValues.get(holder.getAdapterPosition()).getName());
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the main activity that a place from the list is selected
                    mListener.onItemSelected(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDescView;
        public final TextView mContentView;
        public Place mItem;
        public final ImageView mSelectSelectionImg;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDescView = (TextView) view.findViewById(R.id.desc);
            mContentView = (TextView) view.findViewById(R.id.content);
            mSelectSelectionImg = (ImageView) view.findViewById(R.id.selectSelectionImg);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

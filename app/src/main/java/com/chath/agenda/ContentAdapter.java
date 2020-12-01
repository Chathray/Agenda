package com.chath.agenda;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chath.agenda.data.ContentModel;

import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.DrinksViewHolder> implements Filterable {

    private ArrayList<ContentModel> dataOrigin;
    private ArrayList<ContentModel> dataFilter;
    private MainPage main;

    public ContentAdapter(ArrayList<ContentModel> data, MainPage main) {
        this.dataFilter = this.dataOrigin = data;
        this.main = main;
    }

    @NonNull
    @Override
    public ContentAdapter.DrinksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_item, parent, false);
        return new DrinksViewHolder(v, main);
    }

    @Override
    public void onBindViewHolder(ContentAdapter.DrinksViewHolder holder, int position) {
        Activity a = main.getActivity();

        String t = dataFilter.get(position).getTitle();
        String d = dataFilter.get(position).getDescription();
        String c = dataFilter.get(position).getCreatedAt();

        holder.title.setHeight(main.getSpacing());
        holder.index.setHeight(main.getSpacing());

        holder.title.setText(t);
        holder.title.setMaxWidth(a.getResources().getDisplayMetrics().widthPixels - AppUtilities.dpToPx(77, a) - holder.index.getWidth());

        holder.index.setText(String.valueOf(position + 1));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentModel data = new ContentModel(main.getContentKey(), t, d, c);
                ((MainActivity) main.getActivity()).SmartContentView(data);
                AppUtilities.hideKeyboard(main.getContext());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    dataFilter = dataOrigin;
                } else {
                    ArrayList<ContentModel> filteredList = new ArrayList<>();
                    for (ContentModel row : dataOrigin) {
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getDescription().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    dataFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataFilter = (ArrayList<ContentModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public ArrayList<ContentModel> getData() {
        return dataOrigin;
    }

    public void updateData(ArrayList<ContentModel> data) {
        this.dataFilter = this.dataOrigin = data;
    }

    public void removeItem(int position) {
        dataOrigin.remove(position);
        dataFilter = dataOrigin;
        notifyItemRemoved(position);
    }

    public void restoreItem(ContentModel item, int position) {
        dataOrigin.add(position, item);
        dataFilter = dataOrigin;
        notifyItemInserted(position);
    }

    public static class DrinksViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected TextView index;

        public DrinksViewHolder(View itemView, MainPage main) {
            super(itemView);
            title = itemView.findViewById(R.id.si_title);
            index = itemView.findViewById(R.id.si_index);
        }
    }
}

package com.example.android.lagos_java_developers.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.lagos_java_developers.R;
import com.example.android.lagos_java_developers.model.Developers;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by OZMA NIG COM LTD on 04-Aug-17.
 */

public class DevelopersAdapter extends RecyclerView.Adapter<DevelopersAdapter.Developer_ViewHoler> {


    // flag for footer ProgressBar (i.e. last item of list)
    private boolean isLoadingAdded = false;

   private  List<Developers>mDevelopers;
   private Context context;

   final private ListItemClickListiner mOnClickedListiner;

    public interface ListItemClickListiner{
        void onListItemClicked(int clickditemindex);
    }

    public DevelopersAdapter(Context context, List<Developers> developers, ListItemClickListiner listiner){

        mDevelopers=developers;
        this.context=context;
        mOnClickedListiner =listiner;

    }

    @Override
    public DevelopersAdapter.Developer_ViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutResourceId = R.layout.activity_main;
            Context getCurrentContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(getCurrentContext);



        View newViewHolder= layoutInflater.inflate(layoutResourceId,parent,false );

        Developer_ViewHoler newDeveloperViewHolder = new Developer_ViewHoler(newViewHolder);
        return newDeveloperViewHolder;
    }

    @Override
    public void onBindViewHolder(Developer_ViewHoler holder, int position) {
        holder.bind(position);

    }

    @Override
    public int getItemCount() {

        return mDevelopers == null ? 0 : mDevelopers.size();

    }

   public class Developer_ViewHoler extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView devName;
        ImageView devImage;

        private Developer_ViewHoler(View itemView ) {
            super(itemView);

            itemView.setOnClickListener(this);


//             Calls the setOnClicklistiner on the on itemView in the constructor to (this )

            devImage = (ImageView) itemView.findViewById(R.id.dev_image);
           devName = (TextView) itemView.findViewById(R.id.dev_name);


        }




         void bind(int listIndex){
             Developers developers =mDevelopers.get(listIndex);
             Picasso.with(context).load(developers.getImageResourceId())
                     .transform(new CropCircleTransformation())
                     .error(R.drawable.patterns_emptystates_do)
                     .placeholder(R.drawable.avata)
                     .into(devImage);
             devName.setText(developers.getDeveloperName());

    }


        @Override
        public void onClick(View view) {

//              Set the body of the function to get the position which is the item that was clicked


            clickedPosition = getAdapterPosition();

             //This invokes the onclick listener of the other class by passing @param clickedPosition value

            mOnClickedListiner.onListItemClicked(clickedPosition);
        }
    }

    int clickedPosition;

  public void clear(){
      mDevelopers.clear();
      notifyDataSetChanged();
  }

    public void addAll(List<Developers>Developers){
          mDevelopers.addAll(Developers);
        notifyDataSetChanged();
    }

    public  Developers getList(){
       return mDevelopers.get(clickedPosition);
    }


    public void add(Developers mc) {
        mDevelopers.add(mc);
        notifyItemInserted(mDevelopers.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Developers("","","",""));
    }


    public void remove(Developers city) {
        int position = mDevelopers.indexOf(city);
        if (position > -1) {
            mDevelopers.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mDevelopers.size() - 1;
        Developers item = getItem(position);

        if (item != null) {
            mDevelopers.remove(position);
            notifyItemRemoved(position);
        }


    }

    public Developers getItem(int position) {
        return mDevelopers.get(position);
    }
//    public void onLoadMore() {
//        //add null , so the adapter will check view_type and show progress bar at bottom
//        mDevelopers.add(null); //this is Main its for Loader
//      notifyItemInserted(mDevelopers.size() - 1); //added one more line
//
//    }

}
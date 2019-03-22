package coba.sejahtera.pt.movieapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.util.List;

import coba.sejahtera.pt.movieapp.Model.FavoriteDbHelper;
import coba.sejahtera.pt.movieapp.Model.Movie;
import coba.sejahtera.pt.movieapp.R;
import coba.sejahtera.pt.movieapp.View.Activity.detailActivity;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {


    private FavoriteDbHelper favoriteDbHelper;
    private Context mContext;
    private List<Movie> movieList;
    public int pos1;
    public MovieAdapter(Context mContext, List<Movie> movieList){
        this.mContext = mContext;
        this.movieList = movieList;
    }

    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_card, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieAdapter.MyViewHolder viewHolder, int i){
        viewHolder.title.setText(movieList.get(i).getOriginalTitle());
        String vote = Double.toString(movieList.get(i).getVoteAverage());
        viewHolder.userrating.setText(vote);

        String poster = "https://image.tmdb.org/t/p/w500" + movieList.get(i).getPosterPath();

        Glide.with(mContext)
                .load(poster)
                .placeholder(R.drawable.load)
                .into(viewHolder.thumbnail);

    }

    @Override
    public int getItemCount(){
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, userrating;
        public ImageView thumbnail;
        public MaterialFavoriteButton materialFavoriteButtonNice;
        public Movie movie,movie1;


        public MyViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            userrating = (TextView) view.findViewById(R.id.userrating);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            materialFavoriteButtonNice = (MaterialFavoriteButton) view.findViewById(R.id.favorite_button1);
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    pos1 = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        Movie clickedDataItem = movieList.get(pos);
                        Intent intent = new Intent(mContext, detailActivity.class);
                        intent.putExtra("movies", clickedDataItem );
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        Toast.makeText(v.getContext(), "You clicked " + clickedDataItem.getOriginalTitle(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            materialFavoriteButtonNice.setOnFavoriteChangeListener(
                    new MaterialFavoriteButton.OnFavoriteChangeListener(){
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite){
                            if (favorite){
                                int pos = getAdapterPosition();
                                favoriteDbHelper = new FavoriteDbHelper(mContext);
                                movie = new Movie();
                                Double rate = movieList.get(pos).getVoteAverage();
                                movie.setId(movieList.get(pos).getId());
                                movie.setOriginalTitle(movieList.get(pos).getOriginalTitle());
                                movie.setPosterPath(movieList.get(pos).getPosterPath());
                                movie.setVoteAverage(rate);
                                movie.setOverview(movieList.get(pos).getOverview());

                                favoriteDbHelper.addFavorite(movie);
                                Snackbar.make(buttonView, "Added to Favorite",
                                        Snackbar.LENGTH_SHORT).show();
                            }else{
                                int movie_id = movieList.get(pos1).getId();
                                favoriteDbHelper = new FavoriteDbHelper(mContext);
                                favoriteDbHelper.deleteFavorite(movie_id);
                                Snackbar.make(buttonView, "Removed from Favorite",
                                        Snackbar.LENGTH_SHORT).show();
                            }

                        }
                    }
            );
        }
    }
}

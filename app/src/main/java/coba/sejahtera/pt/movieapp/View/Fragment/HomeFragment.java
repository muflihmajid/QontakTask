package coba.sejahtera.pt.movieapp.View.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import coba.sejahtera.pt.movieapp.API.Client;
import coba.sejahtera.pt.movieapp.API.Services;
import coba.sejahtera.pt.movieapp.Adapter.MovieAdapter;
import coba.sejahtera.pt.movieapp.BuildConfig;
import coba.sejahtera.pt.movieapp.Model.Movie;
import coba.sejahtera.pt.movieapp.Model.MoviesResponse;
import coba.sejahtera.pt.movieapp.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Tambahan
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> MovieList;
    private SwipeRefreshLayout swipeContainer;
    ProgressDialog pd;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        initViews();
        loadJSON();
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                initViews();
                Toast.makeText(getContext(), "Movies Refreshed", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    public Activity getActivity1(){
        Context context = getContext();
        while (context instanceof ContextWrapper){
            if (context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;

    }

    private void initViews()
    {
        MovieList = new ArrayList<>();
        adapter = new MovieAdapter(getContext(),MovieList);

        if(getActivity1().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        }else
            {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
            }
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

    }

    private void loadJSON() {
        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }

            Client Client = new Client();
            Services apiService =
                    Client.getClient().create(Services.class);
            Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    Collections.sort(movies, Movie.BY_NAME_ALPHABETICAL);
                    recyclerView.setAdapter(new MovieAdapter(getContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }

                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getContext(), "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

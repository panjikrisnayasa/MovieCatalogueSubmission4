package com.panjikrisnayasa.moviecataloguesubmission5.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.panjikrisnayasa.moviecataloguesubmission5.R
import com.panjikrisnayasa.moviecataloguesubmission5.model.Movie
import com.panjikrisnayasa.moviecataloguesubmission5.view.DetailMovieTVShowActivity
import kotlinx.android.synthetic.main.item_recycler_fragment_movies.view.*

class MoviesAdapter :
    RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>() {

    companion object {
        private const val BASE_URL = "https://image.tmdb.org/t/p/w185/"
    }

    private var mData = ArrayList<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_fragment_movies, parent, false)
        return MoviesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    fun setData(movies: ArrayList<Movie>) {
        mData.clear()
        mData.addAll(movies)
        notifyDataSetChanged()
    }

    private fun moveToDetail(view: View, movieID: String?) {
        val viewContext = view.context
        val detailIntent =
            Intent(viewContext, DetailMovieTVShowActivity::class.java)
        detailIntent.putExtra(DetailMovieTVShowActivity.EXTRA_MOVIE_ID, movieID)
        viewContext.startActivity(detailIntent)
    }

    inner class MoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) {
            with(itemView) {
                image_item_recycler_fragment_movies_poster.clipToOutline = true
                val posterPath = BASE_URL + movie.posterPath
                Glide.with(context).load(posterPath)
                    .into(image_item_recycler_fragment_movies_poster)
                val tVoteAverage = movie.voteAverage
                var voteAverage = 0f
                if (tVoteAverage != null) {
                    voteAverage = (tVoteAverage / 2).toFloat()
                }
                rating_item_recycler_fragment_movies.rating = voteAverage
                text_item_recycler_fragment_movies_vote_average.text = movie.voteAverage.toString()
                text_item_recycler_fragment_movies_title.text = movie.title
                text_item_recycler_fragment_movies_popularity.text = movie.popularity.toString()
                text_item_recycler_fragment_movies_release_date.text = movie.releaseDate
                val forAdult = movie.forAdult
                if (forAdult!!) {
                    text_item_recycler_fragment_movies_rating.text =
                        resources.getString(R.string.movie_rating_adult)
                } else {
                    text_item_recycler_fragment_movies_rating.text =
                        resources.getString(R.string.movie_rating_all_ages)
                }

                button_item_recycler_fragment_movies_details.setOnClickListener {
                    moveToDetail(it, movie.id)
                }
                itemView.setOnClickListener {
                    moveToDetail(it, movie.id)
                }
            }
        }
    }
}
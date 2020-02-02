package com.panjikrisnayasa.moviecataloguesubmission4.view


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.panjikrisnayasa.moviecataloguesubmission4.R
import com.panjikrisnayasa.moviecataloguesubmission4.adapter.FavoredTVShowsAdapter
import com.panjikrisnayasa.moviecataloguesubmission4.db.FavoredTVShowsHelper
import com.panjikrisnayasa.moviecataloguesubmission4.helper.MappingHelper
import com.panjikrisnayasa.moviecataloguesubmission4.model.TVShow
import kotlinx.android.synthetic.main.fragment_favorite_tvshows.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class FavoriteTVShowsFragment : Fragment() {

    companion object {
        private const val EXTRA_STATE = "extra_state"
    }

    private lateinit var mFavoriteTVShowsAdapter: FavoredTVShowsAdapter
    private lateinit var mFavoredTVShowsHelper: FavoredTVShowsHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_tvshows, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showRecyclerView()

        mFavoredTVShowsHelper = FavoredTVShowsHelper.getInstance(this.activity!!.applicationContext)
        mFavoredTVShowsHelper.open()

        if (savedInstanceState == null) {
            loadFavoredMoviesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<TVShow>(EXTRA_STATE)
            if (list != null) mFavoriteTVShowsAdapter.listTVShows = list
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (requestCode == DetailFavoredMovieTVShowActivity.REQUEST_UPDATE) {
                if (resultCode == DetailFavoredMovieTVShowActivity.RESULT_ADD) {
                }
                if (resultCode == DetailFavoredMovieTVShowActivity.RESULT_DELETE) {
                    val position =
                        data.getIntExtra(DetailFavoredMovieTVShowActivity.EXTRA_POSITION, 0)
                    mFavoriteTVShowsAdapter.removeItem(position)
                    if (mFavoriteTVShowsAdapter.listTVShows.size == 0) {
                        text_fragment_favorite_tvshow.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, mFavoriteTVShowsAdapter.listTVShows)
    }

    override fun onDestroy() {
        super.onDestroy()
        mFavoredTVShowsHelper.close()
    }

    private fun showRecyclerView() {
        recycler_fragment_favorite_tvshow.setHasFixedSize(true)
        mFavoriteTVShowsAdapter = FavoredTVShowsAdapter(this)
        recycler_fragment_favorite_tvshow.layoutManager = LinearLayoutManager(this.context)
        recycler_fragment_favorite_tvshow.adapter = mFavoriteTVShowsAdapter
    }

    private fun loadFavoredMoviesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            progress_fragment_favorite_tvshow.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = mFavoredTVShowsHelper.queryAll()
                MappingHelper.mapFavoredTVShowCursorToArrayList(cursor)
            }
            progress_fragment_favorite_tvshow.visibility = View.INVISIBLE
            val tvShow = deferredNotes.await()
            if (tvShow.size > 0) {
                mFavoriteTVShowsAdapter.listTVShows = tvShow
                text_fragment_favorite_tvshow.visibility = View.GONE
            } else {
                mFavoriteTVShowsAdapter.listTVShows = ArrayList()
                text_fragment_favorite_tvshow.visibility = View.VISIBLE
            }
        }
    }
}

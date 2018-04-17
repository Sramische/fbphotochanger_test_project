package com.inspirationdriven.fbphotochanger.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.inspirationdriven.fbphotochanger.R
import com.inspirationdriven.fbphotochanger.model.Album
import com.inspirationdriven.fbphotochanger.viewmodel.PhotoViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var photoModelView: PhotoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        photoModelView = ViewModelProviders.of(this)[PhotoViewModel::class.java]

        supportFragmentManager.addOnBackStackChangedListener {
            val hasStacked = supportFragmentManager.backStackEntryCount > 0
            supportActionBar?.setDisplayHomeAsUpEnabled(hasStacked)
        }

        if (savedInstanceState == null)
            setFragment(PhotoFragment(), "photo")

        photoModelView.fetchProfilePicture(Resources.getSystem().displayMetrics.widthPixels)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> supportFragmentManager.popBackStack()
            R.id.action_open_collection -> showAlbumsScreen()
            R.id.action_open_editor -> openImageEditor()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun showAlbumsScreen() {
        showFragment(AlbumListFragment(),"albums")
    }

    public fun showAlbumImages(a: Album){
        showFragment(AlbumImagesFragment.newInstance(a), a.meta.name)
    }

    private fun openImageEditor() {

    }

    private fun setFragment(frag: Fragment, tag: String){
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, frag, tag)
                .commit()
    }

    private fun showFragment(frag: Fragment, tag: String? = null){
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, frag)
                .addToBackStack(tag)
                .commit()
    }

    companion object {
        fun show(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}

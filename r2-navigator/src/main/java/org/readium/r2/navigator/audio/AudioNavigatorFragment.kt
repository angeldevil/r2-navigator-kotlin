/*
 * Copyright 2020 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.r2.navigator.audio

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.readium.r2.navigator.MediaNavigator
import org.readium.r2.navigator.R
import org.readium.r2.navigator.extensions.formatElapsedTime
import org.readium.r2.navigator.extensions.viewById
import org.readium.r2.shared.AudioSupport
import org.readium.r2.shared.FragmentNavigator
import org.readium.r2.shared.publication.services.cover
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@AudioSupport @FragmentNavigator
@OptIn(ExperimentalTime::class)
class AudioNavigatorFragment(
    private val mediaNavigator: MediaNavigator
) : Fragment(), MediaNavigator by mediaNavigator {

    /**
     * Factory for an [AudioNavigatorFragment].
     *
     * @param mediaNavigator The underlying chromeless navigator handling media playback.
     */
    class Factory(
        private val mediaNavigator: MediaNavigator
    ) : FragmentFactory() {

        override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
            when (className) {
                AudioNavigatorFragment::class.java.name -> AudioNavigatorFragment(mediaNavigator)
                else -> super.instantiate(classLoader, className)
            }

    }

    private val coverView: ImageView by viewById(R.id.coverView)
    private val seekBar: SeekBar by viewById(R.id.seekBar)
    private val positionLabel: TextView by viewById(R.id.progressTime)
    private val durationLabel: TextView by viewById(R.id.chapterTime)
    private val playPauseButton: View by viewById(R.id.play_pause)
    private val fastForwardButton: View by viewById(R.id.fast_back)
    private val rewindButton: View by viewById(R.id.fast_back)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_r2_audio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            publication.cover()?.let { coverView.setImageBitmap(it) }
        }

        mediaNavigator.playbackInfo.observe(viewLifecycleOwner, Observer { info ->
            seekBar.max = info.duration?.inSeconds?.roundToInt() ?: 0
            seekBar.progress = info.position.inSeconds.roundToInt()
            positionLabel.text = info.position.formatElapsedTime()
            durationLabel.text = info.duration?.formatElapsedTime() ?: ""
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                mediaNavigator.seekTo(progress.seconds)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        playPauseButton.setOnClickListener { playPause() }
        fastForwardButton.setOnClickListener { goForward() }
        rewindButton.setOnClickListener { goBackward() }

//            next_chapter!!.setOnClickListener {
//                goForward(false) {}
//            }
//
//            prev_chapter!!.setOnClickListener {
//                goBackward(false) {}
//            }
    }

}
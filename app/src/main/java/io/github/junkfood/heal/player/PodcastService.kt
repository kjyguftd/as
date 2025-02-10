package io.github.junkfood.heal.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import io.github.junkfood.heal.MainActivity
import io.github.junkfood.heal.R

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PodcastService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private val sessionCallback = CustomMediaSessionCallback()
    private val customCommandButtons = mutableMapOf<String, CommandButton>()

    private var customLayout = ImmutableList.of<CommandButton>()

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession =
        mediaSession

    override fun onCreate() {
        super.onCreate()
        MediaControllerUtil.get(this)
        player = ExoPlayerHolder.get(this)
        customCommandButtons.apply {
            put(
                ButtonID.SHUFFLE_MODE_ON, getShuffleCommandButton(
                    SessionCommand(ButtonID.SHUFFLE_MODE_ON, Bundle.EMPTY)
                )
            )
            put(
                ButtonID.SHUFFLE_MODE_OFF, getShuffleCommandButton(
                    SessionCommand(ButtonID.SHUFFLE_MODE_OFF, Bundle.EMPTY)
                )
            )
        }
        initSession()
    }

    override fun onDestroy() {
        player.release()
        mediaSession.release()
        MediaControllerUtil.release()
        super.onDestroy()
    }


    private inner class CustomMediaSessionCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)
            val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
            customCommandButtons.forEach { (_, commandButton) ->
                commandButton.sessionCommand?.let { availableSessionCommands.add(it) }
            }
            return MediaSession.ConnectionResult.accept(
                availableSessionCommands.build(),
                connectionResult.availablePlayerCommands
            )
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            if (customCommand.customAction == ButtonID.SHUFFLE_MODE_ON) {
                player.shuffleModeEnabled = true
                // Temp
                customLayout = ImmutableList.of(customCommandButtons[ButtonID.SHUFFLE_MODE_ON]!!)
                session.setCustomLayout(customLayout)
            } else {
                player.shuffleModeEnabled = false
                customLayout = ImmutableList.of(customCommandButtons[ButtonID.SHUFFLE_MODE_OFF]!!)
                session.setCustomLayout(customLayout)
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }


    }

    private fun initSession() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(sessionCallback)
            .setId("MediaSession")
            .setSessionActivity(pendingIntent)
            .build()

        if (customLayout.isNotEmpty()) {
            // Send custom layout to legacy
            mediaSession.setCustomLayout(customLayout)
        }
    }


    private fun getShuffleCommandButton(sessionCommand: SessionCommand): CommandButton {
        val isOn = sessionCommand.customAction == ButtonID.SHUFFLE_MODE_ON
        return CommandButton.Builder()
            .setDisplayName(
                getString(
                    if (isOn) R.string.exo_controls_shuffle_on_description
                    else R.string.exo_controls_shuffle_off_description
                )
            )
            .setSessionCommand(sessionCommand)
            .setIconResId(if (isOn) R.drawable.round_shuffle_on_24 else R.drawable.round_shuffle_off_24)
            .build()
    }

    companion object {
        private const val PACKAGE_NAME = "io.github.junkfood.heal"
    }

    private object ButtonID {
        const val SHUFFLE_MODE_ON = "$PACKAGE_NAME.SHUFFLE_ON"
        const val SHUFFLE_MODE_OFF = "$PACKAGE_NAME.SHUFFLE_OFF"
    }

}
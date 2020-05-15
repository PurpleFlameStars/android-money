package com.GoLemon.supplier.player.listener;

public abstract class PlayerStateListenerImpl implements PlayerStateListener {
    @Override
    public void onPlayStart() {

    }

    @Override
    public void OnPlayPaused() {

    }

    @Override
    public void OnPlayResume() {

    }

    @Override
    public void OnPlayStoped() {

    }

    @Override
    public void OnPlayComplete() {

    }

    @Override
    public void onFullScreenChange(boolean isfull) {

    }

    @Override
    public void OnVideoBufferStart() {

    }

    @Override
    public void OnVideoBuffering(int progress) {

    }

    @Override
    public void OnVideoBufferStop() {

    }
    @Override
    public void onVideoSizeChange(int width, int height) {

    }

    @Override
    public void onVideoError(int code, int extra) {

    }

    @Override
    public void onVideoError(int code, String url, String extra) {

    }

    @Override
    public void OnReplay() {

    }

    @Override
    public void OnPrepared(boolean value) {

    }
}

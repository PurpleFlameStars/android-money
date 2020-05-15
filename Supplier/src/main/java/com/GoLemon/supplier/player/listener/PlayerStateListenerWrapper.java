package com.GoLemon.supplier.player.listener;

public class PlayerStateListenerWrapper implements PlayerStateListener {
    private PlayerStateListener _inner;
    public PlayerStateListenerWrapper(PlayerStateListener listener){
        _inner=listener;
    }
    @Override
    public void onFullScreenChange(boolean isfull) {
        if(_inner!=null){
            _inner.onFullScreenChange(isfull);
        }
    }

    @Override
    public void OnVideoBufferStart() {
        if(_inner!=null){
            _inner.OnVideoBufferStart();
        }
    }

    @Override
    public void OnVideoBuffering(int progress) {
        if(_inner!=null){
            _inner.OnVideoBuffering(progress);
        }
    }

    @Override
    public void OnVideoBufferStop() {
        if(_inner!=null){
            _inner.OnVideoBufferStop();
        }
    }

    @Override
    public void onVideoSizeChange(int width, int height) {
        if(_inner!=null){
            _inner.onVideoSizeChange(width,height);
        }
    }

    @Override
    public void onVideoError(int code, int extra) {
        if(_inner!=null){
            _inner.onVideoError(code,extra);
        }
    }

    @Override
    public void onVideoError(int code, String url, String extra) {
        if(_inner!=null){
            _inner.onVideoError(code,url,extra);
        }
    }

    @Override
    public void onPlayStart() {
        if(_inner!=null){
            _inner.onPlayStart();
        }
    }

    @Override
    public void OnPlayPaused() {
        if(_inner!=null){
            _inner.OnPlayPaused();
        }
    }

    @Override
    public void OnPlayResume() {
        if(_inner!=null){
            _inner.OnPlayResume();
        }
    }

    @Override
    public void OnPlayStoped() {
        if(_inner!=null){
            _inner.OnPlayStoped();
        }
    }

    @Override
    public void OnPlayComplete() {
        if(_inner!=null){
            _inner.OnPlayComplete();
        }
    }

    @Override
    public void OnReplay() {
        if(_inner!=null){
            _inner.OnReplay();
        }
    }

    @Override
    public void OnPrepared(boolean value) {
        if(_inner!=null){
            _inner.OnPrepared(value);
        }
    }
}

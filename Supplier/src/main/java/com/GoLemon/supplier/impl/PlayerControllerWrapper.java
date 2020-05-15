package com.GoLemon.supplier.impl;

import android.content.Context;
import android.view.View;

import com.GoLemon.supplier.player.IPlayerController;
import com.GoLemon.supplier.player.IPlayerEntity;
import com.GoLemon.supplier.video.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class PlayerControllerWrapper implements IPlayerController {
    private IPlayerController _PrimerCtrl;
    private List<IPlayerController> _others;
    public PlayerControllerWrapper(IPlayerController ctrl){
        _others=new ArrayList<>();
        _PrimerCtrl =ctrl;
    }
    public void addController(IPlayerController ctrl){
        if(ctrl == null){
            return;
        }
        if(_others==null){
            _others=new ArrayList<>();
        } else if(_others.contains(ctrl)){
            return;
        }
        _others.add(ctrl);
    }
    public void removeController(IPlayerController ctrl){
        if(ctrl == null || _others==null || _others.isEmpty()){
            return;
        }
        if(!_others.contains(ctrl)){
            return;
        }
        _others.remove(ctrl);
    }

    @Override
    public void setPlayerEntity(IPlayerEntity entity) {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.setPlayerEntity(entity);
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.setPlayerEntity(entity);
            }
        }
    }

    @Override
    public void setVideo(VideoItem item) {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.setVideo(item);
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.setVideo(item);
            }
        }
    }

    @Override
    public void showCtrlPanel() {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.showCtrlPanel();
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.showCtrlPanel();
            }
        }
    }

    @Override
    public void showCtrlPanel(boolean show) {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.showCtrlPanel(show);
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.showCtrlPanel(show);
            }
        }
    }

    @Override
    public void onProgressChange(long position, long duration, int secondaryProgress) {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.onProgressChange(position,duration,secondaryProgress);
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.onProgressChange(position,duration,secondaryProgress);
            }
        }
    }

    @Override
    public void onFullScreenChange(boolean isfull) {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.onFullScreenChange(isfull);
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.onFullScreenChange(isfull);
            }
        }
    }

    @Override
    public boolean isPrePared() {
        if(_PrimerCtrl !=null){
            return _PrimerCtrl.isPrePared();
        }
        return false;
    }

    @Override
    public boolean OnReset() {
        if(_PrimerCtrl !=null){
            return _PrimerCtrl.OnReset();
        }
        return false;
    }

    @Override
    public View getLaoutView(Context cxt) {
        if(_PrimerCtrl !=null){
            return _PrimerCtrl.getLaoutView(cxt);
        }
        return null;
    }

    @Override
    public void onPlayStart() {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.onPlayStart();
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.onPlayStart();
            }
        }
    }

    @Override
    public void OnPlayPaused() {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.OnPlayPaused();
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.OnPlayPaused();
            }
        }
    }

    @Override
    public void OnPlayResume() {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.OnPlayResume();
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.OnPlayResume();
            }
        }
    }

    @Override
    public void OnPlayStoped() {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.OnPlayStoped();
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.OnPlayStoped();
            }
        }
    }

    @Override
    public void OnPlayComplete() {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.OnPlayComplete();
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.OnPlayComplete();
            }
        }
    }

    @Override
    public void OnReplay() {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.OnReplay();
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.OnReplay();
            }
        }
    }

    @Override
    public void OnPrepared(boolean value) {
        if(_PrimerCtrl !=null){
            _PrimerCtrl.OnPrepared(value);
        }
        if(_others == null || _others.isEmpty()){
            return;
        }
        for(IPlayerController ctrl:_others){
            if(ctrl !=null){
                ctrl.OnPrepared(value);
            }
        }
    }

}

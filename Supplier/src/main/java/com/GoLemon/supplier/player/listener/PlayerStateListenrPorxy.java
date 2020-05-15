package com.GoLemon.supplier.player.listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zheng on 2019/1/2.
 */

public class PlayerStateListenrPorxy implements PlayerStateListener {
    private List<PlayerStateListener> corelistener=new ArrayList<>();
    public boolean addCallback(PlayerStateListener callback){
        if(callback==null){
            return false;
        }
        if(corelistener.contains(callback)){
            return false;
        }
       return corelistener.add(callback);
    }
    public void removeCallback(PlayerStateListener callback){
        if(callback ==null){
            return;
        }
        if(corelistener.contains(callback)){
            corelistener.remove(callback);
        }
    }
    public void RemoveAllListener(){
        if(corelistener!=null){
            corelistener.clear();
        }
    }
    @Override
    public void onPlayStart() {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.onPlayStart();
        }
    }

    @Override
    public void OnPlayPaused() {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.OnPlayPaused();
        }

    }

    @Override
    public void OnPlayResume() {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.OnPlayResume();
        }
    }

    @Override
    public void OnPlayStoped() {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.OnPlayStoped();
        }
    }

    @Override
    public void OnPlayComplete() {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.OnPlayComplete();
        }
    }

    @Override
    public void OnReplay() {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.OnReplay();
        }
    }

    @Override
    public void OnPrepared(boolean value) {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.OnPrepared(value);
        }
    }

    @Override
    public void onFullScreenChange(boolean isfull) {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.onFullScreenChange(isfull);
        }
    }

    @Override
    public void OnVideoBufferStart() {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.OnVideoBufferStart();
        }
    }

    @Override
    public void OnVideoBuffering(int progress) {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.OnVideoBuffering(progress);
        }
    }

    @Override
    public void OnVideoBufferStop() {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.OnVideoBufferStop();
        }
    }

    @Override
    public void onVideoSizeChange(int width, int height) {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.onVideoSizeChange(width,height);
        }
    }

    @Override
    public void onVideoError(int code, int extra) {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.onVideoError(code,extra);
        }
    }
    @Override
    public void onVideoError(int code, String url, String extra) {
        if(corelistener.isEmpty()){
            return;
        }
        for(PlayerStateListener item:corelistener){
            item.onVideoError(code,url,extra);
        }
    }

}

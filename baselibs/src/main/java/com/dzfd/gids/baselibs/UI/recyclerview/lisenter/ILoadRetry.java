package com.dzfd.gids.baselibs.UI.recyclerview.lisenter;

import com.dzfd.gids.baselibs.network.HttpLoader;

public interface ILoadRetry {
    void OnRetryLoad(HttpLoader.HttpLoadPos loadPos, boolean Manually);
}

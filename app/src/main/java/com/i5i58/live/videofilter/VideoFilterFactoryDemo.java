package com.i5i58.live.videofilter;

import com.zego.zegoliveroom.videofilter.ZegoVideoFilter;
import com.zego.zegoliveroom.videofilter.ZegoVideoFilterFactory;

/**
 * Created by robotding on 16/12/3.
 */

public class VideoFilterFactoryDemo extends ZegoVideoFilterFactory {
    private int mode = 5;
    private ZegoVideoFilter mFilter = null;

    public ZegoVideoFilter create() {
        switch (mode) {
            case 0:
                mFilter = new VideoFilterMemDemo();
                break;
            case 1:
                mFilter = new VideoFilterSurfaceTextureDemo();
                break;
            case 2:
                mFilter = new VideoFilterHybridDemo();
                break;
            case 3:
                mFilter = new VideoFilterGlTexture2dDemo();
                break;
            case 4:
                mFilter = new VideoFilterSurfaceTextureDemo2();
                break;
            case 5:
                mFilter = new VideoFilterI420MemDemo();
                break;
        }

        return mFilter;
    }

    public void destroy(ZegoVideoFilter vf) {
        mFilter = null;
    }
}

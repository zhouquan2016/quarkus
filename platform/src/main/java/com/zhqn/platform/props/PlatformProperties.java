package com.zhqn.platform.props;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "platform")
public interface PlatformProperties {

    @WithDefault("/work/upload")
    String uploadPath();
}

package com.zhqn.shop.props;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "shop")
public interface ShopProperties {

    @WithDefault("/work/upload")
    String uploadPath();
}

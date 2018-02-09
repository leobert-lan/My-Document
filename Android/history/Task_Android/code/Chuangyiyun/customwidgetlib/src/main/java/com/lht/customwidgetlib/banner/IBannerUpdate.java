package com.lht.customwidgetlib.banner;

import android.widget.ImageView;

/**
 * @package com.lht.customwidgetlib.banner
 * @project AndroidBase
 * @classname IBannerUpdate
 * @description: 该接口将图片加载部分迁移出去，定义了更新的行为和规则，具体实现迁移出去避免了图片第三方库的直接依赖
 * Created by leobert on 2016/4/18.
 */
public interface IBannerUpdate {
    void UpdateImage(ImgRes<?> res, ImageView imageView);
}

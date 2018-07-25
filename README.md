# 一款用于在Android设备上获取（拍照或从相册中选择）图片的开源工具库

### 使用说明

    继承ImagePickerBaseActivity。
    通过takePhoto()使用相机拍照获取图片。
    通过getPictrue()从相册选择图片。
    通过setSelectDialog(View imagePickerView)是否同时支持拍照和选取相册图片。
    通过自定义控件ImagePickerLayout可设置图片数量、文字提示、是否可删除等属性。
    通过refreshPhotoContentView(ArrayList<ImageItem> mImageselectList) 刷新图片数据。


### 版本说明

    V1.0.0
        *支持通过相机拍照获取图片*
        *支持从相册选择图片*
        *支持Android7.0*

### GitHub地址： https://github.com/XieXiePro/AndroidImagePicker
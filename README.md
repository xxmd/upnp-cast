# UPNP-CAST

A example project for cast media file(Image, Video, Audio) from android device to windows computer by [UPNP]([Universal Plug and Play Device Architecture (openconnectivity.org)](https://openconnectivity.org/upnp-specs/UPnP-arch-DeviceArchitecture-v2.0-20200417.pdf)) protocol.

## Getting started

1. Add repository for download cling and jetty package.

   ```
   maven {
       allowInsecureProtocol(true)
       url "http://4thline.org/m2/"
   }
   ```

2. Implement dependencies.

   ```
   implementation 'org.fourthline.cling:cling-core:2.1.1'
   implementation 'me.shaohui:bottomdialog:1.1.9' // for device choose
   implementation 'io.github.xxmd:upnp-cast:1.0.0'
   ```

3. Add required permission, service and cast activity in **Androidminifaset.xml** file.

   ```xml
   <!--Required permission-->
   <uses-permission android:name="android.permission.INTERNET"/>
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
   <uses-permission android:name="android.permission.READ_PHONE_STATE" />
   <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
   <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   <uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE" />
   <uses-permission android:name="android.permission.WAKE_LOCK" />
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   
   <service android:name="org.fourthline.cling.android.AndroidUpnpServiceImpl"/>
   
   <!--Cast entry activity-->
   <activity android:name="io.github.xxmd.CastActivity"/>
   ```

4. Main code steps.

   ```java
   private void doAfterPermissionGranted(Runnable runnable, String... permission) {
       RxPermissions rxPermissions = new RxPermissions(getActivity());
       rxPermissions.request(permission)
               .subscribe(granted -> {
                   if (granted) {
                       runnable.run();
                   } else {
                       // show tips
                   }
               });
   }
   
   private void chooseDevice() {
       String[] requiredPermissions = {Manifest.permission.READ_PHONE_STATE,
               Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
               Manifest.permission.WAKE_LOCK,
               Manifest.permission.ACCESS_WIFI_STATE,
               Manifest.permission.ACCESS_FINE_LOCATION};
   
       doAfterPermissionGranted(() -> {
           SearchDeviceDialog searchDeviceDialog = new SearchDeviceDialog(selectedDevice);
           searchDeviceDialog.setSearchDeviceListener(new SearchDeviceDialog.SearchDeviceListener() {
               @Override
               public void onConfirm(Device device) {
                   // echo device info
               }
   
               @Override
               public void onCancel() {
   
               }
           });
           searchDeviceDialog.show(getActivity().getSupportFragmentManager());
       }, requiredPermissions);
   }
   
   private void chooseFile(Consumer<List<String>> onSuccess) {
       // implement by yourself, please make sure the result files all have same type(all image or all video or all audio).
   }
   
   /**
    * 1. check selectedDevice is existed.
    * 2. choose cast files.
    * 3. start cast activity.
    */
   private void chooseFileThenCast() {
       if (selectedDevice != null) {
           String[] fileRelatedPermission = {
               Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
   
           doAfterPermissionGranted(() -> {
               chooseFile(filePathList -> {
                   CastActivity.startCastActivity(mContext, selectedDevice, filePathList);
               });
           }, fileRelatedPermission);
       }
   }
   ```

5. Want to view more usage details please clone the project then run it.

## Usage

1. Start the **Windows Media Player** application on windows.

2. Make sure you have checked **two necessary options** when you first use.

   	- Allow control my player remotely.
   	- Allow auto play my media.  

   | ![](https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/Snipaste_2023-12-12_17-31-50.png) |
   | ------------------------------------------------------------ |

3. Run **upnp-cast** application, choose device firstly, then cast any media file by yourself.

4. You will see visualize result on compute screen when cast successfully.

## Screenshot


|                          Cast image                          |                          Cast video                          |                          Cast audio                          |
| :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![Audio Cast](https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/Screenshot_20231212_183306_io.github.xxmd.jpg) | ![Video Cast](https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/Screenshot_20231212_183329_io.github.xxmd.jpg) | ![Image Cast](https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/Screenshot_20231212_183339_io.github.xxmd.jpg) |

## Warning

Some compute cannot cast audio successfully due to lack audio output device(`headset`, `speaker`, etc...).

## Reference

1. [Upnp document](https://openconnectivity.org/upnp-specs/UPnP-arch-DeviceArchitecture-v2.0-20200417.pdf)
2. [Cling](https://github.com/4thline/cling)
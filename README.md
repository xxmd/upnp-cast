# UPNP-CAST

A example project for cast media file(Image, Video, Audio) from android device to windows computer by [UPNP]([Universal Plug and Play Device Architecture (openconnectivity.org)](https://openconnectivity.org/upnp-specs/UPnP-arch-DeviceArchitecture-v2.0-20200417.pdf)) protocol.

## Getting start

1. Start the **Windows Media Player** application on windows.

2. Make sure you have checked **two necessary options** when you first use.

   	- Allow control my player remotely.
   	- Allow auto play my media.  

   ![](https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/Snipaste_2023-12-12_17-31-50.png)  

3. Run **upnp-cast** application, choose device firstly, then cast any media file by yourself.
4. You will see visualize result on compute screen when cast successfully.

## Screenshot
```html
<div>
	<iamge src="https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/Screenshot_20231212_183339_io.github.xxmd.jpg"></iamge>  
	<iamge src="https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/Screenshot_20231212_183329_io.github.xxmd.jpg"></iamge> 
	<iamge src="https://wms-file-bucket.oss-cn-hangzhou.aliyuncs.com/Screenshot_20231212_183306_io.github.xxmd.jpg"></iamge>   
</div>
```

## Warning

Some compute cannot cast audio successfully due to lack audio output device(`headset`, `speaker`, etc...).

## Reference

1. [Upnp document](https://openconnectivity.org/upnp-specs/UPnP-arch-DeviceArchitecture-v2.0-20200417.pdf)
2. [Cling]([4thline/cling: UPnP/DLNA library for Java and Android (github.com)](https://github.com/4thline/cling))


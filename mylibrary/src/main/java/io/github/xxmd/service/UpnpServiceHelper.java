package io.github.xxmd.service;

import android.os.Build;
import android.util.Log;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;

import java.util.function.Consumer;

public class UpnpServiceHelper {
    /**
     * 设置播放链接
     * @param upnpService
     * @param device
     * @param uri
     */
    public static void setAVTransportURI(AndroidUpnpService upnpService, Device device, String uri, Consumer<ActionInvocation> onSuccess) {
        AVTransportService avTransportService = AVTransportService.getSingleton(device);
        ActionInvocation setURIInvocation = avTransportService.setAVTransportURI(uri);
        upnpService.getControlPoint().execute(new ActionCallback(setURIInvocation) {
            @Override
            public void success(ActionInvocation invocation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onSuccess.accept(invocation);
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                throw new RuntimeException(defaultMsg);
            }
        });
    }
    /**
     * 播放
     * @param upnpService
     * @param device
     */
    public static void play(AndroidUpnpService upnpService, Device device, Consumer<ActionInvocation> onSuccess) {
        AVTransportService avTransportService = AVTransportService.getSingleton(device);
        ActionInvocation invocation = avTransportService.play();
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onSuccess.accept(invocation);
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                // 还是要有失败重试机制
                play(upnpService, device, onSuccess);
            }
        });
    }

    public static void getPositionInfo(AndroidUpnpService upnpService, Device device, Consumer<String> onSuccess) {
        AVTransportService avTransportService = AVTransportService.getSingleton(device);
        ActionInvocation invocation = avTransportService.getPositionInfo();
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                System.out.println(invocation);
                String relTime = invocation.getOutput("RelTime").getValue().toString(); // 格式 h:mm:ss
                // 小时 h 为零就去除掉
                String zeroPrefix = "0:";
                if (relTime.startsWith(zeroPrefix)) {
                    relTime.replace(zeroPrefix, "");
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onSuccess.accept(relTime);
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                throw new RuntimeException(defaultMsg);
            }
        });
    }

    public static void getTransportInfo(AndroidUpnpService upnpService, Device device, Consumer<ActionInvocation> onSuccess) {
        AVTransportService avTransportService = AVTransportService.getSingleton(device);
        ActionInvocation invocation = avTransportService.getTransportInfo();
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onSuccess.accept(invocation);
                }
                System.out.println();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                throw new RuntimeException(defaultMsg);
            }
        });
    }

    public static void seek(AndroidUpnpService upnpService, Device device, String target) {
        AVTransportService avTransportService = AVTransportService.getSingleton(device);
        ActionInvocation invocation = avTransportService.seek(target);
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                System.out.println();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                seek(upnpService, device, target);
                throw new RuntimeException(defaultMsg);
            }
        });
    }

    public static void pause(AndroidUpnpService upnpService, Device device) {
        AVTransportService avTransportService = AVTransportService.getSingleton(device);
        ActionInvocation invocation = avTransportService.pause();
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                System.out.println();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                throw new RuntimeException(defaultMsg);
            }
        });
    }

    public static void stop(AndroidUpnpService upnpService, Device device) {
        AVTransportService avTransportService = AVTransportService.getSingleton(device);
        ActionInvocation invocation = avTransportService.stop();
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                System.out.println();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                throw new RuntimeException(defaultMsg);
            }
        });
    }

    public static void getVolume(AndroidUpnpService upnpService, Device device, Consumer<ActionInvocation> onSuccess) {
        RenderingControlService renderingControlService = RenderingControlService.getSingleton(device);
        ActionInvocation invocation = renderingControlService.getVolume();
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onSuccess.accept(invocation);
                }
                System.out.println();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                throw new RuntimeException(defaultMsg);
            }
        });
    }

    public static void setVolume(AndroidUpnpService upnpService, Device device, String desiredVolume) {
        RenderingControlService renderingControlService = RenderingControlService.getSingleton(device);
        ActionInvocation invocation = renderingControlService.setVolume(desiredVolume);
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                System.out.println();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                throw new RuntimeException(defaultMsg);
            }
        });
    }

    public static void getMute(AndroidUpnpService upnpService, Device device, Consumer<ActionInvocation> onSuccess) {
        RenderingControlService renderingControlService = RenderingControlService.getSingleton(device);
        ActionInvocation invocation = renderingControlService.getMute();
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onSuccess.accept(invocation);
                }
                System.out.println();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                throw new RuntimeException(defaultMsg);
            }
        });
    }

    public static void setMute(AndroidUpnpService upnpService, Device device, String desiredMute) {
        RenderingControlService renderingControlService = RenderingControlService.getSingleton(device);
        ActionInvocation invocation = renderingControlService.setMute(desiredMute);
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                System.out.println();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                throw new RuntimeException(defaultMsg);
            }
        });
    }

    public static void setNextAVTransportURI(AndroidUpnpService upnpService, Device device, String nextUri) {
        AVTransportService avTransportService = AVTransportService.getSingleton(device);
        ActionInvocation invocation = avTransportService.setNextAVTransportURI(nextUri);
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                System.out.println();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                setNextAVTransportURI(upnpService, device, nextUri);
                throw new RuntimeException(defaultMsg);
            }
        });
    }

    public static void getMediaInfo(AndroidUpnpService upnpService, Device device, Consumer<ActionInvocation> onSuccess) {
        AVTransportService avTransportService = AVTransportService.getSingleton(device);
        ActionInvocation invocation = avTransportService.getMediaInfo();
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onSuccess.accept(invocation);
                }
                System.out.println();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                throw new RuntimeException(defaultMsg);
            }
        });
    }

    public static void next(AndroidUpnpService upnpService, Device device) {
        AVTransportService avTransportService = AVTransportService.getSingleton(device);
        ActionInvocation invocation = avTransportService.next();
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                System.out.println();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                throw new RuntimeException(defaultMsg);
            }
        });
    }

    public static void previous(AndroidUpnpService upnpService, Device device) {
        AVTransportService avTransportService = AVTransportService.getSingleton(device);
        ActionInvocation invocation = avTransportService.previous();
        upnpService.getControlPoint().execute(new ActionCallback(invocation) {
            @Override
            public void success(ActionInvocation invocation) {
                Log.e("previous_success", invocation.toString());
                System.out.println();
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e("previous_failure", defaultMsg);
                previous(upnpService, device);
                throw new RuntimeException(defaultMsg);
            }
        });
    }
}
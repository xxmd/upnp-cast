package io.github.xxmd.service;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDAServiceId;

/**
 * upnp文档 http://upnp.org/specs/av/UPnP-av-RenderingControl-v1-Service.pdf
 */
public class RenderingControlService {
    public static final String SERVICE_ID = "RenderingControl";
    public static final String DEFAULT_INSTANCE_ID = "0";

    /**
     * The following channel spatial positions are defined:
     * • Master (Master)
     * • Left Front (LF)
     * • Right Front (RF)
     * • Center Front (CF)
     * • Low Frequency Enhancement (LFE) [Super woofer]
     * • Left Surround (LS)
     * • Right Surround (RS)
     * • Left of Center (LFC) [in front]
     * • Right of Center (RFC) [in front]
     * • Surround (SD) [rear]
     * • Side Left (SL) [left wall]
     * • Side Right (SR) [right wall]
     * • Top (T) [overhead]
     * • Bottom (B) [bottom]
     */
    public static final String DEFAULT_CHANEL = "Master";
    private final Service service;
    private static Device preDevice;

    private static RenderingControlService instance;
    private RenderingControlService(Device device) {
        ServiceId serviceId = new UDAServiceId(SERVICE_ID);
        service = device.findService(serviceId);
    }

    public static RenderingControlService getSingleton(Device device) {
        if (device.equals(preDevice) && instance != null) {
            return instance;
        }
        instance = new RenderingControlService(device);
        return instance;
    }

    /**
     * 页码 35页
     * This action retrieves the current value of the Volume state variable of the specified channel for the
     * specified instance of this service. The CurrentVolume (OUT) parameter contains a value ranging from 0 to
     * a device-specific maximum. See Section 2.2.16 (Volume) for more details,
     * 2.4.29.1.Arguments
     * Argument(s) Direction relatedStateVariable
     * InstanceID IN A_ARG_TYPE_InstanceID
     * Channel IN A_ARG_TYPE_Channel
     * CurrentVolume OUT Volume
     * @param instanceId
     * @param channel
     * @return
     */
    private ActionInvocation getVolume(String instanceId, String channel) {
        Action action = service.getAction("GetVolume");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        actionInvocation.setInput("Channel", channel);
        return actionInvocation;
    }

    public ActionInvocation getVolume() {
        return getVolume(DEFAULT_INSTANCE_ID, DEFAULT_CHANEL);
    }

    /**
     * 页码 35页
     * This action sets the Volume state variable of the specified Instance and Channel to the specified value. The
     * DesiredVolume input parameter contains a value ranging from 0 to a device-specific maximum. See
     * Section 2.2.16 (Volume) for more details,
     * 2.4.30.1.Arguments
     * Argument(s) Direction relatedStateVariable
     * InstanceID IN A_ARG_TYPE_InstanceID
     * Channel IN A_ARG_TYPE_Channel
     * DesiredVolume IN Volume
     * @param instanceId
     * @param channel
     * @param desiredVolume
     * @return
     */
    private ActionInvocation setVolume(String instanceId, String channel, String desiredVolume) {
        Action action = service.getAction("SetVolume");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        actionInvocation.setInput("Channel", channel);
        actionInvocation.setInput("DesiredVolume", desiredVolume);
        return actionInvocation;
    }

    public ActionInvocation setVolume(String desiredVolume) {
        return setVolume(DEFAULT_INSTANCE_ID, DEFAULT_CHANEL, desiredVolume);
    }

    /**
     * 页码 33页
     * This action retrieves the current value of the Mute setting of the channel for the specified instance of this service.
     * 2.4.27.1.Arguments
     * InstanceID IN A_ARG_TYPE_InstanceID
     * Channel IN A_ARG_TYPE_Channel
     * CurrentMute OUT Mute
     * @param instanceId
     * @param channel
     * @return
     */
    private ActionInvocation getMute(String instanceId, String channel) {
        Action action = service.getAction("GetMute");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        actionInvocation.setInput("Channel", channel);
        return actionInvocation;
    }

    public ActionInvocation getMute() {
        return getMute(DEFAULT_INSTANCE_ID, DEFAULT_CHANEL);
    }

    /**
     * 页码 35页
     * This action sets the Mute state variable of the specified instance of this service to the specified value.
     * 2.4.28.1.Arguments
     * InstanceID IN A_ARG_TYPE_InstanceID
     * Channel IN A_ARG_TYPE_Channel
     * DesiredMute IN Mute
     * @param instanceId
     * @param channel
     * @param desiredMute
     * @return
     */
    private ActionInvocation setMute(String instanceId, String channel, String desiredMute) {
        Action action = service.getAction("SetMute");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        actionInvocation.setInput("Channel", channel);
        actionInvocation.setInput("DesiredMute", desiredMute);
        return actionInvocation;
    }

    public ActionInvocation setMute(String desiredMute) {
        return setMute(DEFAULT_INSTANCE_ID, DEFAULT_CHANEL, desiredMute);
    }
}

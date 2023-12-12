package io.github.xxmd.service;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDAServiceId;

/**
 * upnp文档 http://www.upnp.org/specs/av/UPnP-av-AVTransport-v3-Service-20101231.pdf
 */
public class AVTransportService {
    public static final String SERVICE_ID = "AVTransport";
    public static final String DEFAULT_INSTANCE_ID = "0";
    public static final String DEFAULT_SPEED = "1";
    private final Service service;
    private static Device preDevice;

    private static AVTransportService instance;
    private AVTransportService(Device device) {
        ServiceId serviceId = new UDAServiceId(SERVICE_ID);
        service = device.findService(serviceId);
    }

    public static AVTransportService getSingleton(Device device) {
        if (device.equals(preDevice) && instance != null) {
            return instance;
        }
        instance = new AVTransportService(device);
        return instance;
    }

    /**
     * 所在页码 53页
     * Table 2-25: Arguments for SetAVTransportURI()
     * Argument Direction relatedStateVariable
     * 1. InstanceID IN A_ARG_TYPE_InstanceID
     * 2. CurrentURI IN AVTransportURI
     * 3. CurrentURIMetaData IN AVTransportURIMetaData
     * @param instanceId
     * @param currentURI
     */
    private ActionInvocation setAVTransportURI(String instanceId, String currentURI, String currentURIMetaData) {
        Action action = service.getAction("SetAVTransportURI");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        actionInvocation.setInput("CurrentURI", currentURI);
        actionInvocation.setInput("CurrentURIMetaData", currentURIMetaData);
        return actionInvocation;
    }

    public ActionInvocation setAVTransportURI(String currentURI) {
        // 这个 currentURIMetaData 理论上是可以传入 xml 字符串的，在 xml 中描述标题，作者，流派等信息,但是试过了没什么用，感兴趣的可以试一下
        return setAVTransportURI(DEFAULT_INSTANCE_ID, currentURI, "");
    }

    /**
     * 所在页码 62页
     * Table 2-43: Arguments for Play()
     * Argument Direction relatedStateVariable
     * 1. InstanceID IN A_ARG_TYPE_InstanceID
     * 2. Speed IN TransportPlaySpeed
     * @return
     */
    private ActionInvocation play(String instanceId, String speed) {
        Action action = service.getAction("Play");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        actionInvocation.setInput("Speed", speed);
        return actionInvocation;
    }

    public ActionInvocation play() {
        return play(DEFAULT_INSTANCE_ID, DEFAULT_SPEED);
    }

    /**
     * 所在页码 43页
     * Table 2-31: Arguments for GetPositionInfo()
     * Argument Direction relatedStateVariable
     * 1. InstanceID IN A_ARG_TYPE_InstanceID
     * Track OUT CurrentTrack
     * TrackDuration OUT CurrentTrackDuration
     * TrackMetaData OUT CurrentTrackMetaData
     * TrackURI OUT CurrentTrackURI
     * RelTime OUT RelativeTimePosition
     * AbsTime OUT AbsoluteTimePosition
     * RelCount OUT RelativeCounterPosition
     * AbsCount OUT AbsoluteCounterPosition
     * @param instanceId
     * @return
     */
    private ActionInvocation getPositionInfo(String instanceId) {
        Action action = service.getAction("GetPositionInfo");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        return actionInvocation;
    }

    public ActionInvocation getPositionInfo() {
        return getPositionInfo(DEFAULT_INSTANCE_ID);
    }

    /**
     * 所在页码 42页
     * Table 2-29: Arguments for GetTransportInfo()
     * Argument Direction relatedStateVariable
     * 1. InstanceID IN A_ARG_TYPE_InstanceID
     * AVTransport:2 Service Template Version 1.01 – Document Version 1.00 43
     * Copyright © 1999-2006, Contributing Members of the UPnPTM Forum. All rights Reserved.
     * Argument Direction relatedStateVariable
     * CurrentTransportState OUT TransportState
     * CurrentTransportStatus OUT TransportStatus
     * CurrentSpeed OUT TransportPlaySpeed
     * @param instanceId
     * @return
     */
    private ActionInvocation getTransportInfo(String instanceId) {
        Action action = service.getAction("GetTransportInfo");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        return actionInvocation;
    }

    public ActionInvocation getTransportInfo() {
        return getTransportInfo(DEFAULT_INSTANCE_ID);
    }

    /**
     * 所在页码 29页
     * Table 15: Arguments for Seek
     * AVTransport:1 Service Template Version 1.01 30
     * Argument Direction relatedStateVariable
     * InstanceID IN A_ARG_TYPE_InstanceI
     * Unit IN A_ARG_TYPE_SeekMod
     * Target IN A_ARG_TYPE_SeekTarg
     * @param instanceId
     * @return
     */
    private ActionInvocation getTransportInfo(String instanceId, String target) {
        Action action = service.getAction("seek");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        /**
         * Table 1.8: allowedValueList for A_ARG_TYPE_SeekMode
         * Value Req. or Opt.
         * “TRACK_NR” R
         * “ABS_TIME” O
         * “REL_TIME” O
         * “ABS_COUNT” O
         * “REL_COUNT” O
         * “CHANNEL_FREQ” O
         * “TAPE-INDEX” O
         * “FRAME” O
         */
        actionInvocation.setInput("Unit", "ABS_TIME");
        actionInvocation.setInput("Target", target);
        return actionInvocation;
    }


    /**
     * 所在页码 29页
     * Table 15: Arguments for Seek
     * AVTransport:1 Service Template Version 1.01 30
     * Argument Direction relatedStateVariable
     * InstanceID IN A_ARG_TYPE_InstanceI
     * Unit IN A_ARG_TYPE_SeekMod
     * Target IN A_ARG_TYPE_SeekTarg
     * @param instanceId
     * @return
     */
    private ActionInvocation seek(String instanceId, String unit, String target) {
        Action action = service.getAction("Seek");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        /**
         * Table 1.8: allowedValueList for A_ARG_TYPE_SeekMode
         * Value Req. or Opt.
         * “TRACK_NR” R
         * “ABS_TIME” O
         * “REL_TIME” O
         * “ABS_COUNT” O
         * “REL_COUNT” O
         * “CHANNEL_FREQ” O
         * “TAPE-INDEX” O
         * “FRAME” O
         */
        actionInvocation.setInput("Unit", unit);
        actionInvocation.setInput("Target", target);
        return actionInvocation;
    }

    public ActionInvocation seek(String target) {
        /**
         * 目前发现 windowMediaPlayer 只支持 REL_TIME 这种单位， 其他像 ABS_TIME，REL_COUNT，ABS_COUNT 是不支持的
         * 具体支不支持可以在 getPositionInfo 的返回 output 中看到，如果不支持返回的值就是 NOT_IMPLEMENTED 或者 -1
         * 这个 target 时间格式要求有点严格，只能是 h:mm:ss 或者 hh:mm:ss, 像 mm:ss 这种格式是无效的
          */
        return seek(DEFAULT_INSTANCE_ID, "REL_TIME", target);
    }

    /**
     * 所在页码 27页
     * Table 13: Arguments for Pause
     * Argument Direction relatedStateVariable
     * InstanceID IN A_ARG_TYPE_InstanceID
     */
    private ActionInvocation pause(String instanceId) {
        Action action = service.getAction("Pause");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        return actionInvocation;
    }

    public ActionInvocation pause() {
        return pause(DEFAULT_INSTANCE_ID);
    }

    /**
     * 所在页码 55页
     * Table 2-27: Arguments for SetNextAVTransportURI()
     * Argument Direction relatedStateVariable
     * InstanceID IN A_ARG_TYPE_InstanceID
     * NextURI IN NextAVTransportURI
     * NextURIMetaData IN NextAVTransportURIMetaData
     */
    private ActionInvocation setNextAVTransportURI(String instanceId, String nextUri, String nextUriMetaData) {
        Action action = service.getAction("SetNextAVTransportURI");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        actionInvocation.setInput("NextURI", nextUri);
        actionInvocation.setInput("NextURIMetaData", nextUriMetaData);
        return actionInvocation;
    }

    public ActionInvocation setNextAVTransportURI(String nextUri) {
        return setNextAVTransportURI(DEFAULT_INSTANCE_ID, nextUri, "");
    }

    /**
     * 所在页码 56页
     * This REQUIRED action returns information associated with the current media of the specified instance; it has no effect on state.
     * 2.4.3.1 Arguments
     * Table 2-29: Arguments for GetMediaInfo()
     * Argument Direction relatedStateVariable
     * InstanceID IN A_ARG_TYPE_InstanceID
     * NrTracks OUT NumberOfTracks
     * MediaDuration OUT CurrentMediaDuration
     * CurrentURI OUT AVTransportURI
     * CurrentURIMetaData OUT AVTransportURIMetaData
     * NextURI OUT NextAVTransportURI
     * NextURIMetaData OUT NextAVTransportURIMetaData
     * PlayMedium OUT PlaybackStorageMedium
     * RecordMedium OUT RecordStorageMedium
     * WriteStatus OUT RecordMediumWriteStatus
     */
    private ActionInvocation getMediaInfo(String instanceId) {
        Action action = service.getAction("GetMediaInfo");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        return actionInvocation;
    }

    public ActionInvocation getMediaInfo() {
        return getMediaInfo(DEFAULT_INSTANCE_ID);
    }

    /**
     * 所在页码 68页
     * This REQUIRED action is used to advance to the next track. This action is functionally equivalent to
     * Seek(“TRACK_NR”, “CurrentTrackNr+1”). This action does not cycle back to the first track.
     * 2.4.14.1 Arguments
     * Table 2-51: Arguments for Next()
     * Argument Direction relatedStateVariable
     * InstanceID IN A_ARG_TYPE_InstanceID
     */
    private ActionInvocation next(String instanceId) {
        Action action = service.getAction("Next");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        return actionInvocation;
    }

    public ActionInvocation next() {
        return next(DEFAULT_INSTANCE_ID);
    }

    /**
     * 所在页码 69页
     * This REQUIRED action is used to advance to the previous track. This action is functionally equivalent to
     * Seek(“TRACK_NR”, “CurrentTrackNr-1”). This action does not cycle back to the last track.
     * 2.4.15.1 Arguments
     * Table 2-53: Arguments for Previous()
     * Argument Direction relatedStateVariable
     * InstanceID IN A_ARG_TYPE_InstanceID
     */
    private ActionInvocation previous(String instanceId) {
        Action action = service.getAction("Previous");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        return actionInvocation;
    }

    public ActionInvocation previous() {
        return previous(DEFAULT_INSTANCE_ID);
    }



    /**
     * 所在页码 45页
     * Table 2-37: Arguments for Stop()
     * Argument Direction relatedStateVariable
     * InstanceID IN A_ARG_TYPE_InstanceID
     */
    private ActionInvocation stop(String instanceId) {
        Action action = service.getAction("Stop");
        ActionInvocation actionInvocation = new ActionInvocation<>(action);
        actionInvocation.setInput("InstanceID", instanceId);
        return actionInvocation;
    }

    public ActionInvocation stop() {
        return stop(DEFAULT_INSTANCE_ID);
    }
}

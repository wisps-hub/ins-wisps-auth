package com.wisps.auth.helper;

import com.wisps.notice.api.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoticeHelper {

    @DubboReference(version = "1.0.0")
    private NoticeService noticeService;

    public Boolean genSendSmsCaptcha(String mobile){
        return noticeService.genSendSmsCaptcha(mobile);
    }
}

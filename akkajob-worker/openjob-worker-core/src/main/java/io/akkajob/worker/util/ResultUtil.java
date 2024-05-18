package io.akkajob.worker.util;


import io.akkajob.common.constant.StatusEnum;
import io.akkajob.common.response.Result;

/**
 * @author stelin swoft@qq.com
 * @since 1.0.0
 */
public class ResultUtil {
    public static Boolean isSuccess(Result<?> result) {
        return StatusEnum.SUCCESS.getStatus().equals(result.getStatus());
    }
}


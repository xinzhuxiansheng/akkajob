package io.akkajob.common.util;

import io.akkajob.common.constant.StatusEnum;
import io.akkajob.common.response.Result;

public class ResultUtil {
    public static Boolean isSuccess(Result<?> result) {
        return StatusEnum.SUCCESS.getStatus().equals(result.getStatus());
    }
}

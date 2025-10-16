package org.stnhh.sdu_flea_market.utils;

import org.stnhh.sdu_flea_market.data.vo.Result;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<Result> build(Result result) {
        return ResponseEntity
                .status(result.httpStatus())
                .body(result);
    }
}

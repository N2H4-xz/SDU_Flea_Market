package org.stnhh.sdu_flea_market.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private Long total;
    private Integer page;
    private Integer limit;
    private List<T> items;
}


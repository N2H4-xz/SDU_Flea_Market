package org.stnhh.sdu_flea_market.data.vo.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private String comment_id;
    private String product_id;
    private AuthorInfo author;
    private String content;
    private LocalDateTime created_at;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private String user_id;
        private String nickname;
        private String avatar;
    }
}


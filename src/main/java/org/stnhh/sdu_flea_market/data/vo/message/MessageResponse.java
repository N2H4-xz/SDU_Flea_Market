package org.stnhh.sdu_flea_market.data.vo.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String message_id;
    private String sender_id;
    private String recipient_id;
    private String content;
    private LocalDateTime created_at;
}


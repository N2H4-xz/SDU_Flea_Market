package org.stnhh.sdu_flea_market.data.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("messages")
public class Message {
    @TableId(type = IdType.ASSIGN_UUID)
    private String messageId;

    private String senderId;
    private String recipientId;
    private String content;
    private Boolean isRead;
    private LocalDateTime readAt; // 阅读时间

    private LocalDateTime createdAt;
}


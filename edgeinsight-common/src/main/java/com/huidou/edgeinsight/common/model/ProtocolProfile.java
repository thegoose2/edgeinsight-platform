package com.huidou.edgeinsight.common.model;

import com.huidou.edgeinsight.common.model.base.AuditableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "protocol_profile")
public class ProtocolProfile extends AuditableEntity {

    @Id
    @Column(name = "profile_type")
    private String profileType;

    @Column(name = "protocol")
    private String protocol;

    @Column(name = "parser_id")
    private String parserId;

    @Column(name = "topic_patterns", columnDefinition = "JSON")
    private String topicPatterns;

    @Column(name = "msg_type_mapping", columnDefinition = "JSON")
    private String msgTypeMapping;

    @Column(name = "frame_strategy")
    private String frameStrategy;

    @Column(name = "description")
    private String description;
}

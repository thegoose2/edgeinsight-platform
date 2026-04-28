package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.ProtocolProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaProtocolProfileRepository extends JpaRepository<ProtocolProfile, Long> {

    Optional<ProtocolProfile> findByProtocolCode(String protocolCode);
}

package com.huidou.edgeinsight.core.repository.spi;

import com.huidou.edgeinsight.common.model.ProtocolProfile;

public interface ProtocolProfileRepository {

    ProtocolProfile save(ProtocolProfile profile);

    java.util.Optional<ProtocolProfile> findById(Long id);

    java.util.Optional<ProtocolProfile> findByProtocolCode(String protocolCode);

    java.util.List<ProtocolProfile> findAll();

    void deleteById(Long id);
}

package kr.hhplus.be.server.infrastructure.repository;

import java.util.UUID;

public interface UserRepository {
    UUID getUuidByPhone(String phoneNumber);
}

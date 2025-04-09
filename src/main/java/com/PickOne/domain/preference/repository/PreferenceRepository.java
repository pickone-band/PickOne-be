package com.PickOne.domain.preference.repository;

import com.PickOne.domain.preference.model.entity.Preference;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenceRepository extends JpaRepository<Preference, Long>, PreferenceRepositoryCustom {
    Optional<Preference> findByMemberUsername(String username);
    Optional<Preference> findByMemberId(Long memberId);

    List<Preference> findAll();
}

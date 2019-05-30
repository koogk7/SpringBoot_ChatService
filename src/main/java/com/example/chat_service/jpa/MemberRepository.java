package com.example.chat_service.jpa;

import com.example.chat_service.vo.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByName(String name);
    Optional<Member> findByEmailAndPassword(String email, String password);

    @Query("select m.id from Member m where m.name in (:names)")
    List<Object> getUidByName(@Param("names") Set<String> names);

}

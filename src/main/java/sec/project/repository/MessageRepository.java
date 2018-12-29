package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sec.project.domain.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryCustom {

    @Query(value = "select * from Message m where m.message like ? and m.sender like ?", nativeQuery = true)
    List<Message> searchMessage(String search, Long user);

    List<Message> findBySenderId(Long id);

    List<Message> findBySenderIdAndMessageContains(Long id, String search);
}
//entityManager.createNativeQuery("select * from Message m where m.message like "+ content).getResultList();

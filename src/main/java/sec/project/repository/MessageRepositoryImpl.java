package sec.project.repository;

import sec.project.domain.Message;
import javax.persistence.*;
import java.util.List;

public class MessageRepositoryImpl implements MessageRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Message> customMethod(String content){
        return entityManager.createNativeQuery("select * from Message m where m.message like '"+ content+"'", Message.class).getResultList();
    }
}

package sec.project.repository;

import sec.project.domain.Message;

import java.util.List;

public interface MessageRepositoryCustom {

    List<Message> customMethod(String content);
}

